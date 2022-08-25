package controllers

import model.Statistics.StatisticProvider

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
                               val statistics: StatisticProvider) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  def index() = Action.async { implicit request: Request[AnyContent] =>
    Future {
      processQueryResult(carsService.getAllCarsData())
    }(ExecutionContext.global)
  }

  def listCarsByRequest = Action.async { anyRequest =>
    Future {
        anyRequest.body.asJson.flatMap(v => (v \ "query").asOpt[String]) match {
        case None => processQueryResult(carsService.getAllCarsData())
        case Some(query) =>

        val body = anyRequest.body.asJson.get
        val operation = (body \ "operationName").asOpt[String]

        val variables = (body \ "variables").toOption.flatMap {
          case JsString(vars) => Some(parseVariables(vars))
          case obj: JsObject => Some(obj)
          case _ => None
        }

        processQueryResult(carsService.getFieldsByQuery(query, operation, variables))

      }
    }(ExecutionContext.global)
  }

  def processQueryResult(result: Either[QueryExecutionError, JsValue]) = result match {
    case Right(result) => Ok(result)
    case Left(BadQueryError(e)) => BadRequest(f"incorrect query: ${e.getMessage}")
    case Left(ErrorWhileQueryExecution(e)) => InternalServerError(f"internal error: ${e.getMessage}")
    case _ => InternalServerError("something went wrong")
  }

  private def parseVariables(variables: String) =
    if (variables.trim == "" || variables.trim == "null") Json.obj() else Json.parse(variables).as[JsObject]


  def add_car() = Action.async { anyRequest =>
    Future{
      if (anyRequest.body.asJson.isEmpty)
        BadRequest("json expected")
      else {
        val body = anyRequest.body.asJson.get
        val number = (body \ "number").asOpt[String]
        val brand = (body \ "brand").asOpt[String]
        val color = (body \ "color").asOpt[String]
        val issue_year = (body \ "issue_year").asOpt[Int]

        carsService.addCar(number, brand, color, issue_year) match {
          case None => Ok("success")
          case Some(MissingYear(_)) => BadRequest("field \"issue_year\" is missing")
          case Some(MissingNumber(_)) => BadRequest("field \"number\" is missing")
          case Some(MissingColor(_)) => BadRequest("field \"color\" is missing")
          case Some(IncorrectNumberFormat(f)) => BadRequest(f"incorrect number format. should be $f")
          case Some(IncorrectColorFormat(f)) => BadRequest(f"incorrect color format. should be $f")
          case Some(ErrorWhileCarAdding(CarAlreadyExists(_))) => BadRequest(f"car with such parameters is already exists")
          case Some(ErrorWhileCarAdding(ExceptionThrown(e))) => InternalServerError(e.getMessage)
          case Some(ErrorWhileCarAdding(model.repositiries.InternalError(_))) => InternalServerError("something went wrong")
        }
      }
    }(ExecutionContext.global)
  }

  def del_car() = Action.async { request =>
    Future{
      val id = request match {
        case _ if request.body.asText.isDefined => request.body.asText.get.toLongOption
        case _ if request.body.asJson.isDefined => (request.body.asJson.get \ "id").asOpt[Long]
        case _ => None
      }
      carsService.removeCarById(id) match {
        case None => Ok("success")
        case Some(MissingId(_)) => BadRequest("id should be provided as json with \"id\" field or as plane text")
        case Some(ErrorWhileCarRemoving(CarDoesNotExists(_))) => BadRequest("no car with such id found")
        case Some(ErrorWhileCarRemoving(ExceptionThrown(e))) => InternalServerError(e.getMessage)
        case Some(ErrorWhileCarRemoving(model.repositiries.InternalError(_))) => InternalServerError("something went wrong")
      }
    }(ExecutionContext.global)
  }

  def statistic() = Action.async { request =>
    Future{
      Ok(Json.obj(
        "first entry" -> statistics.firstAddTime.map(_.toIsoDateTimeString()),
        "last entry" -> statistics.lastAddTime.map(_.toIsoDateTimeString()),
        "number of entries" -> statistics.numberOfEntries
      ))
    }(ExecutionContext.global)
  }
}
