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
    def email = column[String]("email")
    def dateOfBirth = column[String]("dateOfBirth")
    def gender = column[String]("gender")

    def * = (id, firstName, lastName, email, dateOfBirth, gender) <> ((User.apply _).tupled, User.unapply)
  }

  private val users = TableQuery[UsersTable]

  def create(firstName: String, lastName: String, email: String, dateOfBirth: String, gender: String): Future[User] = db.run {
    (users.map(p => (p.firstName, p.lastName, p.email, p.dateOfBirth, p.gender))
      returning users.map(_.id) into ((u, id) => User(id, u._1, u._2, u._3, u._4, u._5))
      ) += (firstName, lastName, email, dateOfBirth, gender)
  }

    def update(id: Long, firstName: String, lastName: String, email: String, dateOfBirth: String, gender: String): Future[Seq[User]] = db.run {
        users.filter(_.id === id).result
    }
  

  def list(): Future[Seq[User]] = db.run {
    users.result
  }

  def get(id: Long): Future[Seq[User]] = db.run {
    users.filter(_.id === id).result
  }

  def update(id: Int, firstName: String, lastName: String, email: String, dateOfBirth: String, gender: String): Future[Seq[User]] = db.run {
    db.run(users.filter(_.id === id.toLong).map(c => (c.firstName, c.lastName, c.email, c.dateOfBirth, c.gender)).update((firstName, lastName, email, dateOfBirth, gender)))
    users.filter(_.id === id.toLong).result
  }
}