package controllers

import java.nio.file.{Files, Path, Paths}

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  *
  * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
  */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "HomeController GET" should {

    "render the index page from the application" in {
      val controller = inject[HomeController]
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Welcome to Play")
    }

    "give simple PageView" in {
      val controller = inject[HomeController]
      val home = controller.home().apply(FakeRequest())
      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include("Ok")
    }

    "get list of currencies" in {
      val controller = inject[HomeController]
      val curr = controller.currencies().apply(FakeRequest())
      status(curr) mustBe OK
      contentType(curr) mustBe Some("application/json")
      (contentAsJson(curr) \ "base").as[String] mustBe "EUR"
    }
  }

  "file scanner" should {

    "walk filee tree recursively" in {
      val home = s"${System.getProperty("user.home")}"
      val start = System.currentTimeMillis()
      //    val size = scanDirectories(List(
      //      home
      //      s"$home/.npm"
      //      ,s"$home/.m2"
      //    )).size
      val javaFiles = getListOfFiles(s"$home/.npm") // Files.readAllLines(Paths.get("files.list"))
      println(s"java files amount: ${javaFiles.size}")
      val total = System.currentTimeMillis() - start
      println(s"spent $total")
    }

    import scala.collection.JavaConverters._

    def using[R <: AutoCloseable, T](stream: R)(f: R => T): T =
      try {
        f(stream)
      } finally {
        stream.close()
      }

    import resource._

    def getListOfFiles(dir: String): List[Path] = {
      val res = managed(Files.walk(Paths.get(dir))) map { stream =>
        stream.iterator().asScala.filter(Files.isRegularFile(_)).toList
      }
      res.opt.getOrElse(List())
    }


    def scanDirectories(dirs: List[String]): List[Path] = dirs flatMap scanDirectory

    def scanDirectory(dir: String): Iterator[Path] =
      Files.walk(Paths.get(dir)).parallel().filter(Files.isRegularFile(_)).iterator().asScala

  }
}
