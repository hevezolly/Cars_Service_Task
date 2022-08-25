package model.Statistics

import akka.http.scaladsl.model.DateTime
import model.Data
import model.Data.{Color, Year}
import model.repositiries.{AddCarError, CarsRepository}

trait RuntimeRepositoryStatistic extends CarsRepository with StatisticProvider {

  protected var lastEntry: Option[DateTime] = None
  protected var firstEntry: Option[DateTime] = None

  private def update(): Unit ={
    val now = DateTime.now
    if (firstEntry.isEmpty)
      firstEntry = Some(now);
    lastEntry = Some(now)
  }

  abstract override def addCar(number: Data.Number, brand: String, color: Color, issue_year: Year): Option[AddCarError] = {
    val res = super.addCar(number, brand, color, issue_year)
    if (res.isEmpty)
      update()
    res
  }

  override def numberOfEntries: Long = allCars.length

  override def lastAddTime: Option[DateTime] = lastEntry

  override def firstAddTime: Option[DateTime] = firstEntry
}
