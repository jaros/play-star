package controllers

import java.nio.file.{Files, Path, Paths}
import javax.inject._

import models.Envelope
import models.JsonSerializers._
import play.api.libs.json.Json
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

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

  def home() = Action {
    Ok(Json.toJson(Envelope("Hällo Döcker Würld")))
  }

}

object Main {

  import scala.collection.JavaConverters._

  def main(args: Array[String]): Unit = {
    scanFiles(s"${System.getProperty("user.home")}/projects/play-star/app/").foreach(println(_))
  }

  def scanFiles(dir: String): Iterator[Path] =
    Files.walk(Paths.get(dir)).filter(Files.isRegularFile(_)).iterator().asScala


}
