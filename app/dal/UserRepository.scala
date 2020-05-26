package dal

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import models.User
import models.UserInfo
import models.Role

import scala.concurrent.{Future, ExecutionContext}

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class RolesTable(tag: Tag) extends Table[Role](tag, "roles") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name") 
    def description = column[String]("description") 
    def * = (id, name, description) <> ((Role.apply _).tupled, Role.unapply)
  }

  private val roles = TableQuery[RolesTable]

  private class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("firstName")
    def lastName = column[String]("lastName")
    def email = column[String]("email")
    def dateOfBirth = column[String]("dateOfBirth")
    def gender = column[String]("gender")
    def roleId = column[Long]("roleId")
    def login = column[String]("login")
    def password = column[String]("password")
    def * = (id, firstName, lastName, email, dateOfBirth, gender, roleId, login, password) <> ((User.apply _).tupled, User.unapply)
    def role = foreignKey("fk_roleid", roleId, roles)(_.id)
  }

  private val users = TableQuery[UsersTable]

  def create(firstName: String, lastName: String, email: String, dateOfBirth: String, gender: String, roleId: Long, login: String, password: String): Future[User] = db.run {
    (users.map(p => (p.firstName, p.lastName, p.email, p.dateOfBirth, p.gender, p.roleId, p.login, p.password))
      returning users.map(_.id) into ((u, id) => User(id, u._1, u._2, u._3, u._4, u._5, u._6, u._7, u._8))
      ) += (firstName, lastName, email, dateOfBirth, gender, roleId, login, password)
  }

  def update(id: Long, firstName: String, lastName: String, email: String, dateOfBirth: String, gender: String): Future[Seq[User]] = db.run {
    users.filter(_.id === id).result
  }
  

  def list(): Future[Seq[User]] = db.run {
    users.result
  }

  def listJoin(): Future[Seq[(String, String)]] = db.run {
    (users join roles on (_.roleId === _.id))
  .map{ case (u, r) => (u.firstName, r.name) }.result
  }

  def listUsersInfo(): Future[Seq[UserInfo]] = {
    db.run((users join roles on (_.roleId === _.id)).map { 
      case (u, r) => (u.id, u.firstName, u.lastName, r.name) 
    }.result
    ).map(x => x.map( ui => UserInfo(ui._1, ui._2, ui._3, ui._4)))
  }

  def get(id: Long): Future[Seq[User]] = db.run {
    users.filter(_.id === id).result
  }

  def update(id: Int, firstName: String, lastName: String, email: String, dateOfBirth: String, gender: String): Future[Seq[User]] = db.run {
    db.run(users.filter(_.id === id.toLong).map(c => (c.firstName, c.lastName, c.email, c.dateOfBirth, c.gender)).update((firstName, lastName, email, dateOfBirth, gender)))
    users.filter(_.id === id.toLong).result
  }
}