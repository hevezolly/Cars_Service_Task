package model.Data

import model.repositiries.CarsRepository
import sangria.macros.derive.{ObjectTypeDescription, deriveObjectType}
import sangria.schema.{Argument, Field, IntType, ListType, LongType, ObjectType, OptionInputType, OptionType, Schema, StringType, fields}

object CarSchema {

  implicit val CarType =
    deriveObjectType[Unit, Car](
      ObjectTypeDescription("car information"))

  val IdArg = Argument("id", OptionInputType(LongType))
  val NumArg = Argument("number", OptionInputType(StringType))
  val BrandArg = Argument("brand", OptionInputType(StringType))
  val ColorArg = Argument("color", OptionInputType(StringType))
  val IssueArg = Argument("issue_year", OptionInputType(IntType))

  val GetByArgsQuery = ObjectType("Query", fields[CarsRepository, Unit](
    Field("cars", ListType(CarType),
      description = Some("Returns car with specific id"),
      arguments = IdArg :: NumArg :: BrandArg :: ColorArg :: IssueArg :: Nil,
      resolve = c => c.ctx.carByParameters(c arg IdArg,
        (c arg NumArg).map(Number(_)),
        c arg BrandArg,
        (c arg ColorArg).map(Color(_)),
        (c arg IssueArg).map(Year(_))))
  ))

  def ArgsSchema = Schema(GetByArgsQuery)
}
