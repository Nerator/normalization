package ru.dovzhikov.normalization.model.db

import java.io.File
import java.text.SimpleDateFormat

import ru.dovzhikov.normalization.model.InputData.XLSRow
import ru.dovzhikov.normalization.model.db.Tables._
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Модуль, содержащий операции по работе с базой данных
  */
class DBUtil(val file: File) {

  //private val db = Database.forConfig("climate")
  private val db = Database.forURL(s"jdbc:sqlite:${file.getAbsolutePath}", driver = "org.sqlite.JDBC", keepAliveConnection = true)

  /**
    * Отображение (Map) названий субъектов РФ в числовые идентификаторы
    * Тип идентификатора оставлен как String
    */
  lazy val subjIdByName: Future[Map[String, String]] = {
    val q = СубъектыРф.map(s => (s.назсубъекта, s.идсубъекта))
    db.run(q.result) map (Map(_: _*))
  }

  /**
    * Отображение (Map) названий опасных явлений в числовые идентификаторы
    */
  lazy val oyaIdByName: Future[Map[String, Int]] = {
    //val q = СубъектыРф.filter(r => (names contains r.назсубъекта).asColumnOf[Boolean]).map(_.идсубъекта)
    val q = СписокОпасныхЯвлений.map(o => (o.названиеОпасногоЯвления, o.номерЯвления))
    db.run(q.result) map (Map(_: _*))
  }

  /**
    * Отображение, необходимое для вычисления обобщённого индекса погодно-климатического
    * риска для экономики субъектов РФ
    */
  lazy val risk1: Future[Map[Int, Double]] = {
    val q1 =
      sql"""SELECT ИДСубъекта, [Повторяемость ОЯ в субъектеРФ], [Площадь ОЯ],
              [Средняя длительность (дней)], [Коэффициент агрессивности]
            FROM [1(1)_Повторяемость и продолжительность ОЯ по субъектам РФ]
         """.as[(String, Double, Double, Double, Double)]

    val q2 = for {
      ss <- СубъектыСведения
    } yield (ss.идсубъекта, ss.площадь, ss.врп)

    val areaVrp =
      for (qres <- db.run(q2.result))
        yield {
          val seq = for ((id, ar, v) <- qres)
            yield (id.toInt -> ar.get, id.toInt -> v.get)
          (Map(seq map (_._1): _*), Map(seq map (_._2): _*))
        }

    val sum = for {
      (areaMap, _) <- areaVrp
      q1res <- db.run(q1)
    } yield q1res.map {
      case (id, p, s, t, k) => (id.toInt, p * t * k * (if (s > areaMap(id.toInt)) 1.0 else s / areaMap(id.toInt)))
    }.groupBy(_._1) mapValues (_.map(_._2).sum)

    for {
      (_, vrpMap) <- areaVrp
      sumMap <- sum
    } yield Map((for {
      k <- sumMap.keys
    } yield (k, sumMap.getOrElse(k, 0.0) * vrpMap.getOrElse(k, 0.0))).toSeq: _*)
  }

  /**
    * Функция вычисления обобщённого индекса погодно-климатического риска для
    * экономики конкретного субъекта РФ
    *
    * @param id идентификатор субъекта РФ
    * @return индекс погодно-климатического риска для экономики субъекта РФ
    */
  def risk1ById(id: Int): Future[Double] =
    risk1 map (_(id))

