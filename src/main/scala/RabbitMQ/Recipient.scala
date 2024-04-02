package RabbitMQ

import com.rabbitmq.client._
import model._
import io.circe._
import io.circe.parser._

import repository._
import org.json4s.{DefaultFormats, jackson}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.util.{Failure, Success}

object RabbitMQConsumer {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats
  val QUEUE_NAME = "visi-organization"
  val factory = new ConnectionFactory()
  factory.setHost("localhost")
  val connection = factory.newConnection()
  val channel = connection.createChannel()

  val consumer: DefaultConsumer = new DefaultConsumer(channel) {
    override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]): Unit = {
      val message = new String(body, "UTF-8")
      println(s" Расписание практики под номером:'$message'")

      val json: Json = parse(message).getOrElse(Json.Null)

      val idValueOpt: Option[String] = json.hcursor.downField("body").get[String]("id").toOption

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
                   |  "routingKey":"${properties.getReplyTo}",
                   |  "body":{
                   |     "scheduleId":"${schedule.scheduleId}",
                   |     "dayOfWeek":"${schedule.dayOfWeek}",
                   |     "startTime":"${schedule.startTime}",
                   |     "endTime":"${schedule.endTime}",
                   |     "semestr":"${schedule.semestr}"
                   |  }
                   |}
                   |""".stripMargin

              val prop = new AMQP.BasicProperties.Builder()
                .correlationId(properties.getCorrelationId)
                .build()

              channel.basicPublish("X:routing.topic2", properties.getReplyTo, prop, responseJson.getBytes("UTF-8"))
              println("Отправлено обратно в практику: " + responseJson)
            }
          case Failure(exception) => println(exception.getMessage)
        }
      }
    }
  }

  // Метод funk() находится на уровне объекта RabbitMQConsumer
  def funk(): Unit = {
    channel.basicConsume(QUEUE_NAME, true, consumer)
  }
}