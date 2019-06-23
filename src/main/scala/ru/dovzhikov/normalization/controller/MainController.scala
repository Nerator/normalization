package ru.dovzhikov.normalization.controller

import java.io.File

import ru.dovzhikov.normalization.controller.UIUtil.RusAlert
import ru.dovzhikov.normalization.model.InputData
import ru.dovzhikov.normalization.model.db.DBUtil
import ru.dovzhikov.normalization.view.dialogs.{ResultDialog, Risk2Dialog, Risk3Dialog}
import ru.dovzhikov.normalization.view.stages.{ChartStage, TableStage}

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.Label
import scalafx.scene.web.WebView
import scalafx.stage.FileChooser
import scalafxml.core.macros.sfxml

import scala.util.{Failure, Success}

@sfxml
class MainController(statusLabel: Label, webView: WebView) extends ControllerWithExtras {

  // Инициализация статусной панели
  updateStatus()

  // Загрузка инструкции
  webView.contextMenuEnabled = false
  webView.engine.load(getClass.getResource("/ru/dovzhikov/normalization/view/instructions.html").toExternalForm)

  // Обработчики меню Файл

  def openDBMenuAction(ae: ActionEvent): Unit = {
    val selectedFile = new FileChooser {
      title = "Открыть базу данных"
      extensionFilters += new FileChooser.ExtensionFilter("Файлы баз данных", "*.db")
    }.showOpenDialog(stage)
    if (selectedFile != null)
      currentDB = for (db <- Option(selectedFile)) yield DBUtil(db)
    updateStatus()
  }

  def closeMenuAction(ae: ActionEvent): Unit = {
    println(s"java version: ${System.getProperty("java.version")}")
    println(s"javafx version: ${System.getProperty("javafx.version")}")
    Platform.exit()
  }

  // Обработчики меню Данные

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

  // Обработчики меню рисков

  def risk1AllMenuAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      new TableStage(db, TableStage.Risk1).show()
    }
  }

  def risk1SubjMenuAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
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
            RusAlert(AlertType.Error, s"Ошибка: ${ex.getMessage}").showAndWait()
        }
      })
    }
  }

  def risk2AllMenuAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      new TableStage(db, TableStage.Risk2All).show()
    }
  }

  def risk2SubjMenuAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      new TableStage(db, TableStage.Risk2Subj).show()
    }
  }

  def risk2WingMenuAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      new TableStage(db, TableStage.Risk2Wing).show()
    }
  }

  def risk2SubjWingMenuAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      val sl = new Risk2Dialog(db.subjects, db.letters).showAndWait()
      sl foreach { case Risk2Dialog.Risk2Input(s, l) =>
        UIUtil.qal.show()
        db.risk2ById(s, l) andThen {
          case _ => UIUtil.qal.close()
        } onComplete {
          case Success(risk) =>
            new ResultDialog(risk).showAndWait()
          case Failure(ex) =>
            //ex.printStackTrace()
            RusAlert(AlertType.Error, s"Ошибка: ${ex.getMessage}").showAndWait()
        }
      }
    }
  }

  def risk3AllMenuAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      new TableStage(db, TableStage.Risk3).show()
    }
  }

  def risk3SubjMethodMenuAction(ae: ActionEvent): Unit = {
    currentDB.fold[Unit] {
      RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      val sn = new Risk3Dialog(db.subjects).showAndWait()
      sn foreach { case Risk3Dialog.Risk3Input(s,n) =>
        UIUtil.qal.show()
        db.risk3ById(n, s) andThen {
          case _ => UIUtil.qal.close()
        } onComplete {
          case Success(risk) =>
            new ResultDialog(risk).showAndWait()
          case Failure(ex) =>
            RusAlert(AlertType.Error, s"Ошибка: ${ex.getMessage}").showAndWait()
        }
      }
    }
  }

  // Обработчики меню анализа

  def chartMenuAction(ae: ActionEvent): Unit =
    new ChartStage(currentDB).show()

  // Приватные методы

  private def processXLS(xls: File): Unit = {
    currentDB.fold[Unit] {
      RusAlert(AlertType.Error, "База данных не загружена").showAndWait()
    } { db =>
      val rows = InputData.getValues(xls)

      UIUtil.qal.show()
      db.addMissingRowsToDB(rows) andThen {
        case _ => UIUtil.qal.close()
      } onComplete {
        case Success(added) =>
          RusAlert(AlertType.Information, s"Добавлено $added новых значений").showAndWait()
        case Failure(ex) =>
          RusAlert(AlertType.Error, s"Ошибка: ${ex.getMessage}").showAndWait()
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

