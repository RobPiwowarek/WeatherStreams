package unit

import org.scalatest.FlatSpec

class HelloWorldTest extends FlatSpec{

  "An empty Set" should "have size 0" in
    assert(Set.empty.size == 0)


  it should "produce NoSuchElementException when head is invoked" in
    assertThrows[NoSuchElementException] {
      Set.empty.head
    }

}
