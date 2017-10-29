package controllers

object DominoSample {


  def main(args: Array[String]): Unit = {

    val input = "dd;0;;s;;;a"
    val row = input.split(";")
    row.foreach(s => println(s.length))

    (0 /: List("blah", "op")) ((c, _) => c + 1)
  }
}
