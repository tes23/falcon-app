name := "redis-client"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.6"

resolvers += (
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
)

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
