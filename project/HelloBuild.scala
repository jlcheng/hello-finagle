import sbt._
import Keys._

object MainBuild extends Build {

  lazy val root = Project(id="root", base=file(".")) settings (
    name := "Scala Example",
    version := "1.0.1",
    organization := "jcheng",
    scalaVersion := "2.8.2.RC3", // Finagle requires scala 2.8
    resolvers    += "twitter.com" at "http://maven.twttr.com",
    libraryDependencies ++= Seq(
      "com.twitter" % "finagle-core" % "1.9.0",
      "org.scalatest" % "scalatest_2.8.1" % "1.5.1" % "test"
    )
  )

}
