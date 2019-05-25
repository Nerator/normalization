package ru.dovzhikov.normalization.controller

import scalafx.Includes._
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType, ChoiceDialog}

object UIUtil {

  def al(text: String): Alert = {
    val al = new Alert(AlertType.None) {
      title = text
      contentText = text
      // it is not possible to programmatically close a dialog box that doesn't have a Close/Cancel button:
      buttonTypes += ButtonType.Cancel
    }
    al.getDialogPane.lookupButton(ButtonType.Cancel).disable = true
    al
  }

  val qal: Alert = al("Выполняю запрос...")

  def cd[T](ch: Seq[T], header: String): ChoiceDialog[T] =
    new ChoiceDialog(defaultChoice = ch.head, choices = ch) {
      title = header
      headerText = header // "Choose letter"
    }

  class RusAlert(tp: AlertType, content: String) extends Alert(tp, content) {
    private val text = tp match {
      case AlertType.None => ""
      case AlertType.Error => "Ошибка"
      case AlertType.Confirmation => "Подтверждение"
      case AlertType.Information => "Информация"
      case AlertType.Warning => "Предупреждение"
    }
    title = text
    headerText = text
  }
}


