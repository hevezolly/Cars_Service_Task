package model.logging

trait ConsoleLogger extends Logger {
  override def rawLog(msg: String) = println(msg)
}
