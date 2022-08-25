package model.repositiries

import model.Data._
import model.Data.{Car, Color, Year}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util._

@Singleton
class CarsDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
extends HasDatabaseConfigProvider[JdbcProfile] with CarsRepository {
  import profile.api._

  db.run(CarsTable.query.schema.createIfNotExists)

  override def carById(id: Long): Option[Car] ={
    val waitForRes = db.run(
      CarsTable.query.filter(_.id === id).result
    ).map(v => v.find(_ => true).map(CarsTable.ToCar))
    Await.ready(waitForRes, Duration.Inf)
    waitForRes.value.flatMap(_.toOption).flatten
  }

  override def allCars: List[Car] = {
    val waitForRes = db.run(CarsTable.query.result
    ).map(v => v.map(CarsTable.ToCar).toList)
    Await.ready(waitForRes, Duration.Inf)
    waitForRes.value.flatMap(_.toOption) getOrElse Nil
  }

  override def carByParameters(id: Option[Long],
                               number: Option[Number],
                               brand: Option[String],
                               color: Option[Color],
                               issue_year: Option[Year]): List[Car] = {

    val filterFunc = Array[CarsTable => Rep[Boolean]](_ => true,
      id.map(v => (c: CarsTable) => c.id === v).getOrElse(_ => true),
      number.map(v => (c: CarsTable) => c.number === v.value).getOrElse(_ => true),
      brand.map(v => (c: CarsTable) => c.brand === v).getOrElse(_ => true),
      color.map(v => (c: CarsTable) => c.color === v.value).getOrElse(_ => true),
      issue_year.map(v => (c: CarsTable) => c.issue_year === v.value).getOrElse(_ => true),
    ).reduceLeft((f1, f2) => (c: CarsTable) => f1(c) && f2(c))

    val waitForRes = db.run(
      CarsTable.query.filter(filterFunc).result
    ).map(v => v.map(CarsTable.ToCar).toList)
    Await.ready(waitForRes, Duration.Inf)
    waitForRes.value.flatMap(_.toOption) getOrElse Nil
  }

  override def addCar(number: Number, brand: String, color: Color, issue_year: Year): Option[AddCarError] = {
    val possibleCars = carByParameters(None, Some(number), Some(brand), Some(color), Some(issue_year))
    if (possibleCars.nonEmpty)
      return Some(CarAlreadyExists())

    val wait = db.run(CarsTable.query += (0L, number.value, brand, color.value, issue_year.value))
    Await.ready(wait, Duration.Inf)
    wait.value match {
      case Some(Success(_)) => None
      case Some(Failure(exception)) => Some(ExceptionThrown(exception))
      case _ => Some(InternalError())
    }
  }

  override def deleteCarById(id: Long): Option[RemoveCarError] = {
    if (carById(id).isEmpty)
      return Some(CarDoesNotExists())

    val wait = db.run(CarsTable.query.filter(_.id === id).delete)
    Await.ready(wait, Duration.Inf)
    wait.value match {
      case Some(Success(_)) => None
      case Some(Failure(exception)) => Some(ExceptionThrown(exception))
      case _ => Some(InternalError())
    }
  }
}
