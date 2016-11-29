import play.routes.compiler.InjectedRoutesGenerator
import sbt._
import Keys._
import play.sbt.Play.autoImport._
import play.sbt.routes.RoutesKeys._

object ApplicationBuild extends Build {

  def getEnv(env:String):Option[String] = Option(System.getenv(env))

  val appName = "playframework-with-reactjs-boilerplate"
  val appVersion = "1.0-SNAPSHOT-" + getEnv("BUILD_NUMBER").map{ _+getEnv("GIT_COMMIT").map("_"+_).getOrElse("")}.getOrElse("local")

  /** 依存ライブラリ */
  val appDependencies = Seq(
    jdbc,
    cache,
    ws,
    specs2 % Test,
    "com.amazonaws" % "aws-java-sdk" % "1.9.7",
    "mysql" % "mysql-connector-java" % "5.1.24",
    "org.scalikejdbc" %% "scalikejdbc" % "2.4.2",
    "org.scalikejdbc" %% "scalikejdbc-config" % "2.4.2",
    "org.scalikejdbc" %% "scalikejdbc-test" % "2.4.2" % "test",
    "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.4.1",
    "org.json4s" %% "json4s-native" % "3.3.0",
    "org.json4s" %% "json4s-ext" % "3.3.0",
    "com.github.tototoshi" %% "play-json4s-native" % "0.5.0",
    "com.github.tototoshi" %% "play-json4s-test-native" % "0.5.0" % "test",
    "org.flywaydb" %% "flyway-play" % "3.0.1",
    "com.nulab-inc" %% "play2-oauth2-provider" % "1.0.0",
    "ch.qos.logback.contrib" % "logback-json-core" % "0.1.5",
    "ch.qos.logback.contrib" % "logback-json-classic" % "0.1.5",
    "ch.qos.logback.contrib" % "logback-jackson" % "0.1.5",
    "com.fasterxml.jackson.core" % "jackson-core" % "2.8.3",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.3",
    "com.fasterxml.jackson.core" % "jackson-annotations" % "2.8.3",
    "com.amazonaws" % "aws-java-sdk" % "1.11.46",
    "io.github.cloudify" %% "spdf" % "1.4.0"
  )

  val main = Project(appName, file(".")).enablePlugins(play.sbt.PlayScala)
    .settings(Gulp.gulpSettings:_*)
    .settings(Beanstalk.ebSettings:_*)
    .settings(scalikejdbc.mapper.SbtPlugin.scalikejdbcSettings)
    .settings(
      version := appVersion,
      libraryDependencies ++= appDependencies,
      scalaVersion := "2.11.7",
      resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
      routesGenerator := InjectedRoutesGenerator,
      PlayKeys.devSettings := Seq("play.server.http.port" -> "9000")
    )
}
