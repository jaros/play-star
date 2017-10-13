package controllers

import java.nio.file.{Files, Path, Paths}
import javax.inject._

import models.Models.{PageStatus, _}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

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
    Ok(Json.toJson(PageView(PageStatus.Ok, Envelope(Map(
      "1" -> Message("Hällo Döcker Würld"),
      "2" -> Message("Blah bla"),
      "3" -> Message("Ops"),
    )))))
  }

}

object Main {

  import scala.collection.JavaConverters._

  def main(args: Array[String]): Unit = {
    val home = s"${System.getProperty("user.home")}"
    scanDirectories(List(
      s"$home/projects/play-star/app/",
      s"$home/projects/play-star/public/"
    )).foreach(println(_))
  }


  def scanDirectories(dirs: List[String]): List[Path] = dirs flatMap scanDirectory

  def scanDirectory(dir: String): Iterator[Path] =
    Files.walk(Paths.get(dir)).filter(Files.isRegularFile(_)).iterator().asScala


}
