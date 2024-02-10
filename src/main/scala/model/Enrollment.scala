package model

import org.mongodb.scala.bson.ObjectId

case class Enrollment(
                       enrollmentId: String,
                       studentId: List[String],
                       courseId: List[String]
                     )
