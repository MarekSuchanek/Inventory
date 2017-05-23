package models

object BarcodeTypes extends Enumeration {
  val code128 = Value("CODE128")
  val code39 = Value("CODE39")
  val codabar = Value("Codabar")
  val ean2 = Value("EAN2")
  val ean5 = Value("EAN5")
  val ean8 = Value("EAN8")
  val ean13 = Value("EAN13")
  val itf = Value("ITF")
  val itf14 = Value("ITF14")
  val msi = Value("MSI")
  val msi10 = Value("MSI10")
  val msi11 = Value("MSI11")
  val msi1010 = Value("MSI1010")
  val msi1110 = Value("MSI1110")
  val pharmacode = Value("Pharmacode")
  val upc = Value("UPC")
}

case class Barcode(id: Option[Long], standard: String, code: String, thingId: Long)
