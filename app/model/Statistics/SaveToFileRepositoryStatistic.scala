package model.Statistics

import akka.http.scaladsl.model.DateTime
import model.Data
import model.Data.{Color, Year}
import model.repositiries.{AddCarError, CarsRepository}
import play.api.libs.json.{JsObject, Json}

import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.reflect.io.File

trait SaveToFileRepositoryStatistic extends CarsRepository with StatisticProvider{

  val saveFileName = "statistics.json"
  if (!File(saveFileName).exists) {
    File(saveFileName).createFile()
    write(Json.obj(
      "last_add" -> None,
      "first_add" -> None
    ))
  }

  private def write(json: JsObject): Unit ={
    val writer = new FileWriter(saveFileName)
    writer.write(json.toString())
    writer.close()
  }

  private def read(): (Option[String], Option[String]) = {
    val source = Source.fromFile(saveFileName)
    val content = try Json.parse(source.mkString) finally source.close()
    val first_add = (content \ "first_add").asOpt[String]
    val last_Add = (content \ "last_add").asOpt[String]
    (first_add, last_Add)
  }

  private def update(): Unit ={
    val now = DateTime.now

    val first_add  = read()._1 match {
      case None => now.toIsoDateTimeString()
      case Some(v) => v
    }
    write(Json.obj(
      "last_add" -> now.toIsoDateTimeString(),
      "first_add" -> first_add
    ))
  }

  abstract override def addCar(number: Data.Number, brand: String, color: Color, issue_year: Year): Option[AddCarError] = {
    val res = super.addCar(number, brand, color, issue_year)
    if (res.isEmpty)
      update()
    res
  }

  override def numberOfEntries: Long = allCars.length

  override def lastAddTime: Option[DateTime] = read()._2.flatMap(DateTime.fromIsoDateTimeString)

  override def firstAddTime: Option[DateTime] = read()._1.flatMap(DateTime.fromIsoDateTimeString)
}
