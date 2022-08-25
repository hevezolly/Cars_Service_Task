package model.services

import model.repositiries.RemoveCarError

sealed trait ServiceError extends Any
sealed trait QueryExecutionError extends Any with ServiceError
sealed trait CarAddError extends Any with ServiceError
sealed trait CarRemoveError extends Any with ServiceError


case class BadQueryError(throwable: Throwable) extends AnyVal with QueryExecutionError
case class ErrorWhileQueryExecution(throwable: Throwable) extends AnyVal with QueryExecutionError
case class QueryWasNotFinished(msg: String) extends AnyVal with QueryExecutionError

case class InternalError(throwable: Throwable) extends AnyVal with ServiceError

case class IncorrectNumberFormat(requiredFormat: String) extends AnyVal with CarAddError
case class IncorrectColorFormat(requiredFormat: String) extends AnyVal with CarAddError
case class ErrorWhileCarAdding(repError: model.repositiries.AddCarError) extends AnyVal with CarAddError

case class MissingNumber(val v: Unit) extends AnyVal with CarAddError
case class MissingColor(val v: Unit) extends AnyVal with CarAddError
case class MissingYear(val v: Unit) extends AnyVal with CarAddError

case class MissingId(val v: Unit) extends AnyVal with CarRemoveError
case class ErrorWhileCarRemoving(val v: RemoveCarError) extends AnyVal with CarRemoveError