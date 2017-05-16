package models

case class Label(id: Option[Long], name: String, description: String, color: String)

case class LabelThing(id: Option[Long], labelId: Long, thingId: Long)
