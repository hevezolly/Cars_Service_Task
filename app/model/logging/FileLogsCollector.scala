package model.logging

import model.logging.FileLogsCollector.filePath

import javax.inject.Singleton
import scala.io.Source
import scala.reflect.io.File

@Singleton
class FileLogsCollector extends LogsCollector {
  override def collect: Seq[String] = {
    if (!File(filePath).exists)
      return Seq()
    val source = Source.fromFile(filePath)
    try source.getLines().toSeq finally source.close()
  }
}

object FileLogsCollector{
  def filePath = "logs.txt"
}
