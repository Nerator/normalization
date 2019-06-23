package ru.dovzhikov.normalization.view.dialogs

import ru.dovzhikov.normalization.model.Normalization

import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control.{ButtonType, ComboBox, Dialog, Label}
import scalafx.scene.layout.GridPane

import scala.language.postfixOps

final class Risk3Dialog(subjects: Seq[(Int, String, Int)])
  extends Dialog[Risk3Dialog.Risk3Input] {

  title = "Выберите субъект и метод нормирования"
  headerText = "Выберите субъект и метод нормирования"

  dialogPane().buttonTypes = Seq(ButtonType.OK, new ButtonType("Отмена", ButtonData.CancelClose))

  //val subjCB = new ComboBox(DBUtil.subjects.values.toSeq)
  val subjCB = new ComboBox(subjects filter { case (_,_,l) => l > 2 } map (_._2))
  val methodCB = new ComboBox(Normalization.methods map (_.name))

  dialogPane().content = new GridPane() {
    hgap = 10
    vgap = 10
    padding = Insets(20, 100, 10, 10)

    add(new Label("Субъект:"), 0, 0)
    add(subjCB, 1, 0)
    add(new Label("Метод:"), 0, 1)
    add(methodCB, 1, 1)
  }

  resultConverter = bt =>
    if (bt == ButtonType.OK) {
      val sO = subjects find (_._2 == subjCB.getValue) map (_._1)
      val fO = Normalization.methods find (_.name == methodCB.getValue) map (_.f)
      val sn = for {
        s <- sO
        f <- fO
      } yield (s,f)
      sn map { case (s,f) => Risk3Dialog.Risk3Input(s,f) } orNull
    } else null

}

object Risk3Dialog {
  final case class Risk3Input(subj: Int, fun: Seq[Double] => Seq[Double])
}

