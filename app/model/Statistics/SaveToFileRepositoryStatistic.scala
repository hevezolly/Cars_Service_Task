package model.Statistics

import akka.http.scaladsl.model.DateTime
import model.Data
import model.Data.{Color, Year}
import model.repositiries.{AddCarError, CarsRepository}
import play.api.libs.json.{JsObject, JsValue, Json}
import spray.json.JsString

import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.reflect.io.File

trait SaveToFileRepositoryStatistic extends CarsRepository with StatisticProvider{

  val saveFileName = "statistics.json"
  if (!File(saveFileName).exists) {
    File(saveFileName).createFile()
    write(Json.obj(
      "last_add" -> None,
      "first_add" -> None
    ))(ExecutionContext.global)
  }

  private def write(json: JsObject)(implicit ec: ExecutionContext): Future[Unit] = Future {
    val writer = new FileWriter(saveFileName)
    writer.write(json.toString())
    writer.close()
  }

  private def read(implicit ec: ExecutionContext): Future[(Option[String], Option[String])] = Future {
    val source = Source.fromFile(saveFileName)
    val content = try Json.parse(source.mkString) finally source.close()
    val first_add = (content \ "first_add").asOpt[String]
    val last_Add = (content \ "last_add").asOpt[String]
    (first_add, last_Add)
  }

  private def update(implicit ec: ExecutionContext): Future[Unit] = {
    val now = DateTime.now.toIsoDateTimeString()
    read.map(_._1).flatMap(first_add => write(Json.obj(
      "last_add" -> now,
      "first_add" -> Json.toJsFieldJsValueWrapper(first_add.getOrElse(now))
    )))
  }

  abstract override def addCar(number: Data.Number, brand: String, color: Color, issueYear: Year)
                              (implicit ec: ExecutionContext): Future[Option[AddCarError]] =
    super.addCar(number, brand, color, issueYear).flatMap{
      case None => update.map(_ => None)
      case v @ Some(_) => Future.successful(v)
    }

  override def numberOfEntries(implicit ec: ExecutionContext): Future[Long] = allCars.map(_.length)

  override def lastAddTime(implicit ec: ExecutionContext): Future[Option[DateTime]] =
    read.map(_._2.flatMap(DateTime.fromIsoDateTimeString))

  override def firstAddTime(implicit ec: ExecutionContext): Future[Option[DateTime]] =
    read.map(_._1.flatMap(DateTime.fromIsoDateTimeString))
}
