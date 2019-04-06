package ru.dovzhikov.normalization.controller

import java.io.File

import ru.dovzhikov.normalization.ScalaFXExecutionContext.scalaFXExecutionContext
import ru.dovzhikov.normalization.model.{InputData, Normalization}
import ru.dovzhikov.normalization.model.db.DBUtil
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, GridPane}
import scalafx.stage.{FileChooser, Modality}
import scalafx.stage.FileChooser.ExtensionFilter
import scalafxml.core.macros.sfxml

import scala.concurrent.Future
import scala.util.{Failure, Success}

@sfxml
class TestController(private val bpane: BorderPane) extends TestControllerInterface {

  def openDBHandle(ae: ActionEvent) {
    val fileChooser = new FileChooser {
      title = "Open Resource File"
      extensionFilters += new ExtensionFilter("Database Files", "*.db")
    }
    //val stage = bpane.scene.value.window.value
    val selectedFile = fileChooser.showOpenDialog(stage)
    if (selectedFile != null) println(selectedFile)
  }

  def onClose(ae: ActionEvent) {
    println(s"java version: ${System.getProperty("java.version")}")
    println(s"javafx version: ${System.getProperty("javafx.version")}")
    Platform.exit()
  }

  def al(text: String) = {
    val al = new Alert(AlertType.None) {
      contentText = text
      // it is not possible to programmatically close a dialog box that doesn't have a Close/Cancel button:
      buttonTypes += ButtonType.Cancel
    }
    al.getDialogPane.lookupButton(ButtonType.Cancel).disable = true
    al
  }

  val qal = al("querying...")

  val tidSubj = new TextInputDialog {
    headerText = "Enter subject ID"
  }

  val tidLetter = new TextInputDialog {
    headerText = "Enter letter"
  }

  //val choices = DBUtil.letters
//  def cd(ch: Seq[String], header: String): ChoiceDialog[String] =
  private def cd[T](ch: Seq[T], header: String): ChoiceDialog[T] =
    new ChoiceDialog(defaultChoice = ch.head, choices = ch) {
      headerText = header // "Choose letter"
    }

  // CC for b2
  case class Cc(a: Int, b: Char)
  // Dialog for button2
  val b2dialog: Dialog[(Int, Char)] = {
  //val b2dialog: Dialog[Cc] = {
    val dialog = new Dialog[(Int, Char)]() {
    //val dialog = new Dialog[Cc]() {
      //title = "Login Dialog"
      headerText = "Choose subject and wing"
    }

    val loginButtonType = new ButtonType("Login", ButtonData.OKDone)
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
      add(new Label("Password:"), 0, 1)
      add(cb2, 1, 1)
    }

    dialog.dialogPane().content = grid

    dialog.resultConverter = dialogButton =>
      if (dialogButton == loginButtonType) {
        val s = DBUtil.subjects.find(_._2 == cb1.getValue).map(_._1)
        val l = DBUtil.letters.find(_._2 == cb2.getValue).map(_._1)
        val sl = for {
          ss <- s
          ll <- l
        } yield (ss,ll)
        if (sl.isDefined) (sl.get._1, sl.get._2)
        else null
      } else
        null

    dialog
  }

  def button1Handle(ae: ActionEvent): Unit = {
    val selection = cd(DBUtil.subjects.unzip._2, "Choose subject")
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
    //    val selection = cd(DBUtil.subjects map (_._2), "Choose subject")
    //      .showAndWait()
    //      .map(sn => DBUtil.subjects.filter(t => t._2 == sn).head._1)
    //    val letter = cd(DBUtil.letters map (_._2), "Choose wing")
    //      .showAndWait()
    //      .map(wn => DBUtil.letters.filter(t => t._2 == wn).head._1)
//    val sl = b2dialog.showAndWait()(new DConvert[(Int, Char), ((Int, Char)) => (Int, Char)] {
//      type S = (Int, Char)
//      override def apply(t: (Int, Char), f: ((Int, Char)) => (Int, Char)): (Int, Char) = t
//    })
    //implicit val abc = DConvert.t2r[(Int, Char), (Int, Char)]
    //val sl = b2dialog.showAndWait[((Int, Char)) => (Int, Char)]()
    val sl = b2dialog.showAndWait()
    sl match {
      case None =>
      case Some((s, l)) =>
        qal.show()
        // TODO: need case class to store result...
        DBUtil.risk2ById(s.asInstanceOf[Int], l.asInstanceOf[Char]) andThen {
        //DBUtil.risk2ById(s, l) andThen {
          case _ => qal.close()
        } onComplete {
          case Success(risk) =>
            new Alert(AlertType.Information, s"Risk: $risk").showAndWait()
          case Failure(ex) =>
            ex.printStackTrace()
            new Alert(AlertType.Error, s"Error: ${ex.getMessage}").showAndWait()
        }
    }
    //sl foreach (((s, l)): (Int, Char)) =>    )

//    for {
//      sel <- selection
//      let <- letter
//    } {
//      qal.show()
//      DBUtil.risk2ById(sel, let) andThen {
//        case _ => qal.close()
//      } onComplete {
//        case Success(risk) =>
//          new Alert(AlertType.Information, s"Risk: $risk").showAndWait()
//        case Failure(ex) =>
//          ex.printStackTrace()
//          new Alert(AlertType.Error, s"Error: ${ex.getMessage}").showAndWait()
//      }
//    }

  }

  def button3Handle(ae: ActionEvent): Unit = {
    val selection = cd(DBUtil.subjects.map(_._2), "Choose subject")
      .showAndWait()
      .map(sn => DBUtil.subjects.filter(t => t._2 == sn).head._1)
    // TODO: Normalization methods
    val method = cd(Normalization.methods, "Choose method")
        .showAndWait()
        .map(_.f)
    //selection foreach (s => {
    for {
      sel <- selection
      m <- method
    } {
      qal.show()
      DBUtil.risk3ById(m, sel) andThen {
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
      case Success(_) =>
        new Alert(AlertType.Information, s"Successfully imported missing values").showAndWait()
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

