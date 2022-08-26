package model.logging

import scala.concurrent.{ExecutionContext, Future}

trait LogsCollector {
  def collect(implicit ec: ExecutionContext): Future[Seq[String]]
}
