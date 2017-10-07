package controllers

import java.time.LocalDate
import javax.inject._

import akka.util.ByteString
import play.api.http.{MimeTypes, Writeable}
import play.api.libs.json.{JsValue, Json}
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


  implicit def writeableOf_JsValue: Writeable[JsValue] = {
    val mimeType = Some(withCharset(MimeTypes.JSON)(Codec.utf_8))
    println(s"writeable is called -- >> $mimeType")
    Writeable(a => ByteString(Json.toBytes(a)), mimeType)
  }

  implicit val envelopeWrites = Json.writes[Envelope]

  def home() = Action {
    Ok(Json.toJson(Envelope("Hällo Döcker World")))
  }

  case class Envelope(message: String, date: LocalDate = LocalDate.now())

}
