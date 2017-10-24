package controllers

import java.util.concurrent.TimeUnit
import javax.inject._

import akka.actor.ActorSystem
import models.MessagesRepo
import models.Models.{PageStatus, _}
import play.api.cache.SyncCacheApi
import play.api.libs.concurrent.CustomExecutionContext
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.duration.Duration


/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cache: SyncCacheApi, cc: ControllerComponents, actorSystem: ActorSystem, executor: TasksCustomExecutionContext) extends AbstractController(cc) {

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

  actorSystem.scheduler.schedule(initialDelay = 10.seconds, interval = 30.seconds) {
    // the block of code that will be executed
    print("Executing something...")
  } (executor)


  def home() = Action {
    Ok(Json.toJson(getPage))
  }

  private def getPage = {
    cache.getOrElseUpdate("myPage", Duration(30, TimeUnit.SECONDS)) {
      PageView(PageStatus.Ok,
        Envelope(
          (Stream.from(0).map(_.toString) zip MessagesRepo.all).toMap
        )
      )
    }

  }
}

class TasksCustomExecutionContext @Inject() (actorSystem: ActorSystem)
  extends CustomExecutionContext(actorSystem, "tasks-dispatcher")