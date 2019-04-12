package ru.dovzhikov.normalization.model

import java.io.{File, FileInputStream}
import java.net.URL
import java.nio.file.{Files, Paths, StandardCopyOption}
import java.text.SimpleDateFormat
import java.util.Date

import com.github.junrar.Junrar
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.sys.process._

object InputData {

  private val defaultLink = "http://meteo.ru/component/docman/doc_download/502-massiv-dannykh-russkij?Itemid="
  private val tmpDir = new File(System.getProperty("java.io.tmpdir"))

  // Fix for strings in windows shell in Idea
  // Currently fixed with passing -Dfile.encoding=windows-1251 parameter to sbt
  // But may be will be useful later?

  // private def toUTF8(s: String) = scala.io.Source.fromBytes(s.getBytes, "UTF-8").mkString

  /**
    * Downloads RAR archive from meteo.ru to specified location, extracts XLS there and deletes RAR
    *
    * @param destination folder(!) to store file
    * @param link        download link to file
    * @return extracted XLS file
    */
  //  def getXLS(destination: File, link: String = defaultLink): File = {
  //    if (!destination.exists()) destination.mkdirs()
  //    if (!destination.isDirectory) throw new IllegalArgumentException("destination should be directory")
  //
  //    val url = new URL(link)
  //    val rar = new File(destination.getAbsolutePath + "/ojdamage_rus.rar")
  //
  //    (url #> rar).!!
  //
  //    Junrar.extract(rar, destination)
  //
  //    val farr = destination.listFiles(file => file.getName == "ojdamage_rus.xls")
  //    if (farr.length != 1) throw new Error("downloaded archive did not contain file ojdamage_rus.xls")
  //    rar.delete()
  //    farr.head
  //  }
  def downloadRar(destination: File = tmpDir, link: String = defaultLink): Future[File] = Future {
    if (!destination.exists()) destination.mkdirs()
    if (!destination.isDirectory) throw new IllegalArgumentException("destination should be directory")

    val url = new URL(link)
    val rar = new File(destination.getAbsolutePath + "/ojdamage_rus.rar")

    (url #> rar).!!

    // sanity check
    require(rar.exists)
    rar
  }

  def extractXls(rar: File, destination: File = tmpDir): Future[File] = Future {
    Junrar.extract(rar, destination)

    val farr = destination.listFiles(file => file.getName == "ojdamage_rus.xls")
    if (farr.length != 1) throw new Error("downloaded archive did not contain file ojdamage_rus.xls")
    rar.delete()
    farr.head
  }

  // Функции для работы с XLS

  /**
    * Получает список строк (за исключением заголовка) из XLS файла
    * @param xls файл XLS
    * @return список строк
    */
  def getRows(xls: File): List[Row] =
    new HSSFWorkbook(new FileInputStream(xls)).getSheetAt(0).rowIterator
      .asScala.toList.tail

  /**
    * Класс, для представления строк электронной таблицы
    * @param id номер наблюдения
    * @param startDate дата начала
    * @param endDate дата окончания
    * @param amount количество опасных явлений
    * @param earliness заблаговременность
    * @param name название опасного явления
    * @param intensivity интенсивность
    * @param subjName название субъекта
    * @param comment комментарий
    */
  case class XLSRow(id: Double, startDate: Date, endDate: Date, amount: Int,
                    earliness: String, name: String, intensivity: Int,
                    subjName: String, comment: String)

  // Get values from row
  def getValues(xls: File): List[XLSRow] = {
    getRows(xls).map(row => {
      val id = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue
//      try {
//        if (id < 6372 || id == 7119 && row.getCell(7).getStringCellValue == "Новгородская область" || id == 7510)
//          row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getDateCellValue.toString
//        else
//          row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue
//      } catch {
//        case e: IllegalStateException =>
//          println(row)
//          println(row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue.toInt)
//          println(row.getCell(7).getStringCellValue)
//          e.printStackTrace()
//      }

      XLSRow(id,
        if (id < 6372 || id == 7119 && row.getCell(7).getStringCellValue == "Новгородская область")
          row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getDateCellValue
        else
          new SimpleDateFormat("dd.MM.yyyy")
            .parse(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue),
        if (id < 6372 || id == 7119 && row.getCell(7).getStringCellValue == "Новгородская область" || id == 7510)
          row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getDateCellValue
        else
          new SimpleDateFormat("dd.MM.yyyy")
            .parse(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue),
        row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue.toInt,
        row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue,
        row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue,
        row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue.toInt,
        // В базе - ХМАО с добавлением "-Югра". Если поменять значение в базе, можно убрать блок
        {
          val subjName = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue
          if (subjName.startsWith("Ханты-Мансийский")) subjName + "-Югра"
          else subjName.replace('c', 'с') // замена английской с на русскую...
        },
        row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue
          .replace('c', 'с')) // замена английской с на русскую...
    })
  }

  def makeDBBackup(): Unit = {
    val db = new File("climate.db")
    require(db.exists)
    def bakName(number: Int = 0): String = {
      val name = s"climate.db.bak.$number"
      if (new File(name).exists())
        bakName(number+1)
      else
        name
    }
    val bname = bakName()
    val path = Files.copy(Paths.get("climate.db"), Paths.get(bname), StandardCopyOption.REPLACE_EXISTING)
    if (path == null)
      println("Error: could not copy")
    else
      println(s"Copied successfully to $bname")
  }

}
