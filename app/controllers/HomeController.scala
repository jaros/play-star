package controllers

import javax.inject._

import models.MessagesRepo
import models.Models.{PageStatus, _}
import play.api.libs.json.Json
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, currencyService: CurrencyService) extends AbstractController(cc) {

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
    Ok(Json.toJson(getPage))
  }

  def currencies() = Action.async {
    currencyService.latest.get() map { resp =>
      Ok(resp.json)
    }
  }

  private def getPage = {
    PageView(PageStatus.Ok,
      Envelope(
        (Stream.from(0).map(_.toString) zip MessagesRepo.all).toMap
      )
    )
  }
}
