package controllers

import play.api.libs.json._

import scala.xml._

object DataTransformer {

  def xmlToJson(xml: NodeSeq): JsValue = {
    def empty_?(node: Node) = node.child.isEmpty

    /* Checks if given node is leaf element. For instance these are considered leafs:
     * <foo>bar</foo>, <foo>{ doSomething() }</foo>, etc.
     */
    def leaf_?(node: Node) = {
      def descendant(n: Node): List[Node] = n match {
        case g: Group => g.nodes.toList.flatMap(x => x :: descendant(x))
        case _ => n.child.toList.flatMap { x => x :: descendant(x) }
      }

      !descendant(node).exists(_.isInstanceOf[Elem])
    }

    def array_?(nodeNames: Seq[String]) = nodeNames.size != 1 && nodeNames.toList.distinct.size == 1

    def directChildren(n: Node): NodeSeq = n.child.filter(c => c.isInstanceOf[Elem])

    def nameOf(n: Node) = (if (n.prefix ne null) n.prefix + ":" else "") + n.label

    def buildAttrs(n: Node) = n.attributes.map((a: MetaData) => (a.key, XValue(a.value.text))).toList

    sealed trait XElem
    case class XValue(value: String) extends XElem
    case class XLeaf(value: (String, XElem), attrs: List[(String, XValue)]) extends XElem
    case class XNode(fields: List[(String, XElem)]) extends XElem
    case class XArray(elems: List[XElem]) extends XElem

    def toJValue(x: XElem): JsValue = x match {
      case XValue(s) => JsString(s)
      case XLeaf((name, value), attrs) => (value, attrs) match {
        case (_, Nil) => toJValue(value)
        case (XValue(""), xs) => JsObject(mkFields(xs))
        case (_, xs) => JsObject((name, toJValue(value)) :: mkFields(xs))
      }
      case XNode(xs) => JsObject(mkFields(xs))
      case XArray(elems) => JsArray(elems.map(toJValue))
    }

    def mkFields(xs: List[(String, XElem)]): List[(String, JsValue)] =
      xs.flatMap { case (name, value) => (value, toJValue(value)) match {
        // This special case is needed to flatten nested objects which resulted from
        // XML attributes. Flattening keeps transformation more predictable.
        // <a><foo id="1">x</foo></a> -> {"a":{"foo":{"foo":"x","id":"1"}}} vs
        // <a><foo id="1">x</foo></a> -> {"a":{"foo":"x","id":"1"}}
        case (XLeaf(v, x :: xs), o: JsObject) => o.fields
        case (_, json) => (name, json) :: Nil
      }
      }

    def buildNodes(xml: NodeSeq): List[XElem] = xml match {
      case n: Node =>
        if (empty_?(n)) XLeaf((nameOf(n), XValue("")), buildAttrs(n)) :: Nil
        else if (leaf_?(n)) XLeaf((nameOf(n), XValue(n.text)), buildAttrs(n)) :: Nil
        else {
          val children = directChildren(n)
          XNode(buildAttrs(n) ::: children.map(nameOf).toList.zip(buildNodes(children))) :: Nil
        }
      case nodes: NodeSeq =>
        val allLabels = nodes.map(_.label)
        if (array_?(allLabels)) {
          val arr = XArray(nodes.toList.flatMap { n =>
            if (leaf_?(n) && n.attributes.length == 0) XValue(n.text) :: Nil
            else buildNodes(n)
          })
          XLeaf((allLabels.head, arr), Nil) :: Nil
        } else nodes.toList.flatMap(buildNodes)
    }

    buildNodes(xml) match {
      case List(x@XLeaf(_, _ :: _)) => toJValue(x)
      case List(x) => JsObject(nameOf(xml.head) -> toJValue(x) :: Nil)
      case x => JsArray(x.map(toJValue))
    }
  }

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
        <CD>
          <TITLE>Greatest Hits</TITLE>
          <ARTIST>Dolly Parton</ARTIST>
          <COUNTRY>USA</COUNTRY>
          <COMPANY>RCA</COMPANY>
          <PRICE>9.90</PRICE>
          <YEAR>1982</YEAR>
        </CD>
        <CD>
          <TITLE>Still got the blues</TITLE>
          <ARTIST>Gary Moore</ARTIST>
          <COUNTRY>UK</COUNTRY>
          <COMPANY>Virgin records</COMPANY>
          <PRICE>10.20</PRICE>
          <YEAR>1990</YEAR>
        </CD>
        <CD>
          <TITLE>Eros</TITLE>
          <ARTIST>Eros Ramazzotti</ARTIST>
          <COUNTRY>EU</COUNTRY>
          <COMPANY>BMG</COMPANY>
          <PRICE>9.90</PRICE>
          <YEAR>1997</YEAR>
        </CD>
        <CD>
          <TITLE>One night only</TITLE>
          <ARTIST>Bee Gees</ARTIST>
          <COUNTRY>UK</COUNTRY>
          <COMPANY>Polydor</COMPANY>
          <PRICE>10.90</PRICE>
          <YEAR>1998</YEAR>
        </CD>
        <CD>
          <TITLE>Sylvias Mother</TITLE>
          <ARTIST>Dr.Hook</ARTIST>
          <COUNTRY>UK</COUNTRY>
          <COMPANY>CBS</COMPANY>
          <PRICE>8.10</PRICE>
          <YEAR>1973</YEAR>
        </CD>
        <CD>
          <TITLE>Maggie May</TITLE>
          <ARTIST>Rod Stewart</ARTIST>
          <COUNTRY>UK</COUNTRY>
          <COMPANY>Pickwick</COMPANY>
          <PRICE>8.50</PRICE>
          <YEAR>1990</YEAR>
        </CD>
        <CD>
          <TITLE>Romanza</TITLE>
          <ARTIST>Andrea Bocelli</ARTIST>
          <COUNTRY>EU</COUNTRY>
          <COMPANY>Polydor</COMPANY>
          <PRICE>10.80</PRICE>
          <YEAR>1996</YEAR>
        </CD>
        <CD>
          <TITLE>When a man loves a woman</TITLE>
          <ARTIST>Percy Sledge</ARTIST>
          <COUNTRY>USA</COUNTRY>
          <COMPANY>Atlantic</COMPANY>
          <PRICE>8.70</PRICE>
          <YEAR>1987</YEAR>
        </CD>
        <CD>
          <TITLE>Black angel</TITLE>
          <ARTIST>Savage Rose</ARTIST>
          <COUNTRY>EU</COUNTRY>
          <COMPANY>Mega</COMPANY>
          <PRICE>10.90</PRICE>
          <YEAR>1995</YEAR>
        </CD>
        <CD>
          <TITLE>1999 Grammy Nominees</TITLE>
          <ARTIST>Many</ARTIST>
          <COUNTRY>USA</COUNTRY>
          <COMPANY>Grammy</COMPANY>
          <PRICE>10.20</PRICE>
          <YEAR>1999</YEAR>
        </CD>
        <CD>
          <TITLE>For the good times</TITLE>
          <ARTIST>Kenny Rogers</ARTIST>
          <COUNTRY>UK</COUNTRY>
          <COMPANY>Mucik Master</COMPANY>
          <PRICE>8.70</PRICE>
          <YEAR>1995</YEAR>
        </CD>
        <CD>
          <TITLE>Big Willie style</TITLE>
          <ARTIST>Will Smith</ARTIST>
          <COUNTRY>USA</COUNTRY>
          <COMPANY>Columbia</COMPANY>
          <PRICE>9.90</PRICE>
          <YEAR>1997</YEAR>
        </CD>
        <CD>
          <TITLE>Tupelo Honey</TITLE>
          <ARTIST>Van Morrison</ARTIST>
          <COUNTRY>UK</COUNTRY>
          <COMPANY>Polydor</COMPANY>
          <PRICE>8.20</PRICE>
          <YEAR>1971</YEAR>
        </CD>
        <CD>
          <TITLE>Soulsville</TITLE>
          <ARTIST>Jorn Hoel</ARTIST>
          <COUNTRY>Norway</COUNTRY>
          <COMPANY>WEA</COMPANY>
          <PRICE>7.90</PRICE>
          <YEAR>1996</YEAR>
        </CD>
        <CD>
          <TITLE>The very best of</TITLE>
          <ARTIST>Cat Stevens</ARTIST>
          <COUNTRY>UK</COUNTRY>
          <COMPANY>Island</COMPANY>
          <PRICE>8.90</PRICE>
          <YEAR>1990</YEAR>
        </CD>
        <CD>
          <TITLE>Stop</TITLE>
          <ARTIST>Sam Brown</ARTIST>
          <COUNTRY>UK</COUNTRY>
          <COMPANY>A and M</COMPANY>
          <PRICE>8.90</PRICE>
          <YEAR>1988</YEAR>
        </CD>
        <CD>
          <TITLE>Bridge of Spies</TITLE>
          <ARTIST>T'Pau</ARTIST>
          <COUNTRY>UK</COUNTRY>
          <COMPANY>Siren</COMPANY>
          <PRICE>7.90</PRICE>
          <YEAR>1987</YEAR>
        </CD>
        <CD>
          <TITLE>Private Dancer</TITLE>
          <ARTIST>Tina Turner</ARTIST>
          <COUNTRY>UK</COUNTRY>
          <COMPANY>Capitol</COMPANY>
          <PRICE>8.90</PRICE>
          <YEAR>1983</YEAR>
        </CD>
        <CD>
          <TITLE>Midt om natten</TITLE>
          <ARTIST>Kim Larsen</ARTIST>
          <COUNTRY>EU</COUNTRY>
          <COMPANY>Medley</COMPANY>
          <PRICE>7.80</PRICE>
          <YEAR>1983</YEAR>
        </CD>
        <CD>
          <TITLE>Pavarotti Gala Concert</TITLE>
          <ARTIST>Luciano Pavarotti</ARTIST>
          <COUNTRY>UK</COUNTRY>
          <COMPANY>DECCA</COMPANY>
          <PRICE>9.90</PRICE>
          <YEAR>1991</YEAR>
        </CD>
        <CD>
          <TITLE>The dock of the bay</TITLE>
          <ARTIST>Otis Redding</ARTIST>
          <COUNTRY>USA</COUNTRY>
          <COMPANY>Stax Records</COMPANY>
          <PRICE>7.90</PRICE>
          <YEAR>1968</YEAR>
        </CD>
        <CD>
          <TITLE>Picture book</TITLE>
          <ARTIST>Simply Red</ARTIST>
          <COUNTRY>EU</COUNTRY>
          <COMPANY>Elektra</COMPANY>
          <PRICE>7.20</PRICE>
          <YEAR>1985</YEAR>
        </CD>
        <CD>
          <TITLE>Red</TITLE>
          <ARTIST>The Communards</ARTIST>
          <COUNTRY>UK</COUNTRY>
          <COMPANY>London</COMPANY>
          <PRICE>7.80</PRICE>
          <YEAR>1987</YEAR>
        </CD>
        <CD>
          <TITLE>Unchain my heart</TITLE>
          <ARTIST>Joe Cocker</ARTIST>
          <COUNTRY>USA</COUNTRY>
          <COMPANY>EMI</COMPANY>
          <PRICE>8.20</PRICE>
          <YEAR>1987</YEAR>
        </CD>
      </CATALOG>

    println(sampleXml)
    println(Json.prettyPrint(xmlToJson(sampleXml)))

  }

}
