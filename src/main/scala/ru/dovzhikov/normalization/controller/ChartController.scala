package ru.dovzhikov.normalization.controller

import ru.dovzhikov.normalization.controller.UIUtil.RusAlert
import ru.dovzhikov.normalization.model.{CSV, Normalization}
import ru.dovzhikov.normalization.view.CSVDialog
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.chart.{LineChart, XYChart}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.stage.FileChooser
import scalafxml.core.macros.sfxml

import scala.util.{Failure, Success}

@sfxml
class ChartController(baseRB: RadioButton,
                      fileRB: RadioButton,
                      factOrColLabel: Label,
                      factOrColCB: ComboBox[String],
                      methodCB: ComboBox[Normalization.Method],
                      allMethodsCB: CheckBox,
                      fileButton: Button,
                      chart: LineChart[String, Double],
                      sourceLabel: Label,
                      modeLabel: Label) extends ControllerWithExtras {

  private var mode: Option[Mode] = None
  var currentCSV: Option[CSV] = None
  var currentCSVData: Option[Seq[CSV.Column]] = None
  var currentData: Option[Seq[(String, Double)]] = None

  // Инициализация статусной панели
  updateStatus()

  methodCB.items = ObservableBuffer(Normalization.methods)
  methodCB.selectionModel().selectFirst()

  // Обрабочики

  def closeMenuHandle(ae: ActionEvent): Unit = {
    stage.close()
  }

  def baseRBAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      new RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
      baseRB.selected = false
    } { db =>
      factOrColLabel.text = "Фактор"
      factOrColLabel.disable = false
      factOrColCB.items().clear()
      db.factorNames onComplete {
        case Success(seq) =>
          factOrColCB.items = ObservableBuffer(seq)
        case Failure(ex) => new RusAlert(AlertType.Error, s"Ошибка: ${ex.getMessage}").showAndWait()
      }
      factOrColCB.disable = false
      fileButton.disable = true
      mode = Some(Base)
    }
    updateStatus()
  }

  def fileRBAction(ae: ActionEvent): Unit = {
    factOrColLabel.text = "Столбец"
    factOrColLabel.disable = false
    factOrColCB.items().clear()
    for (csv <- currentCSV) {
      if (csv.header) factOrColCB.items = ObservableBuffer(csv.parse map (_.header.get))
      else factOrColCB.items = ObservableBuffer((1 to csv.parse.length).map(_.toString))
    }
    factOrColCB.disable = false
    fileButton.disable = false
    mode = Some(File)
    updateStatus()
  }

  def factOrColCBChanged(ae: ActionEvent): Unit = {
    for (value <- Option(factOrColCB.value())) {
      for (m <- mode) m match {
        case File =>
          for {
            c <- currentCSV
            d <- currentCSVData
          } {
            val col = if (c.header)
              (d find (_.header.get == value)).get
            else d(value.toInt - 1)
            println(col)
            currentData = Some(col.data.zipWithIndex map {
              case (db, i) => (i.toString, db)
            })
            updateChart()
          }
        case Base =>
          currentDB.fold[Unit] {
            new RusAlert(AlertType.Error, "База данных не загружена") {
              title = "Ошибка"
            }.showAndWait()
          } { db =>
            db.factorVals onComplete {
              case Success(seq) => {
                currentData = Some((seq filter (_._1 == value) map (_._2)).zipWithIndex map {
                  case (d,i) => (i.toString, d)
                })
                print(currentData.get.take(5))
                updateChart()
              }
              case Failure(ex) => new RusAlert(AlertType.Error, s"Ошибка: ${ex.getMessage}").showAndWait()
            }
          }
      }
    }
  }

  def methodCBChanged(ae: ActionEvent): Unit = {
    updateChart()
  }

  def allMethodsAction(ae: ActionEvent): Unit = {
    methodCB.disable = allMethodsCB.selected()
    updateChart()
//    if (allMethodsCB.selected()) {
//      methodCB.value = null
//      methodCB.disable = true
//    } else
//      methodCB.disable = false
  }

  def fileButtonAction(ae: ActionEvent): Unit = {
    val fileChooser = new FileChooser {
      title = "Открыть файл"
      extensionFilters += new FileChooser.ExtensionFilter("Файлы CSV", "*.csv")
    }
    val selectedFile = fileChooser.showOpenDialog(stage)

    val csvOpts = new CSVDialog().showAndWait()

    currentCSV = for {
      file <- Option(selectedFile)
      opts@CSV.CSVOptions(_,_,_,_) <- csvOpts
    } yield CSV(file, opts)
//    currentCSV = csvOpts.flatMap {
//      case c@CSV.CSVOptions(_,_,_,_) => Option(selectedFile).map(file => CSV(file, c))
//    }
    currentCSVData = currentCSV map (_.parse)

    for (csv <- currentCSV) {
      if (csv.header) factOrColCB.items = ObservableBuffer(csv.parse map (_.header.get))
      else factOrColCB.items = ObservableBuffer((1 to csv.parse.length).map(_.toString))
    }
    updateStatus()
  }

  // PRIVATE

  private def updateChart(): Unit = {
    chart.data().clear()
    if (allMethodsCB.selected()) {
      for (m <- Normalization.methods)
        for (data <- currentData) {
          val newData = m.f(data map (_._2))
          val dt = (data map (_._1), newData).zipped map {
            case (s, db) => XYChart.Data(s, db)
          }
          val series = new XYChart.Series[String, Double]() {
            name = m.name
            data = dt
          }
          chart.data() += series
        }
    } else {
      val m = methodCB.value()
      for (data <- currentData) {
        val newData = m.f(data map (_._2))
        val dt = (data map (_._1), newData).zipped map {
          case (s, db) => XYChart.Data(s, db)
        }
        val series = new XYChart.Series[String, Double]() {
          name = m.name
          data = dt
        }
        chart.data = series
      }
    }
  }

  private def updateStatus(): Unit = {
    modeLabel.text = mode.fold {
      "Режим: нет"
    } {
      case File => "Режим: файл"
      case Base => "Режим: база"
    }
    sourceLabel.text = mode.fold {
      ""
    } {
      case File => currentCSV.fold {
        "Текущий файл: нет"
      } { csv =>
        s"Текущий файл: ${csv.file.getName}"
      }
      case Base => currentDB.fold {
        "Текущая база: нет"
      } { db =>
        s"Текущая база: ${db.file.getName}"
      }
    }
  }

  private sealed trait Mode
  private final case object Base extends Mode
  private final case object File extends Mode

}