  /**
    * Отображение, необходимое для вычисления доли риска для конкретного сектора
    * экономики субъекта РФ
    */
  lazy val risk2: Future[Map[(Int, Char), Double]] = {
    val q1 =
      sql"""SELECT ИДСубъекта, Буква, [Повторяемость ОЯ отрасли], [Площадь ОЯ],
              [Средняя длительность (дней)]
            FROM [2(1)_Повторяемость и продолжительность ОЯ по субъектам РФ и отраслям]
         """.as[(String, String, Double, Double, Double)]

    val q2 = for {
      vrp <- Врп if vrp.год === 2014
    } yield (vrp.идсубъекта, vrp.отрасль, vrp.врп)

    val q3 = for {
      ss <- СубъектыСведения
    } yield (ss.идсубъекта, ss.площадь)

    val vrp = db.run(q2.result) map (seq => Map(seq.map {
      case (id, ot, v) => ((id.toInt, ot.head), v.get)
    }: _*))
    val area = db.run(q3.result) map (seq => Map(seq map {
      case (id, v) => (id.toInt, v.get)
    }: _*))

    val sum = for {
      //vrpmap <- vrp //TODO: Look into why not used
      areamap <- area
      q1vec <- db.run(q1)
    } yield q1vec map {
      case (id, ot, p, s, t) => ((id.toInt, ot.head), p * t * (if (s > areamap(id.toInt)) 1.0 else s / areamap(id.toInt)))
    } groupBy(_._1) map {
      case (k, vec) => (k, vec.map(_._2).sum)
    }

    for {
      vrpmap <- vrp
      summap <- sum
    } yield Map((for {
      k <- summap.keys
    } yield (k, summap.getOrElse(k, 0.0) * vrpmap.getOrElse(k, 0.0))).toSeq: _*)
  }

  /**
    * Функция, вычисляющая долю риска для конкретного сектора экономики субъекта
    * РФ.
    *
    * @param id идентификатор субъекта
    * @param sector буква, обозначающая сектор экономики
    * @return доля риска для конкретного сектора экономики субъекта РФ.
    */
  def risk2ById(id: Int, sector: Char): Future[Double] =
    risk2 map (_((id, sector)))

  /**
    * Отображение, необходимое для вычисления индекса погодно-климатического
    * риска для социальной сферы субъекта РФ.
    * @param norm нормирующая функция
    * @return отображение "ид субъекта -> социальный риск"
    */
  def risk3(norm: Seq[Double] => Seq[Double]): Future[Map[Int, Double]] = {
    // Get subjects' area
    val q1 = for {
      ss <- СубъектыСведения
    } yield (ss.идсубъекта, ss.площадь)

    val area = db.run(q1.result) map (seq => Map(seq map {
      case (sid, a) => (sid.toInt, a.getOrElse(0.0))
    }: _*))

    // Get sum parameters and sum value
    val q2 =
      sql"""SELECT ИДСубъекта, [Повторяемость ОЯ в субъектеРФ], [Площадь ОЯ],
              [Средняя длительность (дней)], [Коэффициент агрессивности]
            FROM [1(1)_Повторяемость и продолжительность ОЯ по субъектам РФ]
         """.as[(String, Double, Double, Double, Double)]

    val sum = for {
      areaMap <- area
      q2vec <- db.run(q2)
    } yield q2vec map {
      case (id, p, s, t, k) => (id.toInt, p * t * k * (if (s > areaMap(id.toInt)) 1.0 else s / areaMap(id.toInt)))
    } groupBy (_._1) map {
      case (id, vec) => (id, vec.map(_._2).sum)
    }

    // Factors

    // Get factors values for each subject
    val q3 = for {
      fs <- Факторысоцрисказначения
    } yield (fs.идсубъекта, fs.кодфактора, fs.значениефактора)

    val idfacts = db.run(q3.result) map (seq => Map(seq map {
      case (i, f, v) => ((i.toInt, f), v)
    }: _*))

    // Group values by factor
    val factsValues = idfacts map (_ groupBy(_._1._2) mapValues (_.map {
      case ((i, _), v) => (i, v)
    }))

    // Normalize values according to passed function
    val normalizedValues = factsValues map (_ mapValues(idval =>
      Map(idval.keys.toList zip norm(idval.values.toList): _*)))

    // Calculate factors sum
    val factPart = for {
      idfactsmap <- idfacts
      normValMap <- normalizedValues
    } yield idfactsmap map {
      case ((id, _), _) => (id, 0.2 * normValMap("ф01")(id) +
        0.2 * (1 - normValMap("ф02")(id)) +
        0.2 * normValMap("ф03")(id) +
        0.2 * (1 - normValMap("ф04")(id)) +
        0.2 * (1 - normValMap("ф05")(id)))
    }

    for {
      sumMap <- sum
      factPartMap <- factPart
    } yield Map((for {
      k <- sumMap.keys
    } yield (k, sumMap.getOrElse(k, 0.0) * factPartMap.getOrElse(k, 0.0))).toSeq: _*)
  }

