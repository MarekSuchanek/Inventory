package models

import java.sql.Timestamp

trait Thing {
  val id: Option[Long]
  val name: String
  val description: String
  val since: Timestamp
  val until: Timestamp
}

object Thing {

  val ATOMIC_TYPE = "ATOMIC"
  val CONTAINER_TYPE = "CONTAINER"
  val FUNCTIONAL_TYPE = "FUNCTIONAL"

  def apply(id: Option[Long], thingType: String, name: String, description: String, since: Timestamp, until: Timestamp) : Thing =
    if (thingType == ATOMIC_TYPE) Atomic(id, name, description, since, until)
    else if (thingType == CONTAINER_TYPE) Container(id, name, description, since, until)
    else if (thingType == FUNCTIONAL_TYPE) FunctionalWhole(id, name, description, since, until)
    else throw new IllegalArgumentException("Unknown thing type")

  def tupled(tuple: (Option[Long], String, String, String, Timestamp, Timestamp)): Thing = (apply _).tupled(tuple)

  def unapply(thing: Thing): Option[(Option[Long], String, String, String, Timestamp, Timestamp)] = thing match {
    case Atomic(id, name, description, since, until) => Some(id, ATOMIC_TYPE, name, description, since, until)
    case Container(id, name, description, since, until) => Some(id, CONTAINER_TYPE, name, description, since, until)
    case FunctionalWhole(id, name, description, since, until) => Some(id, FUNCTIONAL_TYPE, name, description, since, until)
    case _ => None
  }

}


case class Atomic(
                   id: Option[Long],
                   name: String,
                   description: String,
                   since: Timestamp,
                   until: Timestamp
                 ) extends Thing {
}


case class Container(
                      id: Option[Long],
                      name: String,
                      description: String,
                      since: Timestamp,
                      until: Timestamp
                    ) extends Thing {

}


case class FunctionalWhole(
                            id: Option[Long],
                            name: String,
                            description: String,
                            since: Timestamp,
                            until: Timestamp
                          ) extends Thing {
}
