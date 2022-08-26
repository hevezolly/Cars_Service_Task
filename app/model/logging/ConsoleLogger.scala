package model.logging

trait ConsoleLogger extends Logger {
  abstract override def rawLog(msg: String) = { println(msg); super.rawLog(msg) }
}
