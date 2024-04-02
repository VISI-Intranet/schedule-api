package RabbitMQ
import org.json4s.native.Serialization.write
import com.rabbitmq.client.ConnectionFactory

object Send {

  private val routingkey = "univer.scheduleapi.get"

  def send(body: String) {
    val factory = new ConnectionFactory()
    factory.setHost("localhost")
    val connection = factory.newConnection()
    val channel = connection.createChannel()
    channel.basicPublish("X:routing.topic2", routingkey, null, body.getBytes("UTF-8"))
    channel.close()
    connection.close()
  }
}