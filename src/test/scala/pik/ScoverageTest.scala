package pik

import org.scalatest.{OneInstancePerTest, FlatSpec}

class ScoverageTest extends FlatSpec with OneInstancePerTest {
  
    "HelloWorld" should "execute the default block if no arg is given" in {
    val result = HelloWorld.makeGreeting()
    assert(result === "Hello, world")
}
    
    

}