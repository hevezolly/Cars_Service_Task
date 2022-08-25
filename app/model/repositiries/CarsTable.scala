package model.repositiries

import model.Data._
import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlProfile.ColumnOption.SqlType


import scala.language.implicitConversions

class CarsTable(tag: Tag) extends Table[(Long, String, String, String, Int)](tag, "cars"){
  def id = column[Long]("id", SqlType("SERIAL"), O.PrimaryKey, O.AutoInc)
  def number = column[String]("number")
  def brand = column[String]("brand")
  def color = column[String]("color")
  def issue_year = column[Int]("issue_year")

  def * = (id, number, brand, color, issue_year)
}

object CarsTable {
  val query = TableQuery[CarsTable]
  implicit def ToCar(query: (Long, String, String, String, Int)): Car =
    Car(query._1, Number(query._2), query._3, Color(query._4), Year(query._5))
}
