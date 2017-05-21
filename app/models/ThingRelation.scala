package models


import org.joda.time.DateTime

trait ThingRelation {
  val id: Option[Long]
  val partId: Long
  val wholeId: Long
  val since: DateTime
  val until: Option[DateTime]
  val slot: String
}

object ThingRelation {

  val COMPONENT_TYPE = "COMPONENT"
  val CONTAINMENT_TYPE = "CONTAINMENT"

  def apply(id: Option[Long], relType: String, partId: Long, wholeId: Long, since: DateTime, until: Option[DateTime], slot: String, function: Option[String], quantity: Option[Int]) : ThingRelation =
    if (relType == COMPONENT_TYPE) Component(id, partId, wholeId, since, until, slot, function.getOrElse(""))
    else if (relType == CONTAINMENT_TYPE) Containment(id, partId, wholeId, since, until, slot, quantity.getOrElse(1))
    else throw new IllegalArgumentException("Unknown thing type")

  def tupled(tuple: (Option[Long], String, Long, Long, DateTime, Option[DateTime], String, Option[String], Option[Int])): ThingRelation = (apply _).tupled(tuple)

  def unapply(relation: ThingRelation): Option[(Option[Long], String, Long, Long, DateTime, Option[DateTime], String, Option[String], Option[Int])] = relation match {
    case Component(id, partId, wholeId, since, until, slot, function) => Some(id, COMPONENT_TYPE, partId, wholeId, since, until, slot, Some(function), None)
    case Containment(id, partId, wholeId, since, until, slot, quantity) => Some(id, CONTAINMENT_TYPE, partId, wholeId, since, until, slot, None, Some(quantity))
    case _ => None
  }

}

case class Component(
                      id: Option[Long],
                      partId: Long,
                      wholeId: Long,
                      since: DateTime,
                      until: Option[DateTime],
                      slot: String,
                      function: String
                    ) extends ThingRelation {

}

case class Containment(
                        id: Option[Long],
                        partId: Long,
                        wholeId: Long,
                        since: DateTime,
                        until: Option[DateTime],
                        slot: String,
                        quantity: Int
                      ) extends ThingRelation {

}