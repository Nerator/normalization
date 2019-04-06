package ru.dovzhikov.normalization

import scalafx.application.Platform

import scala.concurrent.ExecutionContext

object ScalaFXExecutionContext {
  implicit val scalaFXExecutionContext: ExecutionContext =
    ExecutionContext.fromExecutor(command => Platform.runLater(command))
}
