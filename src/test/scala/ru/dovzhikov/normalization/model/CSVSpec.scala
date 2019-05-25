package ru.dovzhikov.normalization.model

import java.io.File

import org.scalatest._

class CSVSpec extends FlatSpec with Matchers {

  private val EPS = 1E-9

  "CSV" should "parse csv with heading" in {
    val csv = new File(getClass.getResource("/csv-test/testcsv1.csv").toURI)
    val result = CSV(csv).parse
    result should have length 2
    result(0).header shouldEqual Some("header1")
    result(0).data should have length 3
    result(0).data(0) shouldEqual 1.0 +- EPS
    result(0).data(1) shouldEqual 2.0 +- EPS
    result(0).data(2) shouldEqual 3.0 +- EPS
    result(1).header shouldEqual Some("header2")
    result(1).data should have length 3
    result(1).data(0) shouldEqual 1.5 +- EPS
    result(1).data(1) shouldEqual 2.5 +- EPS
    result(1).data(2) shouldEqual 3.5 +- EPS
  }

  it should "parse csv without heading" in {
    val csv = new File(getClass.getResource("/csv-test/testcsv2.csv").toURI)
    val result = CSV(csv, header = false).parse
    result should have length 2
    result(0).header shouldBe empty
    result(0).data should have length 3
    result(0).data(0) shouldEqual 1.0 +- EPS
    result(0).data(1) shouldEqual 2.0 +- EPS
    result(0).data(2) shouldEqual 3.0 +- EPS
    result(1).header shouldBe empty
    result(1).data should have length 3
    result(1).data(0) shouldEqual 1.5 +- EPS
    result(1).data(1) shouldEqual 2.5 +- EPS
    result(1).data(2) shouldEqual 3.5 +- EPS
  }
}
