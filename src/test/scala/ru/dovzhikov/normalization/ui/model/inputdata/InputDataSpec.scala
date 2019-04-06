package ru.dovzhikov.normalization.ui.model.inputdata

import org.scalatest._
import java.io.{File, FileInputStream}

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import ru.dovzhikov.normalization.model.InputData

class InputDataSpec extends FlatSpec with Matchers {

  // Commented so test doesn't perform download every time test is run
  
//  "getXLS" should "work correctly" in {
//    // Setup
//    val root = new File(getClass.getResource("/").toURI)
//    val dest = new File(root.getAbsolutePath + "/down-test")
//    //dest.mkdir()
//    // Download and extract
//    val res = InputData.getXLS(dest)
//
//    // Check result
//    res.exists shouldBe true
//    res.getName shouldEqual "ojdamage_rus.xls"
//    val compFile = new File(getClass.getResource("/xls-test/ojdamage_rus.xls").toURI)
//    res.length shouldEqual compFile.length
//    // Cleanup
//    res.delete()
//    dest.delete()
//  }

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
