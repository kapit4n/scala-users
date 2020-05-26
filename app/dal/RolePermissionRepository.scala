package dal

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import models.RolePermission
import models.Role
import models.Permission

import scala.concurrent.{Future, ExecutionContext}

@Singleton
class RolePermissionRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class RolesTable(tag: Tag) extends Table[Role](tag, "roles") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id, name, "") <> ((Role.apply _).tupled, Role.unapply)
  }

  private class PermissionsTable(tag: Tag) extends Table[Permission](tag, "permissions") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id, name, "", "") <> ((Permission.apply _).tupled, Permission.unapply)
  }

  private val permissions = TableQuery[PermissionsTable]

  private class RolePermissionsTable(tag: Tag) extends Table[RolePermission](tag, "rolePermissions") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def roleId = column[Long]("roleId") 
    def permissionId = column[Long]("permissionId") 
    def * = (id, roleId, permissionId) <> ((RolePermission.apply _).tupled, RolePermission.unapply)
    def permission = foreignKey("fk_rolepermissionId", permissionId, permissions)(_.id)
  }

  private val rolesP = TableQuery[RolePermissionsTable]
  private val roles = TableQuery[RolesTable]

  def create(roleId: Long, permissionId: Long): Future[RolePermission] = db.run {
    (rolesP.map(p => (p.roleId, p.permissionId))
      returning rolesP.map(_.id) into ((u, id) => RolePermission(id, u._1, u._2))
      ) += (roleId, permissionId)
  }

  def update(id: Long, roleId: Long, permissionId: Long): Future[Seq[RolePermission]] = db.run {
    rolesP.filter(_.id === id).result
  }

  def list(): Future[Seq[RolePermission]] = db.run {
    rolesP.result
  }

  def listRolePermissions(roleId: Long): Future[Seq[(Long, String)]] = db.run {
    val data = for {
        (r, p) <- rolesP join permissions  if r.roleId === roleId
        } yield (r.id, p.name)
    data.map(x => x).result
  } 

  def get(id: Long): Future[Seq[RolePermission]] = db.run {
    rolesP.filter(_.id === id).result
  }

  def update(id: Int, roleId: Long, permissionId: Long): Future[Seq[RolePermission]] = db.run {
    db.run(rolesP.filter(_.id === id.toLong).map(c => (c.roleId, c.permissionId)).update((roleId, permissionId)))
    rolesP.filter(_.id === id.toLong).result
  }
}
