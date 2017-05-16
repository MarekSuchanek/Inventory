package models

case class Barcode(id: Option[Long], standard: String, code: String, thingId: Long)
