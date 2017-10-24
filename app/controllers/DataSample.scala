package controllers

import java.util.concurrent.TimeUnit

import org.json4s._
import org.json4s.native.JsonMethods._

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Deadline, Duration}
import scala.concurrent.{Await, Future, Promise}
import scala.util.Try
import scala.xml.Elem

object DataSample {


  val cache: mutable.Map[String, Elem] = mutable.WeakHashMap.empty


  def main(args: Array[String]): Unit = {
    loadCachedXml
    loadCachedXml
    loadCachedXml

    (0 /: List("blah", "op")) ((c, _) => c + 1)

    delay(Duration(7, TimeUnit.SECONDS).fromNow)

    val sampleXml =
      loadCachedXml

    println(sampleXml)
    val jValue = Xml.toJson(sampleXml)
    jValue transformField {
      case ("CD", x: JObject) => ("user", JArray(x :: Nil))
    }
    println(pretty(render(jValue)))

    println(Xml.toXml(jValue))

  }

  def loadCachedXml = cache.getOrElseUpdate("myXml", {
    Future {
      delay(Duration(5, TimeUnit.SECONDS).fromNow)
      cache.remove("myXml")
    }
    loadXml
  })

  def delay(dur: Deadline) = {
    Try(Await.ready(Promise().future, dur.timeLeft))
  }

  private def loadXml = {
    println("tet")
    <CATALOG>
      <CD>
        <TITLE>Empire Burlesque</TITLE>
        <ARTIST>Bob Dylan</ARTIST>
        <COUNTRY>USA</COUNTRY>
        <COMPANY>Columbia</COMPANY>
        <PRICE>10.90</PRICE>
        <YEAR>1985</YEAR>
      </CD>
      <CD>
        <TITLE>Hide your heart</TITLE>
        <ARTIST>Bonnie Tyler</ARTIST>
        <COUNTRY>UK</COUNTRY>
        <COMPANY>CBS Records</COMPANY>
        <PRICE>9.90</PRICE>
        <YEAR>1988</YEAR>
      </CD>
    </CATALOG>
  }
}
