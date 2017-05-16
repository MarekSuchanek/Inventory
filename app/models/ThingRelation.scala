package models

import java.sql.Timestamp

trait ThingRelation {
  val id: Option[Long]
  val partId: Long
  val wholeId: Long
  val since: Timestamp
  val until: Timestamp
  val slot: String
}

object ThingRelation {

  val COMPONENT_TYPE = "COMPONENT"
  val CONTAINMENT_TYPE = "CONTAINMENT"

  def apply(id: Option[Long], relType: String, partId: Long, wholeId: Long, since: Timestamp, until: Timestamp, slot: String, function: String) : ThingRelation =
    if (relType == COMPONENT_TYPE) Component(id, partId, wholeId, since, until, slot, function)
    else if (relType == CONTAINMENT_TYPE) Containment(id, partId, wholeId, since, until, slot)
    else throw new IllegalArgumentException("Unknown thing type")

  def tupled(tuple: (Option[Long], String, Long, Long, Timestamp, Timestamp, String, String)): ThingRelation = (apply _).tupled(tuple)

  def unapply(relation: ThingRelation): Option[(Option[Long], String, Long, Long, Timestamp, Timestamp, String, String)] = relation match {
    case Component(id, partId, wholeId, since, until, slot, function) => Some(id, COMPONENT_TYPE, partId, wholeId, since, until, slot, function)
    case Containment(id, partId, wholeId, since, until, slot) => Some(id, CONTAINMENT_TYPE, partId, wholeId, since, until, slot, null)
    case _ => None
  }

}

case class Component(
                      id: Option[Long],
                      partId: Long,
                      wholeId: Long,
                      since: Timestamp,
                      until: Timestamp,
                      slot: String,
                      function: String
                    ) extends ThingRelation {

}

case class Containment(
                        id: Option[Long],
                        partId: Long,
                        wholeId: Long,
                        since: Timestamp,
                        until: Timestamp,
                        slot: String
                      ) extends ThingRelation {

}