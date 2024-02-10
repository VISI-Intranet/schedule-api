package model

import org.mongodb.scala.bson.ObjectId

case class Student(
                    studentId: String,
                    firstName: String,
                    lastName: String,
                    email: String,
                    schedule: Schedule
                  )