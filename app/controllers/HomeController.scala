package controllers

import javax.inject._
import models.AppInfo
import play.api.{Configuration, Logger}
import play.api.libs.json.Json
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page and basic informations
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, config: Configuration) extends AbstractController(cc) {

  val logger: Logger = Logger(this.getClass)

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  /**
    * Return informations about the application
    *
    * @return model.AppInfo
    */
  def info() = Action { implicit request: Request[AnyContent] =>
    val appName = config.get[String]("app.name")
    val appVersion = config.get[String]("app.version")

    val appInfo = AppInfo(appName, appVersion)

    logger.info("Called info")
    Ok(Json.toJson(appInfo))
  }
}
