package ru.dovzhikov.normalization.view.stages

import ru.dovzhikov.normalization.controller.UIUtil
import ru.dovzhikov.normalization.model.Normalization
import ru.dovzhikov.normalization.model.db.DBUtil
import ru.dovzhikov.normalization.view.stages.TableStage._

import scalafx.application.Platform
import scalafx.beans.property.ObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control.{TableColumn, TableView}
import scalafx.stage.Stage

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

class TableStage(db: DBUtil, mode: Mode) extends Stage {

  title = "Значения риска"
  onCloseRequest = _ => this.close()

  implicit val scalaFXExecutionContext: ExecutionContext =
    ExecutionContext.fromExecutor(command => Platform.runLater(command))

  private val results: Seq[RiskResult] = mode match {
    case Risk1 => Await.result(db.risk1, Duration("10 sec")).toSeq map {
      case (id, r) => Risk1Result(id, db.subjects.filter(id == _._1).head._2, r)
    }
    case Risk2Subj =>
      val subj = UIUtil.cd(db.subjects filter (_._3 > 2) map (_._2), "Выберите субъект")
        .showAndWait()
        .map(sn => db.subjects.filter(_._2 == sn).head._1)
      subj.fold[Seq[Risk2SubjResult]] {
        Seq.empty
      } { id =>
          Await.result(db.risk2, Duration("10 sec")).toSeq.filter(_._1._1 == id) map {
            case ((_, c), r) => Risk2SubjResult(c, db.letters.filter(_._1 == c).head._2, r)
          }
      }
    case Risk2Wing =>
      val wing = UIUtil.cd(db.letters map (_._2), "Выберите отрасль")
        .showAndWait()
        .map(wn => db.letters.filter(_._2 == wn).head._1)
      wing.fold[Seq[Risk2WingResult]] {
        Seq.empty
      } { char =>
        Await.result(db.risk2, Duration("10 sec")).toSeq.filter(_._1._2 == char) map {
          case ((i, _), r) => Risk2WingResult(i, db.subjects.filter(_._1 == i).head._2, r)
        }
      }
    case Risk2All =>
      Await.result(db.risk2, Duration("10 sec")).toSeq map {
        case ((i, c), r) => Risk2AllResult(i, db.subjects.filter(_._1 == i).head._2, c, db.letters.filter(_._1 == c).head._2, r)
      }
    case Risk3 =>
      val subj = UIUtil.cd(Normalization.methods, "Выберите способ нормирования")
        .showAndWait()
      subj.fold[Seq[Risk3Result]] {
        Seq.empty
      } { norm =>
        Await.result(db.risk3(norm.f), Duration("10 sec")).toSeq map {
          case (i, r) => Risk3Result(i, db.subjects.filter(_._1 == i).head._2, r)
        }
      }

  }

  private val table = mode match {
    case Risk1 =>
      new TableView[Risk1Result](ObservableBuffer(results.asInstanceOf[Seq[Risk1Result]])) {
        tableMenuButtonVisible = true
        columns ++= List(
          new TableColumn[Risk1Result, Int]("ИД Субъекта") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.id)
          },
          new TableColumn[Risk1Result, String]("Субъект") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.subj)
          },
          new TableColumn[Risk1Result, Double]("Риск") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.risk)
          }
        ) map (_.delegate) // без этого не работает
        sortOrder += columns.head
      }
    case Risk2Subj =>
      new TableView[Risk2SubjResult](ObservableBuffer(results.asInstanceOf[Seq[Risk2SubjResult]])) {
        tableMenuButtonVisible = true
        columns ++= List(
          new TableColumn[Risk2SubjResult, Char]("Буква отрасли") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.let)
          },
          new TableColumn[Risk2SubjResult, String]("Название отрасли") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.wing)
          },
          new TableColumn[Risk2SubjResult, Double]("Риск") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.risk)
          }
        ) map (_.delegate) // без этого не работает
        sortOrder += columns.head
      }
    case Risk2Wing =>
      new TableView[Risk2WingResult](ObservableBuffer(results.asInstanceOf[Seq[Risk2WingResult]])) {
        tableMenuButtonVisible = true
        columns ++= List(
          new TableColumn[Risk2WingResult, Int]("ИД Субъекта") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.id)
          },
          new TableColumn[Risk2WingResult, String]("Субъект") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.subj)
          },
          new TableColumn[Risk2WingResult, Double]("Риск") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.risk)
          }
        ) map (_.delegate) // без этого не работает
        sortOrder += columns.head
      }
    case Risk2All =>
      new TableView[Risk2AllResult](ObservableBuffer(results.asInstanceOf[Seq[Risk2AllResult]])) {
        tableMenuButtonVisible = true
        columns ++= List(
          new TableColumn[Risk2AllResult, Int]("ИД Субъекта") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.id)
          },
          new TableColumn[Risk2AllResult, String]("Субъект") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.subj)
          },
          new TableColumn[Risk2AllResult, Char]("Буква отрасли") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.let)
          },
          new TableColumn[Risk2AllResult, String]("Название отрасли") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.wing)
          },
          new TableColumn[Risk2AllResult, Double]("Риск") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.risk)
          }
        ) map (_.delegate) // без этого не работает
        sortOrder ++= List(columns.head, columns(2))
      }
    case Risk3 =>
      new TableView[Risk3Result](ObservableBuffer(results.asInstanceOf[Seq[Risk3Result]])) {
        tableMenuButtonVisible = true
        columns ++= List(
          new TableColumn[Risk3Result, Int]("ИД Субъекта") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.id)
          },
          new TableColumn[Risk3Result, String]("Субъект") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.subj)
          },
          new TableColumn[Risk3Result, Double]("Риск") {
            cellValueFactory = cdf => ObjectProperty(cdf.value.risk)
          }
        ) map (_.delegate) // без этого не работает
        sortOrder += columns.head
      }
  }

  scene = new Scene(table)

}

object TableStage {
  sealed trait Mode
  case object Risk1 extends Mode
  case object Risk2Subj extends Mode
  case object Risk2Wing extends Mode
  case object Risk2All extends Mode
  case object Risk3 extends Mode

  sealed trait RiskResult
  case class Risk1Result(id: Int, subj: String, risk: Double) extends RiskResult
  case class Risk2SubjResult(let: Char, wing: String, risk: Double) extends RiskResult
  case class Risk2WingResult(id: Int, subj: String, risk: Double) extends RiskResult
  case class Risk2AllResult(id: Int, subj: String, let: Char, wing: String, risk: Double) extends RiskResult
  case class Risk3Result(id: Int, subj: String, risk: Double) extends RiskResult
}