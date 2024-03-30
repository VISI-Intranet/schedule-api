package route

import Scala.Main
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import repository.ScheduleRepository
import model.Schedule
import amqp._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.Success

object ScheduleRoutes extends Json4sSupport {
  implicit val executor: ExecutionContext = Main.system.getDispatcher
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats
  implicit val timeout = Timeout(5 second)
  val route =
    pathPrefix("schedule") {
      concat(
        get {
          parameter("param") { param =>
            complete(ScheduleRepository.getSchedulesByField(param.toString))
          }
        },
        pathEnd {
          concat(
            get {
              complete(ScheduleRepository.getAllSchedules())
            },
            post {
              entity(as[Schedule]) { schedule =>
                val future = (Main.amqpActor ? RabbitMQ.Ask("univer.teacher_api.getTeacherName",schedule.professorId(0))).mapTo[String]
                onComplete(future) {
                  case Success(value) => {
                    schedule.prosessorName = value
                    complete(ScheduleRepository.addSchedule(schedule))
                  }
                }

              }
            }
          )
        },
        path(Segment) { scheduleId =>
          concat(
            get {
              complete(ScheduleRepository.getScheduleById(scheduleId))
            },
            put {
              entity(as[Schedule]) { updatedSchedule =>
                complete(ScheduleRepository.updateSchedule(scheduleId, updatedSchedule))
              }
            },
            delete {
              complete(ScheduleRepository.deleteSchedule(scheduleId))
            }
          )
        }
      )
    }
}




