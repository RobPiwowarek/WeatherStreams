package pik

object HelloWorld {
  def main(args: Array[String]): Unit = {
    println("Hello Aga")
  }


  val defaultName = "world"

  def makeGreeting(name: String = defaultName): String = {
    s"Hello, $name"
  }
}
