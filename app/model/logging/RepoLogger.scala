package model.logging

import model.Data
import model.Data.{Car, Color, Year}
import model.repositiries.CarsRepository

import scala.concurrent.{ExecutionContext, Future}

trait RepoLogger extends CarsRepository with Logger {

  override val prefix = "REPOSITORY:"

  abstract override def carById(id: Long)(implicit ec: ExecutionContext): Future[Option[Car]] =
    super.carById(id).map{
      case v @ Some(_) => log(f"searching car by id: $id. found some"); v
      case None => log(f"searching car by id: $id. found none"); None
    }

  abstract override def carByParameters(id: Option[Long],
                                        number: Option[Data.Number],
                                        brand: Option[String],
                                        color: Option[Color],
                                        issueYear: Option[Year])
                                       (implicit ec: ExecutionContext): Future[List[Car]] = {
    val filters = (
        id.map(_ => "id") ::
        number.map(_ => "number") ::
        brand.map(_ => "brand") ::
        color.map(_ => "color") ::
        issueYear.map(_ => "issueYear") ::
        Nil
      ).filter(_.isDefined).map(_.get).mkString("[", ", ", "]")
    super.carByParameters(id, number, brand, color, issueYear).map(v =>{
      log(s"filtered by: $filters. found ${v.length}")
      v
    })
  }

  abstract override def allCars(implicit ec: ExecutionContext): Future[List[Car]] = {
    log("requesting all cars")
    super.allCars
  }

}
