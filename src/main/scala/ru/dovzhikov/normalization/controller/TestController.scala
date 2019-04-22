package ru.dovzhikov.normalization.controller

import java.io.File

import ru.dovzhikov.normalization.ScalaFXExecutionContext.scalaFXExecutionContext
import ru.dovzhikov.normalization.model.{InputData, Normalization}
import ru.dovzhikov.normalization.model.db.DBUtil
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control.{Alert, ButtonType, ChoiceDialog, Dialog, Label, TextInputDialog}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.layout.{BorderPane, GridPane}
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter
import scalafxml.core.macros.sfxml

import scala.util.{Failure, Success}

@sfxml
class TestController(private val bpane: BorderPane) extends TestControllerInterface {

  def openDBHandle(ae: ActionEvent): Unit = {
    val fileChooser = new FileChooser {
      title = "Open Resource File"
      extensionFilters += new ExtensionFilter("Database Files", "*.db")
    }
    //val stage = bpane.scene.value.window.value
    val selectedFile = fileChooser.showOpenDialog(stage)
    if (selectedFile != null) println(selectedFile)
  }

  def onClose(ae: ActionEvent): Unit = {
    println(s"java version: ${System.getProperty("java.version")}")
    println(s"javafx version: ${System.getProperty("javafx.version")}")
    Platform.exit()
  }

  def al(text: String): Alert = {
    val al = new Alert(AlertType.None) {
      contentText = text
      // it is not possible to programmatically close a dialog box that doesn't have a Close/Cancel button:
      buttonTypes += ButtonType.Cancel
    }
    al.getDialogPane.lookupButton(ButtonType.Cancel).disable = true
    al
  }

  val qal: Alert = al("querying...")

  val tidSubj: TextInputDialog = new TextInputDialog {
    headerText = "Enter subject ID"
  }

  val tidLetter: TextInputDialog = new TextInputDialog {
    headerText = "Enter letter"
  }

  private def cd[T](ch: Seq[T], header: String): ChoiceDialog[T] =
    new ChoiceDialog(defaultChoice = ch.head, choices = ch) {
      headerText = header // "Choose letter"
    }

  // CC for b2
  case class Risk2Input(a: Int, b: Char)

  // Dialog for button2
  val b2dialog: Dialog[Risk2Input] = {
    val dialog = new Dialog[Risk2Input]() {
      headerText = "Choose subject and wing"
    }

    val loginButtonType = new ButtonType("OK", ButtonData.OKDone)
    dialog.dialogPane().buttonTypes = Seq(loginButtonType, ButtonType.Cancel)

    val cb1 = new scalafx.scene.control.ComboBox(DBUtil.subjects map (_._2)) {
      editable = false
    }

    val cb2 = new scalafx.scene.control.ComboBox(DBUtil.letters map (_._2)) {
      editable = false
    }

    val grid = new GridPane() {
      hgap = 10
      vgap = 10
      padding = Insets(20, 100, 10, 10)

      add(new Label("Subject:"), 0, 0)
      add(cb1, 1, 0)
      add(new Label("Wing:"), 0, 1)
      add(cb2, 1, 1)
    }

    dialog.dialogPane().content = grid

    dialog.resultConverter = dialogButton =>
      if (dialogButton == loginButtonType) {
        val sO = DBUtil.subjects.find(_._2 == cb1.getValue).map(_._1)
        val lO = DBUtil.letters.find(_._2 == cb2.getValue).map(_._1)
        val sl = for {
          s <- sO
          l <- lO
        } yield (s,l)
        sl map { case (s,l) => Risk2Input(s,l) } orNull
      } else
        null

    dialog
  }

  // CC for r3
  case class Risk3Input(subj: Int, fun: List[Double] => List[Double])

