name := "redis-client"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.6"

resolvers += (
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
)

libraryDependencies ++= Seq(
//  "redis.clients" % "jedis" % "2.7.2",
  "biz.paluch.redis" % "lettuce" % "3.2.Final",
  "javax.inject" % "javax.inject" % "1",
  "com.google.inject.extensions" % "guice-assistedinject" % "4.0"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
