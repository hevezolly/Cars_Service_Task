package model.repositiries

import com.google.inject.ImplementedBy
import model.Data._
import model.logging.ConsoleLogger

trait CarsRepository {
  def carById(id: Long): Option[Car]

  def carByParameters(id: Option[Long],
                      number: Option[Number],
                      brand: Option[String],
                      color: Option[Color],
                      issue_year: Option[Year]): List[Car] =
    allCars.filter(c => id.forall(c.id == _) && number.forall(c.number == _) && brand.forall(c.brand == _) &&
                        color.forall(c.color == _) && issue_year.forall(c.issue_year == _))

  def allCars: List[Car]

  def addCar(number: Number,
             brand: String,
             color: Color,
             issue_year: Year): Option[AddCarError]

  def deleteCarById(id: Long): Option[RemoveCarError]
}
