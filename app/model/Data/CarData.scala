package model.Data

import sangria.schema._

import scala.util.matching.Regex


case class Number(val value: String) extends AnyVal

object Number {
  val pattern: Regex = """^[A-Z][A-Z]\d\d\d[A-Z]\d\d$""".r

  def isValid(value: String): Boolean = pattern.matches(value)

  implicit val gqlType =
    ScalarAlias[Number, String](StringType, _.value, (c: String) => c match {
      case _ if isValid(c) => Right(Number(c))
      case _ => Left(new IncorrectNumber)})
}

case class Color(val value: String) extends AnyVal

object Color {
  val pattern: Regex = """^#[a-fA-F0-9]{6}$""".r
  def isValid(value: String): Boolean = pattern.matches(value)

  implicit val gqlType =
    ScalarAlias[Color, String](StringType, _.value, (c: String) => c match {
      case _ if isValid(c) => Right(Color(c))
      case _ => Left(new IncorrectColor)})
}

case class Year(val value: Int) extends AnyVal

object Year {

  implicit val gqlType = ScalarAlias[Year, Int](IntType, _.value, c => Right(Year(c)))
}


case class Car(val id: Long, val number: Number, val brand: String, val color: Color, val issueYear: Year)

object Car {
  def allFieldsNamesString = "id number brand color issueYear"
}
