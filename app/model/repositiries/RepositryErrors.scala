package model.repositiries

sealed trait RepositoryError extends Any
sealed trait AddCarError extends Any with RepositoryError
sealed trait RemoveCarError extends Any with RepositoryError

case class CarAlreadyExists(val v: Unit = ()) extends AnyVal with AddCarError

case class CarDoesNotExists(val v: Unit = ()) extends AnyVal with RemoveCarError
