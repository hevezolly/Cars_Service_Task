package model.repositiries

sealed trait RepositoryError extends Any
sealed trait AddCarError extends Any with RepositoryError
sealed trait RemoveCarError extends Any with RepositoryError

case class CarAlreadyExists(val v: Unit = ()) extends AnyVal with AddCarError
case class ExceptionThrown(val v: Throwable) extends AnyVal with AddCarError with RemoveCarError
case class InternalError(val v: Unit = ()) extends AnyVal with AddCarError with RemoveCarError

case class CarDoesNotExists(val v: Unit = ()) extends AnyVal with RemoveCarError