  // Dialog for button3
  val b3dialog: Dialog[Risk3Input] = {
    val dialog = new Dialog[Risk3Input]() {
      headerText = "Choose subject and wing"
    }

    val loginButtonType = new ButtonType("OK", ButtonData.OKDone)
    dialog.dialogPane().buttonTypes = Seq(loginButtonType, ButtonType.Cancel)

    val cb1 = new scalafx.scene.control.ComboBox(DBUtil.subjects map (_._2)) {
      editable = false
    }

    val cb2 = new scalafx.scene.control.ComboBox(Normalization.methods map (_.name)) {
      editable = false
    }

    val grid = new GridPane() {
      hgap = 10
      vgap = 10
      padding = Insets(20, 100, 10, 10)

      add(new Label("Subject:"), 0, 0)
      add(cb1, 1, 0)
      add(new Label("Method:"), 0, 1)
      add(cb2, 1, 1)
    }

    dialog.dialogPane().content = grid

    dialog.resultConverter = dialogButton =>
      if (dialogButton == loginButtonType) {
        val sO = DBUtil.subjects find (_._2 == cb1.getValue) map (_._1)
        val fO = Normalization.methods find (_.name == cb2.getValue) map (_.f)
        val sn = for {
          s <- sO
          f <- fO
        } yield (s,f)
        sn map { case (s,f) => Risk3Input(s,f) } orNull
      } else null

    dialog
  }

  def button1Handle(ae: ActionEvent): Unit = {
    val selection = cd(DBUtil.subjects.map(_._2), "Choose subject")
      .showAndWait()
      .map(sn => DBUtil.subjects.filter(t => t._2 == sn).head._1.toInt)
    selection foreach (s => {
      qal.show()
      DBUtil.risk1ById(s) andThen {
        case _ => qal.close()
      } onComplete {
        case Success(risk) =>
          new Alert(AlertType.Information, s"Risk: $risk").showAndWait()
        case Failure(ex) =>
          new Alert(AlertType.Error, s"Error: ${ex.getMessage}").showAndWait()
      }
    })
  }

  def button2Handle(ae: ActionEvent): Unit = {
    val sl = b2dialog.showAndWait()
    sl foreach { case Risk2Input(s, l) =>
        qal.show()
        DBUtil.risk2ById(s, l) andThen {
          case _ => qal.close()
        } onComplete {
          case Success(risk) =>
            new Alert(AlertType.Information, s"Risk: $risk").showAndWait()
          case Failure(ex) =>
            ex.printStackTrace()
            new Alert(AlertType.Error, s"Error: ${ex.getMessage}").showAndWait()
        }
    }
  }

  def button3Handle(ae: ActionEvent): Unit = {
    val sn = b3dialog.showAndWait()
    sn foreach { case Risk3Input(s,n) =>
      qal.show()
      DBUtil.risk3ById(n, s) andThen {
        case _ => qal.close()
      } onComplete {
        case Success(risk) =>
          new Alert(AlertType.Information, s"Risk: $risk").showAndWait()
        case Failure(ex) =>
          new Alert(AlertType.Error, s"Error: ${ex.getMessage}").showAndWait()
      }
    }
  }

  private def processXLS(xls: File): Unit = {
    val rows = InputData.getValues(xls)

    qal.show()
    DBUtil.addMissingRowsToDB(rows) andThen {
      case _ => qal.close()
    } onComplete {
      case Success(added) =>
        new Alert(AlertType.Information, s"Successfully imported $added missing values").showAndWait()
      case Failure(ex) =>
        new Alert(AlertType.Error, s"Error: ${ex.getMessage}").showAndWait()
    }
  }

  def importFileHandle(ae: ActionEvent): Unit = {
    val fileChooser = new FileChooser {
      title = "Открыть таблицу"
      extensionFilters += new ExtensionFilter("Файлы XLS", "*.xls")
    }
    val xls = fileChooser.showOpenDialog(stage)
    if (xls != null) processXLS(xls)
  }

  def importInternetHandle(ae: ActionEvent): Unit = {
    println("Hi!")
    val al1 = al("downloading...")
    val al2 = al("extracting...")

    al1.show()
    for {
      rar <- InputData.downloadRar()
      _ = al1.close()
      _ = al2.show()
      xls <- InputData.extractXls(rar)
      _ = al2.close()
    } processXLS(xls)
  }

}

