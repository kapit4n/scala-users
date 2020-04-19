package controllers

import javax.inject._
import models.User
import play.api.{Configuration, Logger}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's User page and basic informations
 */
@Singleton
class UserController @Inject()(cc: ControllerComponents, config: Configuration) extends AbstractController(cc) {

  val logger: Logger = Logger(this.getClass)

  /**
    * Return users list
    *
    * @return model.User
    */
  def list() = Action { implicit request: Request[AnyContent] =>
    val firstName0 = "Sheindel"
    val lastName0 = "Arce"
    val age0 = 40
    val capacity0 = 100

    val firstName1 = "Luis"
    val lastName1 = "Arce"
    val age1 = 40
    val capacity1 = 100

    val user0 = User(firstName0, lastName0, age0, capacity0)
    val user1 = User(firstName1, lastName1, age1, capacity1)

    logger.info("Called user list")
    Ok(Json.toJson(Array(user0, user1)))
  }

  /**
    * Return user by id
    *
    * @return model.User
    */
  def get(id: Int) = Action { implicit request: Request[AnyContent] =>
    val firstName0 = "Sheindel"
    val lastName0 = "Arce"
    val age0 = 40
    val capacity0 = 100

    val user0 = User(firstName0, lastName0, age0, capacity0)

    logger.info(id.toString)
    Ok(Json.toJson(user0))
  }
  

  /**
    * Return created user
    *
    * @return model.User
    */
  def create() = Action { implicit request: Request[AnyContent] =>
    userForm.bindFromRequest.fold(
      errorForm => {
        Ok(Json.toJson("Error to create user"))
      },
      user => {
        Ok(Json.toJson(user))
      }
    )
  }

  /**
    * Return updated user
    *
    * @return model.User
    */
  def update(id: Int) = Action { implicit request: Request[AnyContent] =>
  userForm.bindFromRequest.fold(
      errorForm => {
        Ok(Json.toJson("Error to update user"))
      },
      user => {
        Ok(Json.toJson(user))
      }
    )
  }


val userForm: Form[UserForm] = Form (
  mapping(
    "firstName" -> nonEmptyText,
    "lastName" -> nonEmptyText,
    "age" -> number,
    "capacity" -> number
  )(UserForm.apply)(UserForm.unapply(_))
)

}

case class UserForm(firstName: String, lastName: String, age: Int, capacity: Int)

object UserForm {
  implicit val formatter: OFormat[UserForm] = Json.format[UserForm]
}