name := "cse-client"

organization := "com.github.pierremage"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.11.6", "2.10.4")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

libraryDependencies ++= {
  Seq(
    "com.ning" % "async-http-client" % "1.9.6"
  )
}
