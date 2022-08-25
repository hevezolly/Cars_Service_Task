package model.repositiries

import model.Data.{Car, Color, Number, Year}

class TestRepo extends CarsRepository {
  private val Cars = List(
    Car(0L, Number("bb212b32"), "Lamborghini", Color("#000000"), Year(2000)),
    Car(1L, Number("bb212b31"), "Lamborghini Diablo", Color("#ff00ff"), Year(2010)),
    Car(2L, Number("bb212b31"), "Lamborghini Diablo", Color("#ff00ff"), Year(2011)),
    Car(3L, Number("ew211b30"), "Mercedes", Color("#ffffff"), Year(2011))
  )

  override def carById(id: Long): Option[Car] =
    Cars find (_.id == id)

  override def allCars: List[Car] = Cars

  override def addCar(number: Number, brand: String, color: Color, issue_year: Year): Option[AddCarError] = {
    println("add car")
    None
  }

  override def deleteCarById(id: Long): Option[RemoveCarError] = {
    println("remove car")
    None
  }
}
