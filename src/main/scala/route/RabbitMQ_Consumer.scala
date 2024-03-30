package route

import Scala.Main
import model.Schedule
import amqp._
import repository.ScheduleRepository
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.AsJsonInput.stringAsJsonInput

import scala.util.{Failure, Success}
import java.sql.Timestamp
import scala.concurrent.ExecutionContext

object RabbitMQ_Consumer extends CustomJson4sSupport {
  implicit val executor: ExecutionContext = Main.system.getDispatcher

  def handle(message: Message): Unit = {
    message.routingKey match {
      case ""=>{

      }
      case ""=>
    }
  }
}
