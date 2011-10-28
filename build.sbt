// Basic project information
name          := "Scala Example"

version       := "1.0"

organization  := "org.you"

scalaVersion  := "2.8.2.RC3"

// Add Twitter's Repository
resolvers += "twitter.com" at "http://maven.twttr.com/"

// Add multiple dependencies
libraryDependencies ++= Seq(
    "com.twitter" % "finagle-core" % "1.9.0",
    "com.mongodb.casbah" % "casbah_2.8.1" % "2.1.5.0",
    "org.slf4j" % "slf4j-simple" % "1.6.3",
    "ch.qos.logback" % "logback-core" % "0.9.30",
    "org.scalatest" % "scalatest_2.8.1" % "1.5.1" % "test"
)	
