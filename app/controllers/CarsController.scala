package controllers

import model.Statistics.StatisticProvider
import model.logging.LogsCollector

import javax.inject._
import play.api.mvc._
import model.services._
import model.repositiries._
import play.api.libs.json.{JsObject, JsString, JsValue, Json}

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class CarsController @Inject()(val controllerComponents: ControllerComponents,
                               val carsService: CarsService,
                               val statistics: StatisticProvider,
                               val logs: LogsCollector) extends BaseController {

  implicit val ec: ExecutionContext = ExecutionContext.global

  def index() = Action.async { implicit request: Request[AnyContent] =>
      carsService.getAllCarsData.map(processQueryResult)
  }

  def listCarsByRequest = Action.async { anyRequest =>
    {
        anyRequest.body.asJson.flatMap(v => (v \ "query").asOpt[String]) match {
        case None => carsService.getAllCarsData.map(processQueryResult)
        case Some(query) =>

        val body = anyRequest.body.asJson.get
        val operation = (body \ "operationName").asOpt[String]

        val variables = (body \ "variables").toOption.flatMap {
          case JsString(vars) => Some(parseVariables(vars))
          case obj: JsObject => Some(obj)
          case _ => None
        }

        carsService.getFieldsByQuery(query, operation, variables).map(processQueryResult)

      }
    }
  }

  def processQueryResult(result: Either[QueryExecutionError, JsValue]) = result match {
    case Right(result) => Ok(result)
    case Left(BadQueryError(e)) => BadRequest(f"incorrect query: ${e.getMessage}")
  }

  private def parseVariables(variables: String) =
    if (variables.trim == "" || variables.trim == "null") Json.obj() else Json.parse(variables).as[JsObject]


  def add_car() = Action.async { anyRequest => {
      if (anyRequest.body.asJson.isEmpty) {
        Future.successful(BadRequest("json expected"))
      }
      else{
        val body = anyRequest.body.asJson.get
        val number = (body \ "number").asOpt[String]
        val brand = (body \ "brand").asOpt[String]
        val color = (body \ "color").asOpt[String]
        val issueYear = (body \ "issueYear").asOpt[Int]

        carsService.addCar(number, brand, color, issueYear).map{
          case None => Ok("success")
          case Some(MissingYear(_)) => BadRequest("field \"issueYear\" is missing")
          case Some(MissingNumber(_)) => BadRequest("field \"number\" is missing")
          case Some(MissingColor(_)) => BadRequest("field \"color\" is missing")
          case Some(IncorrectNumberFormat(f)) => BadRequest(f"incorrect number format. should be $f")
          case Some(IncorrectColorFormat(f)) => BadRequest(f"incorrect color format. should be $f")
          case Some(ErrorWhileCarAdding(CarAlreadyExists(_))) => BadRequest(f"car with such parameters is already exists")
        }
      }
    }
  }

  def del_car() = Action.async { request =>{
      val id = request match {
        case _ if request.body.asText.isDefined => request.body.asText.get.toLongOption
        case _ if request.body.asJson.isDefined => (request.body.asJson.get \ "id").asOpt[Long]
        case _ => None
      }
      carsService.removeCarById(id).map{
        case None => Ok("success")
        case Some(MissingId(_)) => BadRequest("id should be provided as json with \"id\" field or as plane text")
        case Some(ErrorWhileCarRemoving(CarDoesNotExists(_))) => BadRequest("no car with such id found")
      }
    }
  }

  def statistic() = Action.async { _ =>{
    for {firstAdd <- statistics.firstAddTime.map(_.map(_.toIsoDateTimeString()))
         lastAdd <- statistics.lastAddTime.map(_.map(_.toIsoDateTimeString()))
         numOfEntries <- statistics.numberOfEntries} yield Ok(
        Json.obj(
          "first entry" -> firstAdd,
          "last entry" -> lastAdd,
          "number of entries" -> numOfEntries
        )
      )
    }
  }

  def provideLogs() = Action.async {_ => {
      logs.collect.map(v => Ok(v.mkString("\n")))
    }
  }
}
