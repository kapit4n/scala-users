package controllers

import javax.inject._
import play.api.{Configuration, Logger}
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future, Await}

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import dal._
import models.RolePermission

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's RolePermission page and basic informations
 */
@Singleton
class RolePermissionController @Inject()(cc: ControllerComponents, config: Configuration, repo: RolePermissionRepository)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  /**
    * Return rolePermissions list
    *
    * @return model.RolePermission
    */
  def list() = Action.async { implicit request =>
    repo.list().map{ data => 
      Ok(Json.toJson(data))
    }
  }

  /**
    * Return rolePermission by id
    *
    * @return model.RolePermission
    */
  def get(id: Int) = Action.async { implicit request: Request[AnyContent] =>
    repo.get(id.toLong).map{ data => 
      Ok(Json.toJson(data(0)))
    }
  }

  /**
    * Return created rolePermission
    *
    * @return model.RolePermission
    */
  def create() = Action.async(parse.json) { implicit request =>
     request.body.validate[RolePermissionForm].fold({ _ => 
      Future(Ok(Json.obj("status" -> "400", "message" -> "Error parse data")))
    }, { rolePermission =>
      repo.create(rolePermission.roleId, rolePermission.permissionId).map { u => 
        Ok(Json.toJson(u))
      }
    })
  }

  /**
    * Return updated rolePermission
    *
    * @return model.RolePermission
    */
  def update(id: Int) = Action.async(parse.json) { implicit request =>
     request.body.validate[RolePermissionForm].fold({ _ => 
      Future(Ok(Json.obj("status" -> "400", "message" -> "Error parse data")))
    }, { rolePermission =>
      repo.update(id, rolePermission.roleId, rolePermission.permissionId).map { r => 
        Ok(Json.toJson(r))
      }
    })
  }

  val rolePermissionForm: Form[RolePermissionForm] = Form (
    mapping(
      "roleId" -> number,
      "permissionId" -> number,
    )(RolePermissionForm.apply)(RolePermissionForm.unapply(_))
  )
}

case class RolePermissionForm(roleId: Int, permissionId: Int)

object RolePermissionForm {
  implicit val formatter: OFormat[RolePermissionForm] = Json.format[RolePermissionForm]
}

