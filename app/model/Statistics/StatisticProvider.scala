package model.Statistics

import akka.http.scaladsl.model.DateTime

trait StatisticProvider {
  def firstAddTime: Option[DateTime]
  def lastAddTime: Option[DateTime]
  def numberOfEntries: Long
}
