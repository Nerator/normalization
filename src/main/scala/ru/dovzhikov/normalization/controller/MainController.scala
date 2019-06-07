package ru.dovzhikov.normalization.controller

import java.io.File

import ru.dovzhikov.normalization.controller.UIUtil.RusAlert
import ru.dovzhikov.normalization.model.InputData
import ru.dovzhikov.normalization.model.db.DBUtil
import ru.dovzhikov.normalization.view.{ChartStage, ResultDialog, Risk2Dialog, Risk3Dialog, TableStage}
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Label}
import scalafx.stage.FileChooser
import scalafxml.core.macros.sfxml

import scala.util.{Failure, Success}

@sfxml
class MainController(statusLabel: Label) extends ControllerWithExtras {

  // Initialize status bar
  updateStatus()

  // File menu handlers

  def openDBMenuAction(ae: ActionEvent): Unit = {
    val selectedFile = new FileChooser {
      title = "Открыть базу данных"
      extensionFilters += new FileChooser.ExtensionFilter("Файлы баз данных", "*.db")
    }.showOpenDialog(stage)
    if (selectedFile != null)
      currentDB = for (db <- Option(selectedFile)) yield new DBUtil(db)
    updateStatus()
  }

  def closeMenuAction(ae: ActionEvent): Unit = {
    println(s"java version: ${System.getProperty("java.version")}")
    println(s"javafx version: ${System.getProperty("javafx.version")}")
    Platform.exit()
  }

  // Data menu handlers

  def importFileMenuAction(ae: ActionEvent): Unit = {
    val fileChooser = new FileChooser {
      title = "Открыть таблицу"
      extensionFilters += new FileChooser.ExtensionFilter("Файлы XLS", "*.xls")
    }
    val xls = fileChooser.showOpenDialog(stage)
    if (xls != null) processXLS(xls)
  }

  def importInternetMenuAction(ae: ActionEvent): Unit = {
    println("Hi!")
    val al1 = UIUtil.al("загрузка...")
    val al2 = UIUtil.al("извлечение...")

    al1.show()
    for {
      rar <- InputData.downloadRar()
      _ = al1.close()
      _ = al2.show()
      xls <- InputData.extractXls(rar)
      _ = al2.close()
    } processXLS(xls)
  }

  // Risk menu handlers

  def risk1AllMenuAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      new RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      new TableStage(db, TableStage.Risk1).show()
    }
  }

  def risk1SubjMenuAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      new RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      val selection = UIUtil.cd(db.subjects filter { case (_,_,l) => l > 2 } map (_._2), "Выберите субъект")
        .showAndWait()
        .map(sn => db.subjects.filter(t => t._2 == sn).head._1.toInt)
      selection foreach (s => {
        UIUtil.qal.show()
        db.risk1ById(s) andThen {
          case _ => UIUtil.qal.close()
        } onComplete {
          case Success(risk) =>
            new ResultDialog(risk).showAndWait()
          case Failure(ex) =>
            new RusAlert(AlertType.Error, s"Ошибка: ${ex.getMessage}").showAndWait()
        }
      })
    }
  }

  def risk2AllMenuAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      new RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      new TableStage(db, TableStage.Risk2All).show()
    }
  }

  def risk2SubjMenuAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      new RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      new TableStage(db, TableStage.Risk2Subj).show()
    }
  }

  def risk2WingMenuAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      new RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      new TableStage(db, TableStage.Risk2Wing).show()
    }
  }

  def risk2SubjWingMenuAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      new RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      val sl = new Risk2Dialog(db).showAndWait()
      sl foreach { case Risk2Dialog.Risk2Input(s, l) =>
        UIUtil.qal.show()
        db.risk2ById(s, l) andThen {
          case _ => UIUtil.qal.close()
        } onComplete {
          case Success(risk) =>
            new ResultDialog(risk).showAndWait()
          case Failure(ex) =>
            //ex.printStackTrace()
            new RusAlert(AlertType.Error, s"Ошибка: ${ex.getMessage}").showAndWait()
        }
      }
    }
  }

  def risk3AllMenuAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      new RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      new TableStage(db, TableStage.Risk3).show()
    }
  }

  def risk3SubjMethodMenuAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      new Alert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      val sn = new Risk3Dialog(currentDB.get).showAndWait()
      sn foreach { case Risk3Dialog.Risk3Input(s,n) =>
        UIUtil.qal.show()
        db.risk3ById(n, s) andThen {
          case _ => UIUtil.qal.close()
        } onComplete {
          case Success(risk) =>
            new ResultDialog(risk).showAndWait()
          case Failure(ex) =>
            new RusAlert(AlertType.Error, s"Ошибка: ${ex.getMessage}").showAndWait()
        }
      }
    }
  }

  // Analysis menu handlers

  def chartMenuAction(ae: ActionEvent): Unit =
    new ChartStage(currentDB).show()

  // Private methods

  private def processXLS(xls: File): Unit = {
    currentDB.fold[Unit] {
      new RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      val rows = InputData.getValues(xls)

      UIUtil.qal.show()
      db.addMissingRowsToDB(rows) andThen {
        case _ => UIUtil.qal.close()
      } onComplete {
        case Success(added) =>
          new RusAlert(AlertType.Information, s"Добавлено $added новых значений").showAndWait()
        case Failure(ex) =>
          new RusAlert(AlertType.Error, s"Ошибка: ${ex.getMessage}").showAndWait()
      }
    }
  }

  private def updateStatus(): Unit = {
    statusLabel.text = currentDB.fold {
      "Текущая база: нет"
    } { db =>
      s"Текущая база: ${db.file.getName}"
    }
  }

}
