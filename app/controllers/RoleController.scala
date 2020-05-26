package controllers

import javax.inject._
import play.api.{Configuration, Logger}
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future, Await}

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import dal._
import models.Role

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's User page and basic informations
 */
@Singleton
class RoleController @Inject()(cc: ControllerComponents, config: Configuration, repo: RoleRepository)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  /**
    * Return roles list
    *
    * @return model.Role
    */
  def list() = Action.async { implicit request =>
    repo.list().map{ data => 
      Ok(Json.toJson(data))
    }
  }

  /**
    * Return user by id
    *
    * @return model.User
    */
  def get(id: Int) = Action.async { implicit request: Request[AnyContent] =>
    repo.get(id.toLong).map{ data => 
      Ok(Json.toJson(data(0)))
    }
  }
  

  /**
    * Return created user
    *
    * @return model.User
    */
  def create() = Action.async(parse.json) { implicit request =>
     request.body.validate[RoleForm].fold({ _ => 
      Future(Ok(Json.obj("status" -> "400", "message" -> "Error parse data")))
    }, { role =>
      repo.create(role.name, role.description).map { u => 
        Ok(Json.toJson(u))
      }
    })
  }


  /**
    * Return updated user
    *
    * @return model.User
    */
  def update(id: Int) = Action.async(parse.json) { implicit request =>
     request.body.validate[RoleForm].fold({ _ => 
      Future(Ok(Json.obj("status" -> "400", "message" -> "Error parse data")))
    }, { role =>
      repo.update(id, role.name, role.description).map { r => 
        Ok(Json.toJson(r))
      }
    })
  }

  val roleForm: Form[RoleForm] = Form (
    mapping(
      "name" -> nonEmptyText,
      "descrition" -> text,
    )(RoleForm.apply)(RoleForm.unapply(_))
  )
  
}

case class RoleForm(name: String, description: String)

object RoleForm {
  implicit val formatter: OFormat[RoleForm] = Json.format[RoleForm]
}

