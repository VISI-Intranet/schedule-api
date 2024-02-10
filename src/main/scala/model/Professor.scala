package model

import org.mongodb.scala.bson.ObjectId

case class Professor(
                      professorId: String,
                      firstName: String,
                      lastName: String,
                      email: String,
                      schedule: Schedule
                    )