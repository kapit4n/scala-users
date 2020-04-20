package dal

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.User

import scala.concurrent.{Future, ExecutionContext}

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class UsersTable(tag: Tag) extends Table[User](tag, "users") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("firstName")
    def lastName = column[String]("lastName")
    def age = column[Int]("age")
    def capacity = column[Int]("capacity")

    def * = (id, firstName, lastName, age, capacity) <> ((User.apply _).tupled, User.unapply)
  }

  private val users = TableQuery[UsersTable]

  def create(firstName: String, lastName: String, age: Int, capacity: Int): Future[User] = db.run {
    (users.map(p => (p.firstName, p.lastName, p.age, p.capacity))
      returning users.map(_.id) into ((u, id) => User(id, u._1, u._2, u._3, u._4))
      ) += (firstName, lastName, age, capacity)
  }

    def update(id: Long, firstName: String, lastName: String, age: Int, capacity: Int): Future[Seq[User]] = db.run {
        users.filter(_.id === id).result
    }
  

  def list(): Future[Seq[User]] = db.run {
    users.result
  }

  def get(id: Long): Future[Seq[User]] = db.run {
    users.filter(_.id === id).result
  }
}