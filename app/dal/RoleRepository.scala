package dal

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Role

import scala.concurrent.{Future, ExecutionContext}

@Singleton
class RoleRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
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

  def create(name: String, description: String): Future[Role] = db.run {
    (roles.map(p => (p.name, p.description))
      returning roles.map(_.id) into ((u, id) => Role(id, u._1, u._2))
      ) += (name, description)
  }

  def update(id: Long, name: String, description: String): Future[Seq[Role]] = db.run {
    roles.filter(_.id === id).result
  }

  def list(): Future[Seq[Role]] = db.run {
    roles.result
  }

  def get(id: Long): Future[Seq[Role]] = db.run {
    roles.filter(_.id === id).result
  }

  def update(id: Int, name: String, description: String): Future[Seq[Role]] = db.run {
    db.run(roles.filter(_.id === id.toLong).map(c => (c.name, c.description)).update((name, description)))
    roles.filter(_.id === id.toLong).result
  }
}