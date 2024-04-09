package repository

import scala.concurrent.Future
import scala.collection.JavaConverters._
import collection.MongoDBConnection
import model._
import org.mongodb.scala.Document
import org.mongodb.scala.bson.{BsonDocument, BsonString}

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.Try


object ScheduleRepository {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  def getAllSchedules(): Future[List[Schedule]] = {
    val futureSchedules = MongoDBConnection.scheduleCollection.find().toFuture()

    futureSchedules.map { docs =>
      Option(docs).map { docList =>
        docList.collect { case doc: Document =>
          val courseIdList = Option(doc.get("courseId")).collect {
            case list: java.util.List[BsonString] => list.asScala.map(_.getValue).toList
          }.getOrElse(List.empty[String])

          Schedule(
            scheduleId = doc.getString("scheduleId"),
            courseId = courseIdList,
            professorId = Option(doc.get("professorId")).collect {
              case list: java.util.List[BsonString] => list.asScala.map(_.getValue).toList
            }.getOrElse(List.empty[String]),
            disciplineId = Option(doc.get("disciplineId")).collect {
              case list: java.util.List[BsonString] => list.asScala.map(_.getValue).toList
            }.getOrElse(List.empty[String]),
            roomId = Option(doc.get("roomId")).collect {
              case list: java.util.List[BsonString] => list.asScala.map(_.getValue).toList
            }.getOrElse(List.empty[String]),
            semestr = doc.getString("semestr"),
            dayOfWeek = doc.getString("dayOfWeek"),
            startTime = doc.getString("startTime"),
            endTime = doc.getString("endTime"),
            prosessorName = doc.getString("prosessoName"),
            disciplineName= None
          )
        }.toList // Преобразование Seq в List
      }.getOrElse(List.empty)
    }
  }


  def getScheduleById(scheduleId: String): Future[Option[Schedule]] = {
    val scheduleDocument = Document("scheduleId" -> scheduleId)

    MongoDBConnection.scheduleCollection.find(scheduleDocument).headOption().map {
      case Some(doc) =>
        Some(
          Schedule(
            scheduleId = doc.getString("scheduleId").toString,
            courseId = Option(doc.getList("courseId", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            professorId = Option(doc.getList("professorId", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            disciplineId = Option(doc.getList("disciplineId", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            roomId = Option(doc.getList("roomId", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            semestr = doc.getString("semestr"),
            dayOfWeek = doc.getString("dayOfWeek"),
            startTime = doc.getString("startTime"),
            endTime = doc.getString("endTime"),
            prosessorName = doc.getString("prosessorName"),
            disciplineName = None
          )
        )
      case None => None
    }
  }


  def addSchedule(schedule: Schedule): Future[String] = {
    val scheduleDocument = BsonDocument(
      "scheduleId" -> Option(schedule.scheduleId),
      "courseId" -> Option(schedule.courseId.map(BsonString(_))),
      "professorId" -> Option(schedule.professorId.map(BsonString(_))),
      "disciplineId" -> Option(schedule.disciplineId.map(BsonString(_))),
      "roomId" -> Option(schedule.roomId.map(BsonString(_))),
      "semestr" -> BsonString(schedule.semestr),
      "dayOfWeek" -> BsonString(schedule.dayOfWeek),
      "startTime" -> BsonString(schedule.startTime),
      "endTime" -> BsonString(schedule.endTime),
      "proseesorName"-> BsonString(schedule.prosessorName),
    )

    MongoDBConnection.scheduleCollection.insertOne(scheduleDocument).toFuture().map(_ => s"Schedule with ID ${schedule.scheduleId} has been added to the database.")
  }

  def deleteSchedule(scheduleId: String): Future[String] = {
    val scheduleDocument = Document("scheduleId" -> scheduleId)
    MongoDBConnection.scheduleCollection.deleteOne(scheduleDocument).toFuture().map(_ => s"Schedule with id $scheduleId has been deleted from the database.")
  }

  def updateSchedule(scheduleId: String, updatedSchedule: Schedule): Future[String] = {
    val filter = Document("scheduleId" -> scheduleId)

    val scheduleDocument = BsonDocument(
      "$set" -> BsonDocument(
        "scheduleId" -> BsonString(updatedSchedule.scheduleId),
        "courseId" -> Option(updatedSchedule.courseId.map(BsonString(_))),
        "professorId" -> Option(updatedSchedule.professorId.map(BsonString(_))),
        "roomId" -> Option(updatedSchedule.roomId.map(BsonString(_))),
        "semestr" -> BsonString(updatedSchedule.semestr),
        "dayOfWeek" -> BsonString(updatedSchedule.dayOfWeek),
        "startTime" -> BsonString(updatedSchedule.startTime),
        "endTime" -> BsonString(updatedSchedule.endTime)
      )
    )
    MongoDBConnection.scheduleCollection.updateOne(filter, scheduleDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Schedule with id $scheduleId has been updated in the database."
      } else {
        s"Schedule update unsuccessful: Either there is an issue in the database or with your input."
      }
    }
  }


  def getSchedulesByField(param: String): Future[List[Schedule]] = {
    val keyValue = param.split("=")

    if (keyValue.length == 2) {
      val key = keyValue(0)
      val value = Try(keyValue(1).toInt).toOption

      val scheduleDocument = Document(key -> value)

      MongoDBConnection.scheduleCollection.find(scheduleDocument).toFuture().map { documents =>
        documents.map { doc =>
          Schedule(
            scheduleId = doc.getString("scheduleId").toString,
            courseId = Option(doc.getList("courseId", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            professorId = Option(doc.getList("professorId", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            disciplineId = Option(doc.getList("disciplineId", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            roomId = Option(doc.getList("roomId", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            semestr = doc.getString("semestr"),
            dayOfWeek = doc.getString("dayOfWeek"),
            startTime = doc.getString("startTime"),
            endTime = doc.getString("endTime"),
            prosessorName = doc.getString("prosessorName"),
            disciplineName = None
          )
        }.toList
      }
    } else {
      // Обработка некорректного ввода
      Future.failed(new IllegalArgumentException("Неверный формат параметра"))
    }
  }
}
