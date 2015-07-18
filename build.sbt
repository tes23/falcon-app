name := "falcon-app"

version := "1.0-SNAPSHOT"

//lazy val common = (project in file("common")).enablePlugins(PlayJava)
//lazy val redisClient = (project in file("redisClient")).enablePlugins(PlayJava)

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .aggregate(redisClient)
  .dependsOn(redisClient)
//  .aggregate(common)
//  .dependsOn(common)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

lazy val redisClient = project
//lazy val common = project
