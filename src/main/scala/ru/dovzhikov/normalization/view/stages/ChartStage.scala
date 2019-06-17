package ru.dovzhikov.normalization.view.stages

import java.io.IOException

import javafx.scene.Parent

import ru.dovzhikov.normalization.controller.ControllerWithExtras
import ru.dovzhikov.normalization.model.db.DBUtil

import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.stage.Stage
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

class ChartStage(db: Option[DBUtil]) extends Stage {

  private val resource = getClass.getResource("/ru/dovzhikov/normalization/view/ChartView.fxml")
  if (resource == null) {
    throw new IOException("Cannot load resource: view/MainView.fxml")
  }

  val loader = new FXMLLoader(resource, NoDependencyResolver)
  loader.load[Unit]()
  private val root = loader.getRoot[Parent]
  private val controller = loader.getController[ControllerWithExtras]

  title = "Нормирование данных"
  scene = new Scene(root)
  onCloseRequest = _ => this.close()

  controller.stage = this
  controller.currentDB = db
}
