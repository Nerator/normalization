package ru.dovzhikov.normalization.view.dialogs

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

import scalafx.Includes._
import scalafx.scene.control.{Button, ButtonType, Dialog, Label}
import scalafx.scene.layout.VBox

class ResultDialog(value: Double) extends Dialog[Nothing] {

  title = "Результат"
  headerText = "Результат вычислений"

  dialogPane().buttonTypes = Seq(ButtonType.OK)

  private val lbl = new Label(s"Значение риска: $value")
  private val cbB = new Button("Скопировать в буфер") {
    onAction = _ => {
      val clipboard = Toolkit.getDefaultToolkit.getSystemClipboard
      val sel = new StringSelection(value.toString)
      clipboard.setContents(sel, sel)
    }
  }

  dialogPane().content = new VBox(lbl, cbB) {
    spacing = 10.0
  }
}
