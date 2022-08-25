package model.logging

trait LogsCollector {
  def collect: Seq[String]
}
