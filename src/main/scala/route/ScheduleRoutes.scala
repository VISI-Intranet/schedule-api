package route

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import repository.ScheduleRepository
import model.Schedule

object ScheduleRoutes extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

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
                complete(ScheduleRepository.addSchedule(schedule))
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