  /**
    * Функция, вычисляющая социальный риск для конкретного субъекта РФ
    * @param norm функция нормирования
    * @param id идентификатор субъекта РФ
    * @return значение социального риска для субъекта РФ
    */
  def risk3ById(norm: Seq[Double] => Seq[Double], id: Int): Future[Double] =
    risk3(norm) map (_(id))

  /**
    * Добавление недостающих строк в базу данных
    * @param rows список строк, подлежащих добавлению в базу
    * @return
    */
  def addMissingRowsToDB(rows: List[XLSRow]): Future[Int] = {
    // Query to get keys
    val keysQ = for {
      oya <- ОпасныеЯвления
      oyalist <- СписокОпасныхЯвлений if oya.номерЯвления === oyalist.номерЯвления
      subj <- СубъектыРф if oya.идсубъекта === subj.идсубъекта
    } yield (oya.номер, oyalist.названиеОпасногоЯвления, subj.назсубъекта)

    val keysF = db.run(keysQ.result) map (_ map {
      case (id, oname, sname) => (id.toInt, oname, sname)
    })

    val rowsAreContained = keysF map (seq => {
      println(s"total: ${rows.length} rows")
      println(s"in seq: ${seq.length} rows")
      val toadd = rows filterNot (xlsrow =>
        seq.contains((xlsrow.id.toInt, xlsrow.name, xlsrow.subjName))
        )
      println(s"going to add ${toadd.length}") //TODO: 22471 all the time
      toadd
    })

    val actionsList = for {
      list <- rowsAreContained
      oyamap <- oyaIdByName
      subjmap <- subjIdByName
    } yield list map (xlsrow =>
        ОпасныеЯвления += ОпасныеЯвленияRow(
          xlsrow.id,
          new SimpleDateFormat("dd.MM.YYYY").format(xlsrow.startDate), // датаначала String
          new SimpleDateFormat("dd.MM.YYYY").format(xlsrow.endDate), // датаокончания String
          xlsrow.amount,
          Some(xlsrow.earliness),
          oyamap(xlsrow.name), // номерявления: Int
          Some(xlsrow.intensivity),
          subjmap(xlsrow.subjName).toString, // идсубъекта: String
          Some(xlsrow.comment)
        ))

    println("got future")

    actionsList flatMap (dbActions => db.run(DBIO.sequence(dbActions))) map (_.sum)
  }

  lazy val letters: Seq[(Char, String)] = {
    val q = РазделыЭкономики map (v => (v.раздел, v.названиеРаздела)) sortBy (_._1)
    Await.result(db.run(q.result) map (_ map { case (l, n) => (l.head, n) }), Duration("10 sec"))
  }
  //  lazy val letters: Future[Seq[(String, String)]] = {
  //    val q = РазделыЭкономики map (v => (v.раздел, v.названиеРаздела)) sortBy (_._1)
  //    db.run(q.result) map (_ map { case (l, n) => (l.take(1), n) })
  //  }
  lazy val subjects: Seq[(Int, String, Int)] = {
    val q = СубъектыРф map (v => (v.идсубъекта, v.назсубъекта, v.уровень)) sortBy (_._1)
    Await.result(db.run(q.result) map (_ map { case (i, n, l) => (i.toInt, n, l.toInt)}), Duration("10 sec"))
  }
  //  lazy val subjects: Future[Seq[(String, String)]] = {
  //    val q = СубъектыРф map(v => (v.идсубъекта, v.назсубъекта)) sortBy(_._1)
  //    db.run(q.result)
  //  }

  lazy val factorNames: Future[Seq[String]] = {
    val q = Факторысоцриска map (_.названиефактора)
    db.run(q.result)
  }

  lazy val factorVals: Future[Seq[(String,Double)]] = {
    val q = for {
      fs <- Факторысоцриска
      fszs <- Факторысоцрисказначения if fszs.кодфактора === fs.кодфактора
    } yield (fs.названиефактора, fszs.значениефактора)
    db.run(q.result)
  }

}
