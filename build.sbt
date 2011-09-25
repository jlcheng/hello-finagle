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
    "org.scalatest" % "scalatest_2.8.1" % "1.5.1" % "test"
)	
