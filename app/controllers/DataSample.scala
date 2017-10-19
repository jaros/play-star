package controllers

import org.json4s._
import org.json4s.native.JsonMethods._

object DataSample {


  def main(args: Array[String]): Unit = {
    val sampleXml =
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

    println(sampleXml)
    val jValue = Xml.toJson(sampleXml)
    jValue transformField {
      case ("CD", x: JObject) => ("user", JArray(x :: Nil))
    }
    println(pretty(render(jValue)))

    println(Xml.toXml(jValue))

  }

}
