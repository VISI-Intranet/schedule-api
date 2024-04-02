
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import akka.http.scaladsl.server.Directives._
import RabbitMQ._
import scala.concurrent.{ExecutionContextExecutor, Future}
import route._

import scala.io.StdIn
import scala.language.postfixOps;

object Main extends Json4sSupport {

  implicit val system: ActorSystem = ActorSystem("web-service")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  def main(args: Array[String]): Unit = {
    RabbitMQConsumer.funk()

    val Routes = ScheduleRoutes.route

    val bindingFuture = Http().bindAndHandle(Routes, "localhost", 8081)

    println(s"Server online at http://localhost:8081/")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}

/*

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.mongodb.scala.MongoClient
import repository._
import model._

import scala.concurrent.{ExecutionContextExecutor, Future}
import java.util.Date

object Main extends App {

  implicit val system: ActorSystem = ActorSystem("MyAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val scheduleId = "1"

  // Подключение к базе данных
  val client = MongoClient()
  implicit val db = client.getDatabase("UniversitySchedule")

  // Создание объекта факультета
  val schedule = Schedule(
    scheduleId = scheduleId,
    courseId = List("1"),
    professorId = List("1"),
    roomId = List("230"),
    facultyId = List("fIt"),
    dayOfWeek = "Monday",
    startTime = "08:00 ",
    endTime = "09:00",

  )


  //  // Create (Добавление факультета)
/*     val createResult: Future[String] = ScheduleRepository.addSchedule(schedule)
   createResult.onComplete {
      case scala.util.Success(result) => println(s"schedule успешно добавлен в базу данных. Результат: $result")
      case scala.util.Failure(ex) => println(s"Ошибка при добавлении schedule: ${ex.getMessage}")
  }*/

  //  // Read (Чтение факультета по идентификатору)
  /*
   val readResult: Future[Option[Schedule]] = ScheduleRepository.getScheduleById(scheduleId)
   readResult.onComplete {
     case scala.util.Success(Some(schedule)) => println(s"Прочитан schedule: $schedule")
     case scala.util.Success(None) => println(s"schedule с id $scheduleId не найден")
      case scala.util.Failure(ex) => println(s"Ошибка при чтении schedule: ${ex.getMessage}")
    }
*/

  //  // Update (Обновление факультета)
/*  val updatedSchedule = Schedule(
    scheduleId = scheduleId,
    courseId = List("1"),
    professorId = List("1"),
    roomId = List("231"),
    facultyId = List("fIt"),
    dayOfWeek = "Sunday",
    startTime = "10:00 ",
    endTime = "10:50",
  )

  val updateResult: Future[String] = ScheduleRepository.updateSchedule(scheduleId, updatedSchedule)
  updateResult.onComplete {
    case scala.util.Success(result) => println(s"Schedule успешно обновлен. Результат: $result")
    case scala.util.Failure(ex) => println(s"Ошибка при обновлении Schedule: ${ex.getMessage}")
      }*/

 /*     // Delete (Удаление факультета)
      val deleteResult: Future[String] = ScheduleRepository.deleteSchedule(scheduleId)
  deleteResult.onComplete {
    case scala.util.Success(result) => println(s"Schedule успешно удален. Результат: $result")
    case scala.util.Failure(ex) => println(s"Ошибка при удалении Schedule: ${ex.getMessage}")
  }*/
 // Get schedules by day of week
 val dayOfWeek = "Monday" // Replace with the desired day of the week
  val getByDayOfWeekResult: Future[List[List[Schedule]]] = ScheduleRepository.getScheduleByDayOfWeek(dayOfWeek)
  getByDayOfWeekResult.onComplete {
    case scala.util.Success(schedules) => println(s"Schedules for $dayOfWeek: $schedules")
    case scala.util.Failure(ex) => println(s"Ошибка при получении расписания по дню недели: ${ex.getMessage}")
  }
      // Закрытие соединения с базой данных
      client.close()
  }


*/
