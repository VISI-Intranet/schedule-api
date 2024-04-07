package model

import org.mongodb.scala.bson.ObjectId

case class Schedule(
                     scheduleId: String,
                     courseId: List[String],
                     professorId: List[String],
                     roomId: List[String],
                     dayOfWeek: String,
                     startTime: String,
                     endTime: String,
                     semestr: String,
                     var prosessorName: String
                   )