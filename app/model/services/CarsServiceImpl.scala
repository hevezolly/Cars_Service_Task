package model.services

import model.Data._
import model.logging.ConsoleLogger
import model.repositiries.{CarsDAO, CarsRepository, RemoveCarError}
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
class CarsServiceImpl @Inject() (val carsRepository: CarsRepository) extends CarsService {

  private def executeGraphQLQuery(query: Document, operation: Option[String], vars: Option[JsObject]): Future[JsValue] = {
    implicit val ec = ExecutionContext.global
    Executor.execute(CarSchema.ArgsSchema, query, carsRepository, operationName = operation, variables = vars getOrElse Json.obj())
      .map(v => Json.parse(v.toString()))
  }

  override def getFieldsByQuery(query: String,
                                ops: Option[String] = None,
                                vars: Option[JsObject] = None)
                               (implicit ec: ExecutionContext): Future[Either[QueryExecutionError, JsValue]] =
    QueryParser.parse(query) match {
    case Failure(error) => Future.successful(Left(BadQueryError(error)))
    case Success(queryAst) => executeGraphQLQuery(queryAst, ops, vars).map(Right(_))
  }

  override def getAllCarsData(implicit ec: ExecutionContext): Future[Either[QueryExecutionError, JsValue]] =
    getFieldsByQuery(f"{ cars { ${Car.allFieldsNamesString} } }")

  override def addCar(number: Option[String],
             brand: Option[String],
             color: Option[String],
             issueYear: Option[Int])(implicit ec: ExecutionContext): Future[Option[CarAddError]] = {
    if (number.isEmpty)
      return Future.successful(Some(MissingNumber()))
    if (color.isEmpty)
      return Future.successful(Some(MissingColor()))
    if (issueYear.isEmpty)
      return Future.successful(Some(MissingYear()))

    if (!Number.isValid(number.get))
      return Future.successful(Some(IncorrectNumberFormat(Number.pattern.toString())))
    if (!Color.isValid(color.get))
      return Future.successful(Some(IncorrectColorFormat(Color.pattern.toString())))

    carsRepository.addCar(Number(number.get), brand.getOrElse(""), Color(color.get), Year(issueYear.get))
      .map(_.map(v => ErrorWhileCarAdding(v)))
  }

  override def removeCarById(id: Option[Long])(implicit ec: ExecutionContext): Future[Option[CarRemoveError]] = id match {
    case None => Future.successful(Some(MissingId()))
    case Some(id) => carsRepository.deleteCarById(id).map(_.map(v => ErrorWhileCarRemoving(v)))
  }

}


