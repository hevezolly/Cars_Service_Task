package model.repositiries

import model.Data._
import model.Data.{Car, Color, Year}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util._

@Singleton
class CarsDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
extends HasDatabaseConfigProvider[JdbcProfile] with CarsRepository {

  import profile.api._

  db.run(CarsTable.query.schema.createIfNotExists)

  override def carById(id: Long)(implicit ec: ExecutionContext): Future[Option[Car]] = db.run(
      CarsTable.query.filter(_.id === id).result
    ).map(v => v.find(_ => true).map(CarsTable.ToCar))

  override def allCars(implicit ec: ExecutionContext): Future[List[Car]] = {
    db.run(CarsTable.query.result
    ).map(v => v.map(CarsTable.ToCar).toList)
  }

  override def carByParameters(id: Option[Long],
                               number: Option[Number],
                               brand: Option[String],
                               color: Option[Color],
                               issueYear: Option[Year])
                              (implicit ec: ExecutionContext): Future[List[Car]] = {

    val filterFunc = List[CarsTable => Rep[Boolean]](
      id.map(v => (c: CarsTable) => c.id === v).getOrElse(_ => true),
      number.map(v => (c: CarsTable) => c.number === v.value).getOrElse(_ => true),
      brand.map(v => (c: CarsTable) => c.brand === v).getOrElse(_ => true),
      color.map(v => (c: CarsTable) => c.color === v.value).getOrElse(_ => true),
      issueYear.map(v => (c: CarsTable) => c.issueYear === v.value).getOrElse(_ => true),
    ).reduceLeft((f1, f2) => (c: CarsTable) => f1(c) && f2(c))

    db.run(
      CarsTable.query.filter(filterFunc).result
    ).map(v => v.map(CarsTable.ToCar).toList)
  }

  override def addCar(number: Number, brand: String, color: Color, issueYear: Year)
                     (implicit ec: ExecutionContext): Future[Option[AddCarError]] =
    carByParameters(None, Some(number), Some(brand), Some(color), Some(issueYear)).flatMap {
      case Nil => db.run(CarsTable.query += (0L, number.value, brand, color.value, issueYear.value)).map(_ => None)
      case _ => Future.successful(Some(CarAlreadyExists()))
    }

  override def deleteCarById(id: Long)(implicit ec: ExecutionContext): Future[Option[RemoveCarError]] = {
    carById(id).flatMap {
      case None => Future.successful(Some(CarDoesNotExists()))
      case Some(_) => db.run(CarsTable.query.filter(_.id === id).delete).map(_ => None)
    }
  }
}
