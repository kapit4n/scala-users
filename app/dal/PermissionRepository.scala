package dal

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import models.Permission

import scala.concurrent.{Future, ExecutionContext}

@Singleton
class PermissionRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class PermissionsTable(tag: Tag) extends Table[Permission](tag, "permissions") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name") 
    def obj = column[String]("obj") 
    def action = column[String]("action") 
    def * = (id, name, obj, action) <> ((Permission.apply _).tupled, Permission.unapply)
  }

  private val permissions = TableQuery[PermissionsTable]

  def create(name: String, obj: String, action: String): Future[Permission] = db.run {
    (permissions.map(p => (p.name, p.obj, p.action))
      returning permissions.map(_.id) into ((u, id) => Permission(id, u._1, u._2, u._3))
      ) += (name, obj, action)
  }

  def list(): Future[Seq[Permission]] = db.run {
    permissions.result
  }

  def get(id: Long): Future[Seq[Permission]] = db.run {
    permissions.filter(_.id === id).result
  }

  def update(id: Int, name: String, obj: String, action: String): Future[Seq[Permission]] = db.run {
    db.run(permissions.filter(_.id === id.toLong).map(c => (c.name, c.obj, c.action)).update((name, obj, action)))
    permissions.filter(_.id === id.toLong).result
  }
}