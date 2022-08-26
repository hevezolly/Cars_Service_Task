package model.repositiries

import com.google.inject.ImplementedBy
import model.Data._
import model.logging.ConsoleLogger

import scala.concurrent.{ExecutionContext, Future}

trait CarsRepository {
  def carById(id: Long)(implicit ec: ExecutionContext): Future[Option[Car]]

  def carByParameters(id: Option[Long],
                      number: Option[Number],
                      brand: Option[String],
                      color: Option[Color],
                      issueYear: Option[Year])
                     (implicit ec: ExecutionContext): Future[List[Car]] =
    allCars.map(_.filter(c =>
      id.forall(c.id == _) &&
      number.forall(c.number == _) &&
      brand.forall(c.brand == _) &&
      color.forall(c.color == _) &&
      issueYear.forall(c.issueYear == _)
    ))

  def allCars(implicit ec: ExecutionContext): Future[List[Car]]

  def addCar(number: Number,
             brand: String,
             color: Color,
             issueYear: Year)(implicit ec: ExecutionContext): Future[Option[AddCarError]]

  def deleteCarById(id: Long)(implicit ec: ExecutionContext): Future[Option[RemoveCarError]]
}
