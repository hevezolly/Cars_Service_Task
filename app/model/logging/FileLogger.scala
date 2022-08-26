package model.logging

import akka.http.scaladsl.model.DateTime

import java.io.FileWriter
import scala.reflect.io.File

trait FileLogger extends Logger {

  File(FileLogsCollector.filePath).createFile()

  abstract override def rawLog(msg: String): Unit = {
    val fw = new FileWriter(FileLogsCollector.filePath, true)
    val now = DateTime.now
    try {
      fw.write(f"${now.toIsoDateTimeString()}: $msg\n")
    }
    finally fw.close()
    super.rawLog(msg)
  }
}
