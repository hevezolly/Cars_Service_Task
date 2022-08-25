package model.services

import play.api.libs.json.{JsObject, JsValue}

trait CarsService {
  def getFieldsByQuery(query: String,
                       ops: Option[String] = None,
                       vars: Option[JsObject] = None): Either[QueryExecutionError, JsValue]

  def getAllCarsData(): Either[QueryExecutionError, JsValue]

  def addCar(number: Option[String],
             brand: Option[String],
             color: Option[String],
             issue_year: Option[Int]): Option[CarAddError]

  def removeCarById(id: Option[Long]): Option[CarRemoveError]
}
