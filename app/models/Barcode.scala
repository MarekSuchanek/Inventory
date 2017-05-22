package models

object BarcodeTypes extends Enumeration {
  val codabar = Value("Codabar")
  val code = Value("Code X")
  val ean = Value("EAN X")
  val jan = Value("JAN")
  val pharmacode = Value("Pharmacode")
  val telepen = Value("Telepen")
  val upc = Value("UPC")
}

case class Barcode(id: Option[Long], standard: String, code: String, thingId: Long)
