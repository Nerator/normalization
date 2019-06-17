package ru.dovzhikov.normalization.view.dialogs

import ru.dovzhikov.normalization.model.db.DBUtil
import scalafx.Includes._
import scalafx.scene.control.{ButtonType, ComboBox, Dialog, Label}
//import scalafx.geometry.Insets
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.layout.GridPane

import scala.language.postfixOps

final class Risk2Dialog(db: DBUtil) extends Dialog[Risk2Dialog.Risk2Input] {

  title = "Выберите субъект и отрасль"
  headerText = "Выберите субъект и отрасль"

  dialogPane().buttonTypes = Seq(ButtonType.OK, new ButtonType("Отмена", ButtonData.CancelClose))

  val subjCB = new ComboBox(db.subjects filter { case (_,_,l) => l > 2 } map (_._2))
  val wingCB = new ComboBox(db.letters map (_._2))

  dialogPane().content = new GridPane() {
    hgap = 10
    vgap = 10
    //padding = Insets(20, 100, 10, 10)

    add(new Label("Субъект:"), 0, 0)
    add(subjCB, 1, 0)
    add(new Label("Отрасль:"), 0, 1)
    add(wingCB, 1, 1)
  }

  resultConverter = bt =>
    if (bt == ButtonType.OK) {
      val sO = db.subjects.find(_._2 == subjCB.getValue).map(_._1)
      val lO = db.letters.find(_._2 == wingCB.getValue).map(_._1)
      val sl = for {
        s <- sO
        l <- lO
      } yield (s,l)
      sl map { case (s,l) => Risk2Dialog.Risk2Input(s,l) } orNull
    } else null

}

object Risk2Dialog {
  final case class Risk2Input(a: Int, b: Char)
}
