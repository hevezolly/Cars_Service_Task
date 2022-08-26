package model.services

import play.api.libs.json.{JsObject, JsValue}

import scala.concurrent.{ExecutionContext, Future}

trait CarsService {
  def getFieldsByQuery(query: String,
                       ops: Option[String] = None,
                       vars: Option[JsObject] = None)
                      (implicit ec: ExecutionContext): Future[Either[QueryExecutionError, JsValue]]

  def getAllCarsData(implicit ec: ExecutionContext): Future[Either[QueryExecutionError, JsValue]]

  def addCar(number: Option[String],
             brand: Option[String],
             color: Option[String],
             issueYear: Option[Int])(implicit ec: ExecutionContext): Future[Option[CarAddError]]

  def removeCarById(id: Option[Long])(implicit ec: ExecutionContext): Future[Option[CarRemoveError]]
}
