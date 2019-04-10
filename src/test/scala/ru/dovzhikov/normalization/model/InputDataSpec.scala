package ru.dovzhikov.normalization.model

import org.scalatest._

import java.io.File

import org.scalatest.concurrent.{Futures, ScalaFutures}
import org.scalatest.time.{Millis, Seconds, Span}

class InputDataSpec extends FlatSpec with Matchers with Futures with ScalaFutures {

  implicit override val patienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(5, Millis))

  "downloadRar" should "work correctly" in {
    val rar = InputData.downloadRar()
    whenReady(rar) { res =>
      res.exists shouldBe true
      res.getName shouldEqual "ojdamage_rus.rar"
      val compFile = new File(getClass.getResource("/unrar-test/ojdamage_rus.rar").toURI)
      res.length shouldEqual compFile.length
    }
  }

  "extractXls" should "work correctly" in {
    val rar = new File(getClass.getResource("/unrar-test/ojdamage_rus.rar").toURI)
    val xls = InputData.extractXls(rar)
    whenReady(xls) { res =>
      res.exists shouldBe true
      res.getName shouldEqual "ojdamage_rus.xls"
      val compFile = new File(getClass.getResource("/xls-test/ojdamage_rus.xls").toURI)
      res.length shouldEqual compFile.length
    }
  }

  "getValues" should "work correctly" in {
    // Setup
    val inp = new File(getClass.getResource("/xls-test/ojdamage_rus.xls").toURI)
    //import scala.collection.JavaConverters._
    //val rows = new HSSFWorkbook(new FileInputStream(inp)).getSheetAt(0).rowIterator.asScala.toList
    val data = InputData.getValues(inp)
    data.filter(_.id == 7510).foreach(println)
    // TODO: finish
  }

}
