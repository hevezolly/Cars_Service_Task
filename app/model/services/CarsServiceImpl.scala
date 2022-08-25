package model.services

import model.Data._
import model.logging.ConsoleLogger
import model.repositiries.{CarsDAO, CarsRepository, RemoveCarError, TestRepo}
import play.api.libs.json.{JsObject, JsValue, Json}
import sangria.ast.Document
import sangria.execution.Executor
import sangria.parser.QueryParser
import sangria.marshalling.playJson._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

@Singleton
class CarsServiceImpl() extends CarsService {

  @Inject var carsRepository: CarsRepository = null

  private def executeGraphQLQuery(query: Document, operation: Option[String], vars: Option[JsObject]): Future[JsValue] = {
    implicit val ec = ExecutionContext.global
    Executor.execute(CarSchema.ArgsSchema, query, carsRepository, operationName = operation, variables = vars getOrElse Json.obj())
      .map(v => Json.parse(v.toString()))
  }

  override def getFieldsByQuery(query: String,
                       ops: Option[String] = None,
                       vars: Option[JsObject] = None): Either[QueryExecutionError, JsValue] = QueryParser.parse(query) match {
    case Failure(error) => Left(BadQueryError(error))

    case Success(queryAst) =>
      val wait = executeGraphQLQuery(queryAst, ops, vars)
      Await.ready(wait, Duration.Inf)

      wait.value match {
        case Some(Success(result)) => Right(result)
        case Some(Failure(error)) => Left(ErrorWhileQueryExecution(error))
        case None => Left(QueryWasNotFinished("something went wrong"))
      }
  }

  override def getAllCarsData(): Either[QueryExecutionError, JsValue] = getFieldsByQuery(f"{ cars { ${Car.allFieldsNamesString} } }")

  override def addCar(number: Option[String],
             brand: Option[String],
             color: Option[String],
             issue_year: Option[Int]): Option[CarAddError] = {
    if (number.isEmpty)
      return Some(MissingNumber())
    if (color.isEmpty)
      return Some(MissingColor())
    if (issue_year.isEmpty)
      return Some(MissingYear())

    if (!Number.isValid(number.get))
      return Some(IncorrectNumberFormat(Number.pattern.toString()))
    if (!Color.isValid(color.get))
      return Some(IncorrectColorFormat(Color.pattern.toString()))

    carsRepository.addCar(Number(number.get), brand.getOrElse(""), Color(color.get), Year(issue_year.get)) match {
      case None => None
      case Some(v) => Some(ErrorWhileCarAdding(v))
    }
  }

  override def removeCarById(id: Option[Long]): Option[CarRemoveError] = id match {
    case None => Some(MissingId())
    case Some(id) => carsRepository.deleteCarById(id).map(v => ErrorWhileCarRemoving(v))
  }

}


