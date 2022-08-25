package model.logging

import model.services.{CarAddError, CarRemoveError, CarsService, QueryExecutionError}
import play.api.libs.json.{JsObject, JsValue}

trait ServiceLogger extends CarsService with Logger {

  override val prefix: String = "SERVICE:"

  abstract override def removeCarById(id: Option[Long]): Option[CarRemoveError] = {
    log("removing car")
    super.removeCarById(id)
  }

  abstract override def getFieldsByQuery(query: String, ops: Option[String], vars: Option[JsObject]): Either[QueryExecutionError, JsValue] = {
    log("requesting cars by query")
    super.getFieldsByQuery(query, ops, vars)
  }

  abstract override def addCar(number: Option[String], brand: Option[String], color: Option[String], issue_year: Option[Int]): Option[CarAddError] ={
    log("adding car")
    super.addCar(number, brand, color, issue_year)
  }

}
