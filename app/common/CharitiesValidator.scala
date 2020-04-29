package common

object CharitiesValidator {

  val validateTelephoneNumber = """^[0-9 ]{10,30}$"""
  val emailAddressPattern = """^(?i)[_a-z0-9-]+(\.[_a-z0-9-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,4})$"""

}
