package route

import Main.Main
import amqp._
import repository.ScheduleRepository
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.AsJsonInput.stringAsJsonInput

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext


object RabbitMQ_Consumer extends CustomJson4sSupport {
  implicit val executor: ExecutionContext = Main.system.getDispatcher
  val amqpActor = Main.system.actorSelection("user/amqpActor")


  def handle(message: Message): Unit = {
    message.routingKey match {
      case "univer.schedule_api.post"=> {
        val json = parse(message.body)

        val idValueOpt = (json \ "body" \ "id").extractOpt[String]
        idValueOpt.foreach { idValue =>
          println(s"Значение id практики: $idValue")

          val future = ScheduleRepository.getScheduleById(idValue)
          future onComplete {
            case Success(scheduleOpt) =>
              scheduleOpt.foreach { schedule =>
                val responseJson =
                  s"""
                     |{
                     |  "actionClass":"response",
                     |  "routingKey":"${message.replyTo}",
                     |  "body":{
                     |     "scheduleId":"${schedule.scheduleId}",
                     |     "dayOfWeek":"${schedule.dayOfWeek}",
                     |     "startTime":"${schedule.startTime}",
                     |     "endTime":"${schedule.endTime}",
                     |     "semestr":"${schedule.semestr}"
                     |  }
                     |}
                     |""".stripMargin
                amqpActor ! RabbitMQ.Answer(message.replyTo, message.correlationId,responseJson)
              }
          }
        }
      }
      case ""=>
    }
  }
}
