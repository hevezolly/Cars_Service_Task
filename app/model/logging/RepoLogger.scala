package model.logging

import model.Data
import model.Data.{Car, Color, Year}
import model.repositiries.CarsRepository

trait RepoLogger extends CarsRepository with Logger {

  override val prefix = "REPOSITORY:"

  abstract override def carById(id: Long): Option[Car] = {
    val res = super.carById(id)
    val foundPrompt = res match {
      case Some(_) => "found some"
      case None => "found none"
    }
    log(f"searching car by id: $id. $foundPrompt")
    res
  }

  abstract override def carByParameters(id: Option[Long],
                                        number: Option[Data.Number],
                                        brand: Option[String],
                                        color: Option[Color],
                                        issue_year: Option[Year]): List[Car] = {
    val res = super.carByParameters(id, number, brand, color, issue_year)
    val filters = (
      id.map(_ => "id") :: number.map(_ => "number") ::
      brand.map(_ => "brand") :: color.map(_ => "color") ::
      issue_year.map(_ => "issue_year") :: Nil
      ) filter(_.isDefined) map (_.get) mkString("[", ", ", "]")
    log(s"filtered by: $filters. found ${res.length}")
    res
  }

  abstract override def allCars: List[Car] = {
    log("requesting all cars")
    super.allCars
  }

}
