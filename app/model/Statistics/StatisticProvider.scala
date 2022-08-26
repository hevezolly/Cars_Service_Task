package model.Statistics

import akka.http.scaladsl.model.DateTime

import scala.concurrent.{ExecutionContext, Future}

trait StatisticProvider {
  def firstAddTime(implicit ec: ExecutionContext): Future[Option[DateTime]]
  def lastAddTime(implicit ec: ExecutionContext): Future[Option[DateTime]]
  def numberOfEntries(implicit ec: ExecutionContext): Future[Long]
}
