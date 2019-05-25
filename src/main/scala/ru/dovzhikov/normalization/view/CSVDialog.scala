package ru.dovzhikov.normalization.view

import ru.dovzhikov.normalization.model.CSV

import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.geometry.HPos
import scalafx.scene.control._
import scalafx.scene.layout.{ColumnConstraints, GridPane}

final class CSVDialog extends Dialog[CSV.CSVOptions] {

  title = "Параметры CSV"
  headerText = "Параметры CSV"

  dialogPane().buttonTypes = Seq(ButtonType.OK, new ButtonType("Отмена", ButtonBar.ButtonData.CancelClose))

  private val headerLbl = new Label("Заголовок:")
  GridPane.setConstraints(headerLbl, 0, 0)
  private val headerCB = new CheckBox() {
    prefWidth = 200.0
  }
  GridPane.setConstraints(headerCB, 1, 0)
  private val sepLbl = new Label("Разделитель:")
  GridPane.setConstraints(sepLbl, 0, 1)
  private val sepCB = new ComboBox[Char](ObservableBuffer(',', ';', '\t')) {
    prefWidth = 200.0
  }
  GridPane.setConstraints(sepCB, 1, 1)
  private val decLbl = new Label("Знак десятичной точки:")
  GridPane.setConstraints(decLbl, 0, 2)
  private val decCB = new ComboBox[Char](ObservableBuffer('.', ',')) {
    prefWidth = 200.0
  }
  GridPane.setConstraints(decCB, 1, 2)
  private val encLbl = new Label("Кодировка:")
  GridPane.setConstraints(encLbl, 0, 3)
  private val encCB = new ComboBox[String](ObservableBuffer("UTF-8", "windows-1251")) {
    prefWidth = 200.0
  }
  GridPane.setConstraints(encCB, 1, 3)
  private val defB = new Button("По умолчанию") {
    onAction = _ => {
      headerCB.selected = true
      sepCB.value = ','
      decCB.value = '.'
      encCB.value = "UTF-8"
    }
  }
  GridPane.setConstraints(defB, 1, 4)

  private val grid = new GridPane() {
    hgap = 10.0
    vgap = 10.0
    columnConstraints = List(
      new ColumnConstraints() {
        halignment = HPos.Right
      },
      new ColumnConstraints() {
        halignment = HPos.Left
      }
    )
    children = List(
      headerLbl, headerCB, sepLbl, sepCB, decLbl, decCB, encLbl, encCB, defB
    )
  }

  dialogPane().content = grid

  resultConverter = bt =>
    if (bt == ButtonType.OK) CSV.CSVOptions(
      headerCB.selected(),
      sepCB.value(),
      decCB.value(),
      encCB.value()
    ) else null

}
