name := "cse-client"

organization := "com.github.pierremage"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.8"

crossScalaVersions := Seq(scalaVersion.value, "2.10.6")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.3.0"