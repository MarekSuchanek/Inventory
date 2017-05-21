package models

import org.joda.time.DateTime

trait Thing {
  def withId(id: Long): Thing

  val id: Option[Long]
  val name: String
  val description: String
  val since: DateTime
  val until: Option[DateTime]

  def thingType: String
}

object Thing {

  val ATOMIC_TYPE = "ATOMIC"
  val CONTAINER_TYPE = "CONTAINER"
  val FUNCTIONAL_TYPE = "FUNCTIONAL"
  val TYPES = List(ATOMIC_TYPE, CONTAINER_TYPE, FUNCTIONAL_TYPE)


  def apply(id: Option[Long], thingType: String, name: String, description: String, since: DateTime, until: Option[DateTime]) : Thing =
    if (thingType == ATOMIC_TYPE) Atomic(id, name, description, since, until)
    else if (thingType == CONTAINER_TYPE) Container(id, name, description, since, until)
    else if (thingType == FUNCTIONAL_TYPE) FunctionalWhole(id, name, description, since, until)
    else throw new IllegalArgumentException("Unknown thing type")

  def tupled(tuple: (Option[Long], String, String, String, DateTime, Option[DateTime])): Thing = (apply _).tupled(tuple)

  def unapply(thing: Thing): Option[(Option[Long], String, String, String, DateTime, Option[DateTime])] = thing match {
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
                   since: DateTime,
                   until: Option[DateTime]
                 ) extends Thing {
  def withId(id: Long): Thing = copy(id = Some(id))
  def thingType = Thing.ATOMIC_TYPE
}


case class Container(
                      id: Option[Long],
                      name: String,
                      description: String,
                      since: DateTime,
                      until: Option[DateTime]
                    ) extends Thing {
  def withId(id: Long): Thing = copy(id = Some(id))
  def thingType = Thing.CONTAINER_TYPE
}


case class FunctionalWhole(
                            id: Option[Long],
                            name: String,
                            description: String,
                            since: DateTime,
                            until: Option[DateTime]
                          ) extends Thing {
  def withId(id: Long): Thing = copy(id = Some(id))
  def thingType = Thing.FUNCTIONAL_TYPE
}
