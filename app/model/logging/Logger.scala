package model.logging

trait Logger {
  val prefix: String
  final def log(msg: String)= rawLog(prefix + " " + msg)
  def rawLog(msg: String) = {}
}
