package model.repositiries

import model.Data._
import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlProfile.ColumnOption.SqlType


import scala.language.implicitConversions

class CarsTable(tag: Tag) extends Table[(Long, String, String, String, Int)](tag, "cars"){
  def id: Rep[Long] = column[Long]("id", SqlType("SERIAL"), O.PrimaryKey, O.AutoInc)
  def number: Rep[String] = column[String]("number")
  def brand: Rep[String] = column[String]("brand")
  def color: Rep[String] = column[String]("color")
  def issueYear: Rep[Int] = column[Int]("issueYear")

  def * = (id, number, brand, color, issueYear)
}

object CarsTable {
  val query = TableQuery[CarsTable]
  implicit def ToCar(query: (Long, String, String, String, Int)): Car =
    Car(query._1, Number(query._2), query._3, Color(query._4), Year(query._5))
}
