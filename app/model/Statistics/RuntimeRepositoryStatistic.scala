package model.Statistics

import akka.http.scaladsl.model.DateTime
import model.Data
import model.Data.{Color, Year}
import model.repositiries.{AddCarError, CarsRepository}

import scala.concurrent.{ExecutionContext, Future}

trait RuntimeRepositoryStatistic extends CarsRepository with StatisticProvider {

  protected var lastEntry: Option[DateTime] = None
  protected var firstEntry: Option[DateTime] = None

  private def update(): Unit ={
    val now = DateTime.now
    if (firstEntry.isEmpty)
      firstEntry = Some(now);
    lastEntry = Some(now)
  }

  abstract override def addCar(number: Data.Number, brand: String, color: Color, issueYear: Year)
                              (implicit ec: ExecutionContext): Future[Option[AddCarError]] = {
    super.addCar(number, brand, color, issueYear).map {
      case v @ Some(_) => v
      case None => update(); None
    }
  }

  override def numberOfEntries(implicit ec: ExecutionContext): Future[Long] = allCars.map(_.length)

  override def lastAddTime(implicit ec: ExecutionContext): Future[Option[DateTime]] = Future.successful(lastEntry)

  override def firstAddTime(implicit ec: ExecutionContext): Future[Option[DateTime]] = Future.successful(firstEntry)
}
