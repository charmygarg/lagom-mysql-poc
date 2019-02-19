import sbt._

object Dependencies {
  lazy val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
  lazy val typesafeConf = "com.typesafe" % "config" % "1.3.0"
  lazy val mockito = "org.mockito" % "mockito-all" % "1.10.19" % Test
  lazy val filterHelper = "com.typesafe.play" %% "filters-helpers" % "2.6.12"
  lazy val mySqlConnector = "mysql" % "mysql-connector-java" % "8.0.15"
}
