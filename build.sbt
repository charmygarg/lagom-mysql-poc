import Dependencies._

name := "lagom-mysql-poc"

version := "0.1"

scalaVersion := "2.12.8"

lagomKafkaEnabled in ThisBuild := false

lazy val `cassandra-to-sql-poc` = (project in file("."))
  .aggregate(`user-api`, `user-impl`)

lazy val `user-api` = (project in file("user-api"))
  .settings(libraryDependencies ++= Seq(lagomScaladslApi))

lazy val `user-impl` = (project in file("user-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(lagomScaladslTestKit, macwire, filterHelper, scalaTest,
      lagomScaladslPersistenceJdbc, lagomScaladslApi, mockito, mySqlConnector, typesafeConf))
  .dependsOn(`user-api`)

lagomCassandraEnabled in ThisBuild := false
