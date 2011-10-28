package org.jcheng
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject

/**
 * @author jcheng
 *
 */
object CasbahDemo {
  
  def main(argv: Array[String]) {
    val testData = MongoConnection()("jctest")("testdata")
    
    println("Assuming this is not the first run...")
    println("Size should be 2: " + testData.size)
    testData.remove(MongoDBObject("name" -> "firs"))
    println("Size should be 1: " + testData.size)
    testData.remove(MongoDBObject()) // remove everything
    
    testData += MongoDBObject("name" -> "first",  "val"-> 1)
    testData += MongoDBObject("name" -> "second", "val"-> 2)
    
    
  }

}