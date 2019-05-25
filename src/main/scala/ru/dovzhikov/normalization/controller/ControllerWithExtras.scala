package ru.dovzhikov.normalization.controller

import ru.dovzhikov.normalization.model.db.DBUtil
import scalafx.application.Platform
import scalafx.stage.Stage

import scala.concurrent.ExecutionContext

trait ControllerWithExtras {
  var stage: Stage = _
  var currentDB: Option[DBUtil] = None
  implicit val scalaFXExecutionContext: ExecutionContext =
    ExecutionContext.fromExecutor(command => Platform.runLater(command))
}
