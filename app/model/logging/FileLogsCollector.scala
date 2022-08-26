package model.logging

import model.logging.FileLogsCollector.filePath

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.reflect.io.File

@Singleton
class FileLogsCollector extends LogsCollector {
  override def collect(implicit ec: ExecutionContext): Future[Seq[String]] = Future {
    if (!File(filePath).exists)
      Seq[String]()
    else{
      val source = Source.fromFile(filePath)
      try
        source.getLines().toSeq
      finally
        source.close()
    }
  }
}

object FileLogsCollector{
  def filePath = "logs.txt"
}
