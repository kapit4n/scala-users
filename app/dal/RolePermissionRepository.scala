package dal

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import models.RolePermission

import scala.concurrent.{Future, ExecutionContext}

@Singleton
class RolePermissionRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class RolePermissionsTable(tag: Tag) extends Table[RolePermission](tag, "rolePermissions") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def roleId = column[Int]("roleId") 
    def permissionId = column[Int]("permissionId") 
    def * = (id, roleId, permissionId) <> ((RolePermission.apply _).tupled, RolePermission.unapply)
  }

  private val roles = TableQuery[RolePermissionsTable]

  def create(roleId: Int, permissionId: Int): Future[RolePermission] = db.run {
    (roles.map(p => (p.roleId, p.permissionId))
      returning roles.map(_.id) into ((u, id) => RolePermission(id, u._1, u._2))
      ) += (roleId, permissionId)
  }

  def update(id: Long, roleId: Int, permissionId: Int): Future[Seq[RolePermission]] = db.run {
    roles.filter(_.id === id).result
  }

  def list(): Future[Seq[RolePermission]] = db.run {
    roles.result
  }

  def get(id: Long): Future[Seq[RolePermission]] = db.run {
    roles.filter(_.id === id).result
  }

  def update(id: Int, roleId: Int, permissionId: Int): Future[Seq[RolePermission]] = db.run {
    db.run(roles.filter(_.id === id.toLong).map(c => (c.roleId, c.permissionId)).update((roleId, permissionId)))
    roles.filter(_.id === id.toLong).result
  }
}