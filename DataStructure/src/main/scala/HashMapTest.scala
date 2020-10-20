package main.scala
import scala.collection.mutable
object HashMapTest {
  def main(args: Array[String]): Unit = {
    val a = new mutable.HashMap()
    println(a.hashCode())
    println(a.hashCode() & 100)
  }
}


