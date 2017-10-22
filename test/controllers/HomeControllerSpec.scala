package controllers

import java.io.{Closeable, PrintWriter}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  *
  * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
  */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "HomeController GET" should {

    "render the index page from a new instance of controller" in {
      val controller = new HomeController(null, stubControllerComponents())
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Welcome to Play")
    }

    "render the index page from the application" in {
      val controller = inject[HomeController]
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Welcome to Play")
    }

    "render the index page from the router" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Welcome to Play")
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

    def getListOfFiles(dir: String): List[Path] =
      using(Files.walk(Paths.get(dir))) {
        _.iterator().asScala.filter(Files.isRegularFile(_)).toList
      }


    def scanDirectories(dirs: List[String]): List[Path] = dirs flatMap scanDirectory

    def scanDirectory(dir: String): Iterator[Path] =
      Files.walk(Paths.get(dir)).parallel().filter(Files.isRegularFile(_)).iterator().asScala

  }
}
