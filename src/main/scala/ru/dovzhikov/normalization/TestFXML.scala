package ru.dovzhikov.normalization

import ru.dovzhikov.normalization.controller.TestControllerInterface
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import java.io.IOException

import javafx.scene.Parent

object TestFXML extends JFXApp {

  //val abc = Tables.profile

  private val resource = getClass.getResource("/ru/dovzhikov/normalization/view/TestView.fxml")
  if (resource == null) {
    throw new IOException("Cannot load resource: view/TestView.fxml")
  }

  val loader = new FXMLLoader(resource, NoDependencyResolver)
  loader.load[Unit]()
  private val root = loader.getRoot[Parent]
  private val controller = loader.getController[TestControllerInterface]

//  val root = FXMLView(getClass.getResource("startscreen.fxml"), NoDependencyResolver)

  stage = new PrimaryStage() {
    title = "ScalaFX Hello World"
    scene = new Scene(root)
    onCloseRequest = _ => Platform.exit()
  }

  controller.stage = stage

}
