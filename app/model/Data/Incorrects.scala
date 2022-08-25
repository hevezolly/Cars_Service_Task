package model.Data

import sangria.validation.Violation

class IncorrectNumber extends Violation {
  override def errorMessage: String = "incorrect car number"
}

class IncorrectColor extends Violation {
  override def errorMessage: String = "incorrect car color"
}
