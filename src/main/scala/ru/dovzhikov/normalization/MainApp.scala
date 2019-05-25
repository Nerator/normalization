package ru.dovzhikov.normalization

import java.io.IOException

import javafx.scene.Parent

import ru.dovzhikov.normalization.controller.ControllerWithExtras

import scalafx.Includes._
import scalafx.application.{JFXApp, Platform}
import scalafx.scene.Scene

import scalafxml.core.{FXMLLoader, NoDependencyResolver}

object MainApp extends JFXApp {

  private val resource = getClass.getResource("/ru/dovzhikov/normalization/view/MainView.fxml")
  if (resource == null) {
    throw new IOException("Cannot load resource: view/MainView.fxml")
  }

  val loader = new FXMLLoader(resource, NoDependencyResolver)
  loader.load[Unit]()
  private val root = loader.getRoot[Parent]
  private val controller = loader.getController[ControllerWithExtras]

//  val root = FXMLView(getClass.getResource("startscreen.fxml"), NoDependencyResolver)

  stage = new JFXApp.PrimaryStage() {
    title = "Климатические риски"
    scene = new Scene(root)
    onCloseRequest = _ => Platform.exit()
  }

  controller.stage = stage

}
