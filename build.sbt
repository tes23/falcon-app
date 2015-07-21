name := "falcon-app"

version := "1.0-SNAPSHOT"

import com.typesafe.sbt.packager.archetypes.ServerLoader

//lazy val common = (project in file("common")).enablePlugins(PlayJava)
//lazy val redisClient = (project in file("redisClient")).enablePlugins(PlayJava)

lazy val root = (project in file("."))
  .enablePlugins(PlayJava, JavaServerAppPackaging)
//  .aggregate(redisClient)
//  .dependsOn(redisClient)

scalaVersion := "2.11.6"

resolvers += (
  "Local Maven Repository" at "file:///"+Path.userHome.absolutePath+"/.m2/repository"
  )

libraryDependencies ++= Seq(
  javaCore,
//  javaJdbc,
//  cache,
//  javaWs,
  "javax.inject" % "javax.inject" % "1",
  "com.google.inject.extensions" % "guice-assistedinject" % "4.0",
  "biz.paluch.redis" % "lettuce" % "3.2.Final"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

serverLoading in Debian := ServerLoader.Upstart

maintainer in Linux := "Akos Csikor <akos.csikor@cyber-design.info>"

packageSummary in Linux := "Falcon App"

packageDescription := "Simple scalable application"

//lazy val redisClient = project
//lazy val common = project
