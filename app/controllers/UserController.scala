package controllers

import javax.inject._
import play.api.{Configuration, Logger}
import play.api.libs.json.{Json, OFormat}
import scala.concurrent.{ExecutionContext, Future, Await}

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import dal._
import models.User

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's User page and basic informations
 */
@Singleton
class UserController @Inject()(cc: ControllerComponents, config: Configuration, repo: UserRepository)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  val logger: Logger = Logger(this.getClass)

  /**
    * Return users list
    *
    * @return model.User
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
  def create() = Action.async { implicit request: Request[AnyContent] =>
    createUserForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(Json.toJson("Error to create user")))
      },
      user => {
        repo.create(user.firstName, user.lastName, user.age, user.capacity).map { u => 
          Ok(Json.toJson(u))
        }
      }
    )
  }

  /**
    * Return updated user
    *
    * @return model.User
    */
  def update(id: Int) = Action { implicit request: Request[AnyContent] =>
  updateUserForm.bindFromRequest.fold(
      errorForm => {
        Ok(Json.toJson("Error to update user"))
      },
      user => {
        Ok(Json.toJson(user))
      }
    )
  }


  val createUserForm: Form[CreateUserForm] = Form (
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "age" -> number,
      "capacity" -> number
    )(CreateUserForm.apply)(CreateUserForm.unapply(_))
  )

  val updateUserForm: Form[UpdateUserForm] = Form (
    mapping(
      "id" -> longNumber,
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "age" -> number,
      "capacity" -> number
    )(UpdateUserForm.apply)(UpdateUserForm.unapply(_))
  )

}

case class CreateUserForm(firstName: String, lastName: String, age: Int, capacity: Int)

case class UpdateUserForm(id: Long,firstName: String, lastName: String, age: Int, capacity: Int)

object CreateUserForm {
  implicit val formatter: OFormat[CreateUserForm] = Json.format[CreateUserForm]
}

object UpdateUserForm {
  implicit val formatter: OFormat[UpdateUserForm] = Json.format[UpdateUserForm]
}