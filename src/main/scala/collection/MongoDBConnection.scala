package collection



import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}



object MongoDBConnection
{
  private val mongoClient = MongoClient("mongodb://localhost:27017")
  val database: MongoDatabase = mongoClient.getDatabase("UniversitySchedule")
  val studentCollection: MongoCollection[Document] = database.getCollection("Student")
  val scheduleCollection: MongoCollection[Document] = database.getCollection("Schedule")
  val professorCollection: MongoCollection[Document] = database.getCollection("Professor")
  val faculityCollection: MongoCollection[Document] = database.getCollection("Faculity")
  val enrollmentCollection: MongoCollection[Document] = database.getCollection("Enrollment")
  val courseCollection: MongoCollection[Document] = database.getCollection("Course")
  val classroomCollection: MongoCollection[Document] = database.getCollection("Classroom")
}