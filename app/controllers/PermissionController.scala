package controllers

import javax.inject._
import play.api.{Configuration, Logger}
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future, Await}

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import dal._
import models.Permission

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's Permission page and basic informations
 */
@Singleton
class PermissionController @Inject()(cc: ControllerComponents, config: Configuration, repo: PermissionRepository)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  /**
    * Return permissions list
    *
    * @return model.Permission
    */
  def list() = Action.async { implicit request =>
    repo.list().map{ data => 
      Ok(Json.toJson(data))
    }
  }

  /**
    * Return permission by id
    *
    * @return model.Permission
    */
  def get(id: Int) = Action.async { implicit request: Request[AnyContent] =>
    repo.get(id.toLong).map{ data => 
      Ok(Json.toJson(data(0)))
    }
  }

  /**
    * Return created permission
    *
    * @return model.Permission
    */
  def create() = Action.async(parse.json) { implicit request =>
     request.body.validate[PermissionForm].fold({ _ => 
      Future(Ok(Json.obj("status" -> "400", "message" -> "Error parse data")))
    }, { permission =>
      repo.create(permission.name, permission.obj, permission.action).map { u => 
        Ok(Json.toJson(u))
      }
    })
  }

  /**
    * Return updated permission
    *
    * @return model.Permission
    */
  def update(id: Int) = Action.async(parse.json) { implicit request =>
     request.body.validate[PermissionForm].fold({ _ => 
      Future(Ok(Json.obj("status" -> "400", "message" -> "Error parse data")))
    }, { permission =>
      repo.update(id, permission.name, permission.obj, permission.action).map { r => 
        Ok(Json.toJson(r))
      }
    })
  }

  val permissionForm: Form[PermissionForm] = Form (
    mapping(
      "name" -> nonEmptyText,
      "obj" -> nonEmptyText,
      "action" -> nonEmptyText,
    )(PermissionForm.apply)(PermissionForm.unapply(_))
  )
  
}

case class PermissionForm(name: String, obj: String, action: String)

object PermissionForm {
  implicit val formatter: OFormat[PermissionForm] = Json.format[PermissionForm]
}

