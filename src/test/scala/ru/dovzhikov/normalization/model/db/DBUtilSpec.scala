package ru.dovzhikov.normalization.model.db

import java.io.File
import java.nio.file.{Files, Paths, StandardCopyOption}

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time._
import ru.dovzhikov.normalization.model.{InputData, Normalization}

class DBUtilSpec extends FlatSpec with Matchers with ScalaFutures {

  implicit override val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(10, Minutes), interval = Span(5, Millis))

  val dbFile = new File(getClass.getResource("/db-test/climate.db").toURI)
  val db = new DBUtil(dbFile)

  "subjIdByName" should "work correctly" in {
    whenReady(db.subjIdByName) { map =>
      map("Российская Федерация") shouldEqual "1000"
      map("Центральный федеральный округ") shouldEqual "1100"
      map("Белгородская область") shouldEqual "1101"
      map("Брянская область") shouldEqual "1102"
      map("Владимирская область") shouldEqual "1103"
    }
  }

  "oyaIdByName" should "work correctly" in {
    whenReady(db.oyaIdByName) { map =>
      map("Аномально высокая температура") shouldEqual 1
      map("Аномально низкая температура") shouldEqual 2
      map("Ветер") shouldEqual 3
      map("Высокий уровень рек") shouldEqual 4
      map("Гололед") shouldEqual 5
    }
  }

  "risk1" should "work correctly" in {
    whenReady(db.risk1) { map =>
      map(1101) shouldEqual 19775.61 +- 0.01
      map(1102) shouldEqual  4953.12 +- 0.01
      map(1103) shouldEqual  6790.63 +- 0.01
      map(1104) shouldEqual 27073.79 +- 0.01
      map(1105) shouldEqual  3190.81 +- 0.01
    }
  }

  "risk2" should "work correctly" in {
    whenReady(db.risk2) { map =>
      map((1101, 'А')) shouldEqual 589.843555995888 +- 1e-12
      map((1101, 'Б')) shouldEqual 0.0
      map((1101, 'В')) shouldEqual 288.209925174363 +- 1e-12
      map((1101, 'Г')) shouldEqual 182.230642556587 +- 1e-12
      map((1101, 'Д')) shouldEqual 39.3710775635253 +- 1e-12
    }
  }

  "risk3" should "work correctly" in {
    whenReady(db.risk3(Normalization.minmax)) { map =>
      map(1101) shouldEqual 19.686 +- 1e-3
      map(1102) shouldEqual 13.214 +- 1e-3
      map(1103) shouldEqual 13.801 +- 1e-3
      map(1104) shouldEqual 21.109 +- 1e-3
      map(1105) shouldEqual 13.21  +- 1e-3
    }
  }

  "addMissingRowsToDB" should "work correctly" in { // takes ~ 5 minutes...
    // copy db
    val in = Paths.get(dbFile.toURI)
    val out = Paths.get(dbFile.getAbsolutePath.replaceAll("climate.db", "climate-copy.db"))
    Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING)
    val testDB = new DBUtil(new File(getClass.getResource("/db-test/climate-copy.db").toURI))
    val rows = InputData.getValues(new File(getClass.getResource("/xls-test/ojdamage_rus.xls").toURI))
    whenReady(testDB.addMissingRowsToDB(rows)) { amount =>
      amount shouldEqual 2010
    }
    Files.delete(out)
  }

  "letters" should "work correctly" in {
    val let = db.letters
    let(0) shouldEqual (('А', "Сельское хозяйство, охота и лесное хозяйство"))
    let(1) shouldEqual (('Б', "Рыболовство, рыбоводство"))
    let(2) shouldEqual (('В', "Добыча полезных ископаемых"))
    let(3) shouldEqual (('Г', "Обрабатывающие производства"))
    let(4) shouldEqual (('Д', "Производство и распределение электроэнергии, газа и воды"))
  }

  "subjects" should "work correctly" in {
    val subj = db.subjects
    subj(0) shouldEqual ((1000, "Российская Федерация", 1))
    subj(1) shouldEqual ((1100, "Центральный федеральный округ", 2))
    subj(2) shouldEqual ((1101, "Белгородская область", 3))
    subj(3) shouldEqual ((1102, "Брянская область", 3))
    subj(4) shouldEqual ((1103, "Владимирская область", 3))
  }

}
