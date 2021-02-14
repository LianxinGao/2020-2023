package oop.layout

abstract class Element {
  def contents: Array[String]

  val height: Int = contents.length
  val width: Int = if (height == 0) 0 else contents(0).length
}

class ArrayElement(override val contents: Array[String]) extends Element {

}

class LineElement(s: String) extends Element {
  override def contents: Array[String] = Array(s)

  override val height: Int = 1
  override val width: Int = s.length
}

class UniformElement(ch: Char,
                     override val height: Int,
                     override val width: Int) extends Element {
  private val line = ch.toString * width


  // fill: (length)(value)
  override def contents: Array[String] = Array.fill(height)(line)
}
