package model

import org.mongodb.scala.bson.ObjectId

case class Classroom(
                      roomId: String,
                      roomNumber: String,
                      capacity: Int
                    )
