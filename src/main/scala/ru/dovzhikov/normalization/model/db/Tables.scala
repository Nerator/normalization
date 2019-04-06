package ru.dovzhikov.normalization.model.db

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */

object Tables extends {
  val profile = slick.jdbc.SQLiteProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(Врп.schema, ОпасныеЯвления.schema, ОпасныеЯвленияВОтраслях.schema, РазделыЭкономики.schema, СписокОпасныхЯвлений.schema, СубъектыРф.schema, СубъектыСведения.schema, СубъектыСоответствиеоя.schema, Факторысоцриска.schema, Факторысоцрисказначения.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Врп
   *  @param идсубъекта Database column ИДСубъекта SqlType(NCHAR), Length(4,false)
   *  @param версия Database column Версия SqlType(NCHAR), Length(1,false)
   *  @param отрасль Database column Отрасль SqlType(NCHAR), Length(2,false)
   *  @param год Database column Год SqlType(INT)
   *  @param врп Database column ВРП SqlType(FLOAT) */
  case class ВрпRow(идсубъекта: String, версия: String, отрасль: String, год: Int, врп: Option[Double])
  /** GetResult implicit for fetching ВрпRow objects using plain SQL queries */
  implicit def GetResultВрпRow(implicit e0: GR[String], e1: GR[Int], e2: GR[Option[Double]]): GR[ВрпRow] = GR{
    prs => import prs._
    ВрпRow.tupled((<<[String], <<[String], <<[String], <<[Int], <<?[Double]))
  }
  /** Table description of table ВРП. Objects of this class serve as prototypes for rows in queries. */
  class Врп(_tableTag: Tag) extends profile.api.Table[ВрпRow](_tableTag, "ВРП") {
    def * = (идсубъекта, версия, отрасль, год, врп) <> (ВрпRow.tupled, ВрпRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(идсубъекта), Rep.Some(версия), Rep.Some(отрасль), Rep.Some(год), врп).shaped.<>({r=>import r._; _1.map(_=> ВрпRow.tupled((_1.get, _2.get, _3.get, _4.get, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column ИДСубъекта SqlType(NCHAR), Length(4,false) */
    val идсубъекта: Rep[String] = column[String]("ИДСубъекта", O.Length(4,varying=false))
    /** Database column Версия SqlType(NCHAR), Length(1,false) */
    val версия: Rep[String] = column[String]("Версия", O.Length(1,varying=false))
    /** Database column Отрасль SqlType(NCHAR), Length(2,false) */
    val отрасль: Rep[String] = column[String]("Отрасль", O.Length(2,varying=false))
    /** Database column Год SqlType(INT) */
    val год: Rep[Int] = column[Int]("Год")
    /** Database column ВРП SqlType(FLOAT) */
    val врп: Rep[Option[Double]] = column[Option[Double]]("ВРП")

    /** Primary key of Врп (database name PK_ВРП) */
    val pk = primaryKey("PK_ВРП", (идсубъекта, версия, отрасль, год))

    /** Foreign key referencing РазделыЭкономики (database name Разделы_Экономики_FK_1) */
    lazy val разделыЭкономикиFk = foreignKey("Разделы_Экономики_FK_1", отрасль, РазделыЭкономики)(r => r.раздел, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing СубъектыРф (database name Субъекты_РФ_FK_2) */
    lazy val субъектыРфFk = foreignKey("Субъекты_РФ_FK_2", (идсубъекта, версия), СубъектыРф)(r => (r.идсубъекта, r.версия), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Врп */
  lazy val Врп = new TableQuery(tag => new Врп(tag))

  /** Entity class storing rows of table ОпасныеЯвления
   *  @param номер Database column Номер SqlType(FLOAT)
   *  @param датаНачала Database column Дата_Начала SqlType(DATETIME)
   *  @param датаОкончания Database column Дата_Окончания SqlType(DATETIME)
   *  @param количествооя Database column КоличествоОЯ SqlType(INT)
   *  @param заблаговременность Database column Заблаговременность SqlType(NVARCHAR), Length(255,false)
   *  @param номерЯвления Database column Номер_Явления SqlType(INT)
   *  @param интенсивностьЯвления Database column Интенсивность_явления SqlType(INT)
   *  @param идсубъекта Database column ИДСубъекта SqlType(NCHAR), Length(4,false)
   *  @param дополнение Database column Дополнение SqlType(NVARCHAR), Length(255,false) */
  case class ОпасныеЯвленияRow(номер: Double, датаНачала: String, датаОкончания: String, количествооя: Int, заблаговременность: Option[String], номерЯвления: Int, интенсивностьЯвления: Option[Int], идсубъекта: String, дополнение: Option[String])
  /** GetResult implicit for fetching ОпасныеЯвленияRow objects using plain SQL queries */
  implicit def GetResultОпасныеЯвленияRow(implicit e0: GR[Double], e1: GR[String], e2: GR[Int], e3: GR[Option[String]], e4: GR[Option[Int]]): GR[ОпасныеЯвленияRow] = GR{
    prs => import prs._
    ОпасныеЯвленияRow.tupled((<<[Double], <<[String], <<[String], <<[Int], <<?[String], <<[Int], <<?[Int], <<[String], <<?[String]))
  }
  /** Table description of table Опасные_Явления. Objects of this class serve as prototypes for rows in queries. */
  class ОпасныеЯвления(_tableTag: Tag) extends profile.api.Table[ОпасныеЯвленияRow](_tableTag, "Опасные_Явления") {
    def * = (номер, датаНачала, датаОкончания, количествооя, заблаговременность, номерЯвления, интенсивностьЯвления, идсубъекта, дополнение) <> (ОпасныеЯвленияRow.tupled, ОпасныеЯвленияRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(номер), Rep.Some(датаНачала), Rep.Some(датаОкончания), Rep.Some(количествооя), заблаговременность, Rep.Some(номерЯвления), интенсивностьЯвления, Rep.Some(идсубъекта), дополнение).shaped.<>({r=>import r._; _1.map(_=> ОпасныеЯвленияRow.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6.get, _7, _8.get, _9)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Номер SqlType(FLOAT) */
    val номер: Rep[Double] = column[Double]("Номер")
    /** Database column Дата_Начала SqlType(DATETIME) */
    val датаНачала: Rep[String] = column[String]("Дата_Начала")
    /** Database column Дата_Окончания SqlType(DATETIME) */
    val датаОкончания: Rep[String] = column[String]("Дата_Окончания")
    /** Database column КоличествоОЯ SqlType(INT) */
    val количествооя: Rep[Int] = column[Int]("КоличествоОЯ")
    /** Database column Заблаговременность SqlType(NVARCHAR), Length(255,false) */
    val заблаговременность: Rep[Option[String]] = column[Option[String]]("Заблаговременность", O.Length(255,varying=false))
    /** Database column Номер_Явления SqlType(INT) */
    val номерЯвления: Rep[Int] = column[Int]("Номер_Явления")
    /** Database column Интенсивность_явления SqlType(INT) */
    val интенсивностьЯвления: Rep[Option[Int]] = column[Option[Int]]("Интенсивность_явления")
    /** Database column ИДСубъекта SqlType(NCHAR), Length(4,false) */
    val идсубъекта: Rep[String] = column[String]("ИДСубъекта", O.Length(4,varying=false))
    /** Database column Дополнение SqlType(NVARCHAR), Length(255,false) */
    val дополнение: Rep[Option[String]] = column[Option[String]]("Дополнение", O.Length(255,varying=false))

    /** Foreign key referencing СписокОпасныхЯвлений (database name Список_Опасных_Явлений_FK_1) */
    lazy val списокОпасныхЯвленийFk = foreignKey("Список_Опасных_Явлений_FK_1", номерЯвления, СписокОпасныхЯвлений)(r => r.номерЯвления, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table ОпасныеЯвления */
  lazy val ОпасныеЯвления = new TableQuery(tag => new ОпасныеЯвления(tag))

  /** Entity class storing rows of table ОпасныеЯвленияВОтраслях
   *  @param буква Database column Буква SqlType(NCHAR), Length(2,false)
   *  @param номерявления Database column НомерЯвления SqlType(INT) */
  case class ОпасныеЯвленияВОтрасляхRow(буква: String, номерявления: Int)
  /** GetResult implicit for fetching ОпасныеЯвленияВОтрасляхRow objects using plain SQL queries */
  implicit def GetResultОпасныеЯвленияВОтрасляхRow(implicit e0: GR[String], e1: GR[Int]): GR[ОпасныеЯвленияВОтрасляхRow] = GR{
    prs => import prs._
    ОпасныеЯвленияВОтрасляхRow.tupled((<<[String], <<[Int]))
  }
  /** Table description of table Опасные_Явления_В_Отраслях. Objects of this class serve as prototypes for rows in queries. */
  class ОпасныеЯвленияВОтраслях(_tableTag: Tag) extends profile.api.Table[ОпасныеЯвленияВОтрасляхRow](_tableTag, "Опасные_Явления_В_Отраслях") {
    def * = (буква, номерявления) <> (ОпасныеЯвленияВОтрасляхRow.tupled, ОпасныеЯвленияВОтрасляхRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(буква), Rep.Some(номерявления)).shaped.<>({r=>import r._; _1.map(_=> ОпасныеЯвленияВОтрасляхRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Буква SqlType(NCHAR), Length(2,false) */
    val буква: Rep[String] = column[String]("Буква", O.Length(2,varying=false))
    /** Database column НомерЯвления SqlType(INT) */
    val номерявления: Rep[Int] = column[Int]("НомерЯвления")

    /** Primary key of ОпасныеЯвленияВОтраслях (database name PK_Опасные_Явления_В_Отраслях) */
    val pk = primaryKey("PK_Опасные_Явления_В_Отраслях", (буква, номерявления))

    /** Foreign key referencing РазделыЭкономики (database name Разделы_Экономики_FK_1) */
    lazy val разделыЭкономикиFk = foreignKey("Разделы_Экономики_FK_1", буква, РазделыЭкономики)(r => r.раздел, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing СписокОпасныхЯвлений (database name Список_Опасных_Явлений_FK_2) */
    lazy val списокОпасныхЯвленийFk = foreignKey("Список_Опасных_Явлений_FK_2", номерявления, СписокОпасныхЯвлений)(r => r.номерЯвления, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table ОпасныеЯвленияВОтраслях */
  lazy val ОпасныеЯвленияВОтраслях = new TableQuery(tag => new ОпасныеЯвленияВОтраслях(tag))

  /** Entity class storing rows of table РазделыЭкономики
   *  @param раздел Database column Раздел SqlType(NCHAR), PrimaryKey, Length(2,false)
   *  @param названиеРаздела Database column Название_Раздела SqlType(NVARCHAR), Length(75,false) */
  case class РазделыЭкономикиRow(раздел: String, названиеРаздела: String)
  /** GetResult implicit for fetching РазделыЭкономикиRow objects using plain SQL queries */
  implicit def GetResultРазделыЭкономикиRow(implicit e0: GR[String]): GR[РазделыЭкономикиRow] = GR{
    prs => import prs._
    РазделыЭкономикиRow.tupled((<<[String], <<[String]))
  }
  /** Table description of table Разделы_Экономики. Objects of this class serve as prototypes for rows in queries. */
  class РазделыЭкономики(_tableTag: Tag) extends profile.api.Table[РазделыЭкономикиRow](_tableTag, "Разделы_Экономики") {
    def * = (раздел, названиеРаздела) <> (РазделыЭкономикиRow.tupled, РазделыЭкономикиRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(раздел), Rep.Some(названиеРаздела)).shaped.<>({r=>import r._; _1.map(_=> РазделыЭкономикиRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Раздел SqlType(NCHAR), PrimaryKey, Length(2,false) */
    val раздел: Rep[String] = column[String]("Раздел", O.PrimaryKey, O.Length(2,varying=false))
    /** Database column Название_Раздела SqlType(NVARCHAR), Length(75,false) */
    val названиеРаздела: Rep[String] = column[String]("Название_Раздела", O.Length(75,varying=false))
  }
  /** Collection-like TableQuery object for table РазделыЭкономики */
  lazy val РазделыЭкономики = new TableQuery(tag => new РазделыЭкономики(tag))

  /** Entity class storing rows of table СписокОпасныхЯвлений
   *  @param номерЯвления Database column Номер_Явления SqlType(INT), PrimaryKey
   *  @param названиеОпасногоЯвления Database column Название_Опасного_Явления SqlType(NVARCHAR), Length(100,false)
   *  @param единицаизмерения Database column ЕдиницаИзмерения SqlType(NVARCHAR), Length(20,false)
   *  @param площадь Database column Площадь SqlType(FLOAT)
   *  @param коэффициентагрессивности Database column КоэффициентАгрессивности SqlType(FLOAT) */
  case class СписокОпасныхЯвленийRow(номерЯвления: Int, названиеОпасногоЯвления: String, единицаизмерения: Option[String], площадь: Option[Double], коэффициентагрессивности: Option[Double])
  /** GetResult implicit for fetching СписокОпасныхЯвленийRow objects using plain SQL queries */
  implicit def GetResultСписокОпасныхЯвленийRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]], e3: GR[Option[Double]]): GR[СписокОпасныхЯвленийRow] = GR{
    prs => import prs._
    СписокОпасныхЯвленийRow.tupled((<<[Int], <<[String], <<?[String], <<?[Double], <<?[Double]))
  }
  /** Table description of table Список_Опасных_Явлений. Objects of this class serve as prototypes for rows in queries. */
  class СписокОпасныхЯвлений(_tableTag: Tag) extends profile.api.Table[СписокОпасныхЯвленийRow](_tableTag, "Список_Опасных_Явлений") {
    def * = (номерЯвления, названиеОпасногоЯвления, единицаизмерения, площадь, коэффициентагрессивности) <> (СписокОпасныхЯвленийRow.tupled, СписокОпасныхЯвленийRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(номерЯвления), Rep.Some(названиеОпасногоЯвления), единицаизмерения, площадь, коэффициентагрессивности).shaped.<>({r=>import r._; _1.map(_=> СписокОпасныхЯвленийRow.tupled((_1.get, _2.get, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Номер_Явления SqlType(INT), PrimaryKey */
    val номерЯвления: Rep[Int] = column[Int]("Номер_Явления", O.PrimaryKey)
    /** Database column Название_Опасного_Явления SqlType(NVARCHAR), Length(100,false) */
    val названиеОпасногоЯвления: Rep[String] = column[String]("Название_Опасного_Явления", O.Length(100,varying=false))
    /** Database column ЕдиницаИзмерения SqlType(NVARCHAR), Length(20,false) */
    val единицаизмерения: Rep[Option[String]] = column[Option[String]]("ЕдиницаИзмерения", O.Length(20,varying=false))
    /** Database column Площадь SqlType(FLOAT) */
    val площадь: Rep[Option[Double]] = column[Option[Double]]("Площадь")
    /** Database column КоэффициентАгрессивности SqlType(FLOAT) */
    val коэффициентагрессивности: Rep[Option[Double]] = column[Option[Double]]("КоэффициентАгрессивности")
  }
  /** Collection-like TableQuery object for table СписокОпасныхЯвлений */
  lazy val СписокОпасныхЯвлений = new TableQuery(tag => new СписокОпасныхЯвлений(tag))

  /** Entity class storing rows of table СубъектыРф
   *  @param идсубъекта Database column ИДСубъекта SqlType(NCHAR), Length(4,false)
   *  @param версия Database column Версия SqlType(NCHAR), Length(1,false)
   *  @param назсубъекта Database column НазСубъекта SqlType(NVARCHAR), Length(255,false)
   *  @param дополнение Database column Дополнение SqlType(NVARCHAR), Length(255,false)
   *  @param с Database column с SqlType(NVARCHAR), Length(255,false)
   *  @param по Database column по SqlType(NVARCHAR), Length(255,false)
   *  @param уровень Database column Уровень SqlType(NCHAR), Length(1,false)
   *  @param входитВ Database column Входит_в SqlType(NCHAR), Length(4,false)
   *  @param входитВУр Database column Входит_в_ур SqlType(NCHAR), Length(1,false)
   *  @param исключения Database column Исключения SqlType(NCHAR), Length(1,false), Default(1) */
  case class СубъектыРфRow(идсубъекта: String, версия: String, назсубъекта: String, дополнение: Option[String], с: Option[String], по: Option[String], уровень: String, входитВ: Option[String], входитВУр: Option[String], исключения: String = "1")
  /** GetResult implicit for fetching СубъектыРфRow objects using plain SQL queries */
  implicit def GetResultСубъектыРфRow(implicit e0: GR[String], e1: GR[Option[String]]): GR[СубъектыРфRow] = GR{
    prs => import prs._
    СубъектыРфRow.tupled((<<[String], <<[String], <<[String], <<?[String], <<?[String], <<?[String], <<[String], <<?[String], <<?[String], <<[String]))
  }
  /** Table description of table Субъекты_РФ. Objects of this class serve as prototypes for rows in queries. */
  class СубъектыРф(_tableTag: Tag) extends profile.api.Table[СубъектыРфRow](_tableTag, "Субъекты_РФ") {
    def * = (идсубъекта, версия, назсубъекта, дополнение, с, по, уровень, входитВ, входитВУр, исключения) <> (СубъектыРфRow.tupled, СубъектыРфRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(идсубъекта), Rep.Some(версия), Rep.Some(назсубъекта), дополнение, с, по, Rep.Some(уровень), входитВ, входитВУр, Rep.Some(исключения)).shaped.<>({r=>import r._; _1.map(_=> СубъектыРфRow.tupled((_1.get, _2.get, _3.get, _4, _5, _6, _7.get, _8, _9, _10.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column ИДСубъекта SqlType(NCHAR), Length(4,false) */
    val идсубъекта: Rep[String] = column[String]("ИДСубъекта", O.Length(4,varying=false))
    /** Database column Версия SqlType(NCHAR), Length(1,false) */
    val версия: Rep[String] = column[String]("Версия", O.Length(1,varying=false))
    /** Database column НазСубъекта SqlType(NVARCHAR), Length(255,false) */
    val назсубъекта: Rep[String] = column[String]("НазСубъекта", O.Length(255,varying=false))
    /** Database column Дополнение SqlType(NVARCHAR), Length(255,false) */
    val дополнение: Rep[Option[String]] = column[Option[String]]("Дополнение", O.Length(255,varying=false))
    /** Database column с SqlType(NVARCHAR), Length(255,false) */
    val с: Rep[Option[String]] = column[Option[String]]("с", O.Length(255,varying=false))
    /** Database column по SqlType(NVARCHAR), Length(255,false) */
    val по: Rep[Option[String]] = column[Option[String]]("по", O.Length(255,varying=false))
    /** Database column Уровень SqlType(NCHAR), Length(1,false) */
    val уровень: Rep[String] = column[String]("Уровень", O.Length(1,varying=false))
    /** Database column Входит_в SqlType(NCHAR), Length(4,false) */
    val входитВ: Rep[Option[String]] = column[Option[String]]("Входит_в", O.Length(4,varying=false))
    /** Database column Входит_в_ур SqlType(NCHAR), Length(1,false) */
    val входитВУр: Rep[Option[String]] = column[Option[String]]("Входит_в_ур", O.Length(1,varying=false))
    /** Database column Исключения SqlType(NCHAR), Length(1,false), Default(1) */
    val исключения: Rep[String] = column[String]("Исключения", O.Length(1,varying=false), O.Default("1"))

    /** Primary key of СубъектыРф (database name RFkey) */
    val pk = primaryKey("RFkey", (идсубъекта, версия))
  }
  /** Collection-like TableQuery object for table СубъектыРф */
  lazy val СубъектыРф = new TableQuery(tag => new СубъектыРф(tag))

  /** Entity class storing rows of table СубъектыСведения
   *  @param идсубъекта Database column ИДСубъекта SqlType(NCHAR), Length(4,false)
   *  @param версия Database column Версия SqlType(NCHAR), Length(1,false)
   *  @param год Database column Год SqlType(SMALLINT)
   *  @param площадь Database column Площадь SqlType(FLOAT)
   *  @param население Database column Население SqlType(FLOAT)
   *  @param врп Database column ВРП SqlType(FLOAT) */
  case class СубъектыСведенияRow(идсубъекта: String, версия: String, год: Int, площадь: Option[Double], население: Option[Double], врп: Option[Double])
  /** GetResult implicit for fetching СубъектыСведенияRow objects using plain SQL queries */
  implicit def GetResultСубъектыСведенияRow(implicit e0: GR[String], e1: GR[Int], e2: GR[Option[Double]]): GR[СубъектыСведенияRow] = GR{
    prs => import prs._
    СубъектыСведенияRow.tupled((<<[String], <<[String], <<[Int], <<?[Double], <<?[Double], <<?[Double]))
  }
  /** Table description of table Субъекты_Сведения. Objects of this class serve as prototypes for rows in queries. */
  class СубъектыСведения(_tableTag: Tag) extends profile.api.Table[СубъектыСведенияRow](_tableTag, "Субъекты_Сведения") {
    def * = (идсубъекта, версия, год, площадь, население, врп) <> (СубъектыСведенияRow.tupled, СубъектыСведенияRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(идсубъекта), Rep.Some(версия), Rep.Some(год), площадь, население, врп).shaped.<>({r=>import r._; _1.map(_=> СубъектыСведенияRow.tupled((_1.get, _2.get, _3.get, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column ИДСубъекта SqlType(NCHAR), Length(4,false) */
    val идсубъекта: Rep[String] = column[String]("ИДСубъекта", O.Length(4,varying=false))
    /** Database column Версия SqlType(NCHAR), Length(1,false) */
    val версия: Rep[String] = column[String]("Версия", O.Length(1,varying=false))
    /** Database column Год SqlType(SMALLINT) */
    val год: Rep[Int] = column[Int]("Год")
    /** Database column Площадь SqlType(FLOAT) */
    val площадь: Rep[Option[Double]] = column[Option[Double]]("Площадь")
    /** Database column Население SqlType(FLOAT) */
    val население: Rep[Option[Double]] = column[Option[Double]]("Население")
    /** Database column ВРП SqlType(FLOAT) */
    val врп: Rep[Option[Double]] = column[Option[Double]]("ВРП")

    /** Primary key of СубъектыСведения (database name PK_Субъекты_Сведения) */
    val pk = primaryKey("PK_Субъекты_Сведения", (идсубъекта, версия, год))

    /** Foreign key referencing СубъектыРф (database name Субъекты_РФ_FK_1) */
    lazy val субъектыРфFk = foreignKey("Субъекты_РФ_FK_1", (идсубъекта, версия), СубъектыРф)(r => (r.идсубъекта, r.версия), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table СубъектыСведения */
  lazy val СубъектыСведения = new TableQuery(tag => new СубъектыСведения(tag))

  /** Entity class storing rows of table СубъектыСоответствиеоя
   *  @param идсоотв Database column ИДСоотв SqlType(INTEGER), PrimaryKey
   *  @param идсубъекта1 Database column ИДСубъекта1 SqlType(NCHAR), Length(4,false)
   *  @param версиясубъекта1 Database column ВерсияСубъекта1 SqlType(NCHAR), Length(1,false)
   *  @param назсубъекта1 Database column НазСубъекта1 SqlType(NVARCHAR), Length(255,false)
   *  @param началонаблюдений Database column НачалоНаблюдений SqlType(INT)
   *  @param идсубъекта2 Database column ИДСубъекта2 SqlType(NCHAR), Length(4,false)
   *  @param версиясубъекта2 Database column ВерсияСубъекта2 SqlType(NCHAR), Length(1,false)
   *  @param назсубъекта2 Database column НазСубъекта2 SqlType(NVARCHAR), Length(255,false)
   *  @param дополнительно2 Database column Дополнительно2 SqlType(NVARCHAR), Length(50,false) */
  case class СубъектыСоответствиеояRow(идсоотв: Int, идсубъекта1: String, версиясубъекта1: String, назсубъекта1: Option[String], началонаблюдений: Option[Int], идсубъекта2: String, версиясубъекта2: String, назсубъекта2: Option[String], дополнительно2: Option[String])
  /** GetResult implicit for fetching СубъектыСоответствиеояRow objects using plain SQL queries */
  implicit def GetResultСубъектыСоответствиеояRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]], e3: GR[Option[Int]]): GR[СубъектыСоответствиеояRow] = GR{
    prs => import prs._
    СубъектыСоответствиеояRow.tupled((<<[Int], <<[String], <<[String], <<?[String], <<?[Int], <<[String], <<[String], <<?[String], <<?[String]))
  }
  /** Table description of table Субъекты_соответствиеОЯ. Objects of this class serve as prototypes for rows in queries. */
  class СубъектыСоответствиеоя(_tableTag: Tag) extends profile.api.Table[СубъектыСоответствиеояRow](_tableTag, "Субъекты_соответствиеОЯ") {
    def * = (идсоотв, идсубъекта1, версиясубъекта1, назсубъекта1, началонаблюдений, идсубъекта2, версиясубъекта2, назсубъекта2, дополнительно2) <> (СубъектыСоответствиеояRow.tupled, СубъектыСоответствиеояRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(идсоотв), Rep.Some(идсубъекта1), Rep.Some(версиясубъекта1), назсубъекта1, началонаблюдений, Rep.Some(идсубъекта2), Rep.Some(версиясубъекта2), назсубъекта2, дополнительно2).shaped.<>({r=>import r._; _1.map(_=> СубъектыСоответствиеояRow.tupled((_1.get, _2.get, _3.get, _4, _5, _6.get, _7.get, _8, _9)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column ИДСоотв SqlType(INTEGER), PrimaryKey */
    val идсоотв: Rep[Int] = column[Int]("ИДСоотв", O.PrimaryKey)
    /** Database column ИДСубъекта1 SqlType(NCHAR), Length(4,false) */
    val идсубъекта1: Rep[String] = column[String]("ИДСубъекта1", O.Length(4,varying=false))
    /** Database column ВерсияСубъекта1 SqlType(NCHAR), Length(1,false) */
    val версиясубъекта1: Rep[String] = column[String]("ВерсияСубъекта1", O.Length(1,varying=false))
    /** Database column НазСубъекта1 SqlType(NVARCHAR), Length(255,false) */
    val назсубъекта1: Rep[Option[String]] = column[Option[String]]("НазСубъекта1", O.Length(255,varying=false))
    /** Database column НачалоНаблюдений SqlType(INT) */
    val началонаблюдений: Rep[Option[Int]] = column[Option[Int]]("НачалоНаблюдений")
    /** Database column ИДСубъекта2 SqlType(NCHAR), Length(4,false) */
    val идсубъекта2: Rep[String] = column[String]("ИДСубъекта2", O.Length(4,varying=false))
    /** Database column ВерсияСубъекта2 SqlType(NCHAR), Length(1,false) */
    val версиясубъекта2: Rep[String] = column[String]("ВерсияСубъекта2", O.Length(1,varying=false))
    /** Database column НазСубъекта2 SqlType(NVARCHAR), Length(255,false) */
    val назсубъекта2: Rep[Option[String]] = column[Option[String]]("НазСубъекта2", O.Length(255,varying=false))
    /** Database column Дополнительно2 SqlType(NVARCHAR), Length(50,false) */
    val дополнительно2: Rep[Option[String]] = column[Option[String]]("Дополнительно2", O.Length(50,varying=false))

    /** Foreign key referencing СубъектыРф (database name Субъекты_РФ_FK_1) */
    lazy val субъектыРфFk = foreignKey("Субъекты_РФ_FK_1", (идсубъекта2, идсубъекта1, версиясубъекта2, версиясубъекта1), СубъектыРф)(r => (r.идсубъекта, r.идсубъекта, r.версия, r.версия), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table СубъектыСоответствиеоя */
  lazy val СубъектыСоответствиеоя = new TableQuery(tag => new СубъектыСоответствиеоя(tag))

  /** Entity class storing rows of table Факторысоцриска
   *  @param кодфактора Database column КодФактора SqlType(NCHAR), PrimaryKey, Length(3,false)
   *  @param названиефактора Database column НазваниеФактора SqlType(NVARCHAR), Length(250,false)
   *  @param весфактора Database column ВесФактора SqlType(REAL), Default(1.0)
   *  @param едизмеренияфактора Database column ЕдИзмеренияФактора SqlType(NVARCHAR), Length(50,false)
   *  @param сокрназвфактора Database column СокрНазвФактора SqlType(NVARCHAR), Length(50,false)
   *  @param буквафактора Database column БукваФактора SqlType(NCHAR), Length(1,false), Default(z)
   *  @param применение Database column Применение SqlType(NCHAR), Length(8,false), Default(обратное)
   *  @param требуетсянормирование Database column ТребуетсяНормирование SqlType(NCHAR), Length(3,false), Default(да) */
  case class ФакторысоцрискаRow(кодфактора: String, названиефактора: String, весфактора: Double = 1.0, едизмеренияфактора: Option[String], сокрназвфактора: Option[String], буквафактора: String = "z", применение: String = "обратное", требуетсянормирование: String = "да")
  /** GetResult implicit for fetching ФакторысоцрискаRow objects using plain SQL queries */
  implicit def GetResultФакторысоцрискаRow(implicit e0: GR[String], e1: GR[Double], e2: GR[Option[String]]): GR[ФакторысоцрискаRow] = GR{
    prs => import prs._
    ФакторысоцрискаRow.tupled((<<[String], <<[String], <<[Double], <<?[String], <<?[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table ФакторыСоцРиска. Objects of this class serve as prototypes for rows in queries. */
  class Факторысоцриска(_tableTag: Tag) extends profile.api.Table[ФакторысоцрискаRow](_tableTag, "ФакторыСоцРиска") {
    def * = (кодфактора, названиефактора, весфактора, едизмеренияфактора, сокрназвфактора, буквафактора, применение, требуетсянормирование) <> (ФакторысоцрискаRow.tupled, ФакторысоцрискаRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(кодфактора), Rep.Some(названиефактора), Rep.Some(весфактора), едизмеренияфактора, сокрназвфактора, Rep.Some(буквафактора), Rep.Some(применение), Rep.Some(требуетсянормирование)).shaped.<>({r=>import r._; _1.map(_=> ФакторысоцрискаRow.tupled((_1.get, _2.get, _3.get, _4, _5, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column КодФактора SqlType(NCHAR), PrimaryKey, Length(3,false) */
    val кодфактора: Rep[String] = column[String]("КодФактора", O.PrimaryKey, O.Length(3,varying=false))
    /** Database column НазваниеФактора SqlType(NVARCHAR), Length(250,false) */
    val названиефактора: Rep[String] = column[String]("НазваниеФактора", O.Length(250,varying=false))
    /** Database column ВесФактора SqlType(REAL), Default(1.0) */
    val весфактора: Rep[Double] = column[Double]("ВесФактора", O.Default(1.0))
    /** Database column ЕдИзмеренияФактора SqlType(NVARCHAR), Length(50,false) */
    val едизмеренияфактора: Rep[Option[String]] = column[Option[String]]("ЕдИзмеренияФактора", O.Length(50,varying=false))
    /** Database column СокрНазвФактора SqlType(NVARCHAR), Length(50,false) */
    val сокрназвфактора: Rep[Option[String]] = column[Option[String]]("СокрНазвФактора", O.Length(50,varying=false))
    /** Database column БукваФактора SqlType(NCHAR), Length(1,false), Default(z) */
    val буквафактора: Rep[String] = column[String]("БукваФактора", O.Length(1,varying=false), O.Default("z"))
    /** Database column Применение SqlType(NCHAR), Length(8,false), Default(обратное) */
    val применение: Rep[String] = column[String]("Применение", O.Length(8,varying=false), O.Default("обратное"))
    /** Database column ТребуетсяНормирование SqlType(NCHAR), Length(3,false), Default(да) */
    val требуетсянормирование: Rep[String] = column[String]("ТребуетсяНормирование", O.Length(3,varying=false), O.Default("да"))
  }
  /** Collection-like TableQuery object for table Факторысоцриска */
  lazy val Факторысоцриска = new TableQuery(tag => new Факторысоцриска(tag))

  /** Entity class storing rows of table Факторысоцрисказначения
   *  @param идсубъекта Database column ИДСубъекта SqlType(NCHAR), Length(4,false)
   *  @param версия Database column Версия SqlType(NCHAR), Length(1,false)
   *  @param кодфактора Database column КодФактора SqlType(NCHAR), Length(3,false)
   *  @param значениефактора Database column ЗначениеФактора SqlType(FLOAT)
   *  @param год Database column Год SqlType(SMALLINT) */
  case class ФакторысоцрисказначенияRow(идсубъекта: String, версия: String, кодфактора: String, значениефактора: Double, год: Int)
  /** GetResult implicit for fetching ФакторысоцрисказначенияRow objects using plain SQL queries */
  implicit def GetResultФакторысоцрисказначенияRow(implicit e0: GR[String], e1: GR[Double], e2: GR[Int]): GR[ФакторысоцрисказначенияRow] = GR{
    prs => import prs._
    ФакторысоцрисказначенияRow.tupled((<<[String], <<[String], <<[String], <<[Double], <<[Int]))
  }
  /** Table description of table ФакторыСоцРискаЗначения. Objects of this class serve as prototypes for rows in queries. */
  class Факторысоцрисказначения(_tableTag: Tag) extends profile.api.Table[ФакторысоцрисказначенияRow](_tableTag, "ФакторыСоцРискаЗначения") {
    def * = (идсубъекта, версия, кодфактора, значениефактора, год) <> (ФакторысоцрисказначенияRow.tupled, ФакторысоцрисказначенияRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(идсубъекта), Rep.Some(версия), Rep.Some(кодфактора), Rep.Some(значениефактора), Rep.Some(год)).shaped.<>({r=>import r._; _1.map(_=> ФакторысоцрисказначенияRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column ИДСубъекта SqlType(NCHAR), Length(4,false) */
    val идсубъекта: Rep[String] = column[String]("ИДСубъекта", O.Length(4,varying=false))
    /** Database column Версия SqlType(NCHAR), Length(1,false) */
    val версия: Rep[String] = column[String]("Версия", O.Length(1,varying=false))
    /** Database column КодФактора SqlType(NCHAR), Length(3,false) */
    val кодфактора: Rep[String] = column[String]("КодФактора", O.Length(3,varying=false))
    /** Database column ЗначениеФактора SqlType(FLOAT) */
    val значениефактора: Rep[Double] = column[Double]("ЗначениеФактора")
    /** Database column Год SqlType(SMALLINT) */
    val год: Rep[Int] = column[Int]("Год")

    /** Primary key of Факторысоцрисказначения (database name PK_ФакторыСоцРискаЗначения_1) */
    val pk = primaryKey("PK_ФакторыСоцРискаЗначения_1", (идсубъекта, версия, кодфактора, год))

    /** Foreign key referencing СубъектыРф (database name Субъекты_РФ_FK_1) */
    lazy val субъектыРфFk = foreignKey("Субъекты_РФ_FK_1", (идсубъекта, версия), СубъектыРф)(r => (r.идсубъекта, r.версия), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Факторысоцриска (database name ФакторыСоцРиска_FK_2) */
    lazy val факторысоцрискаFk = foreignKey("ФакторыСоцРиска_FK_2", кодфактора, Факторысоцриска)(r => r.кодфактора, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Факторысоцрисказначения */
  lazy val Факторысоцрисказначения = new TableQuery(tag => new Факторысоцрисказначения(tag))

}
