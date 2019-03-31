name := "BarbeQ-Nation"

maintainer := "anurag@knoldus.com"
organization in ThisBuild := "com.knoldus"
version in ThisBuild := "1.0.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion := "2.12.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val mockito = "org.mockito" % "mockito-all" % "1.8.4" % Test

lazy val `barbeQ` = (project in file("."))
  .aggregate(`barbeQ-api`, `barbeQ-impl`)


lazy val `barbeQ-api` = (project in file("barbeQ-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `barbeQ-impl` = (project in file("barbeQ-impl"))
  .enablePlugins(LagomScala, JavaAppPackaging, JavaServerAppPackaging)
   .settings(
//     coverageExcludedPackages := "<empty>;com.knoldus.trailblazer.TrailBlazerLoader; com.knoldus.trailblazer.TrailBlazerApplication",
     coverageMinimum := 90,
     coverageFailOnMinimum := true
   )
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest,
      mockito
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`barbeQ-api`)

lagomKafkaEnabled in ThisBuild := false
lagomCassandraEnabled in ThisBuild := false
lagomUnmanagedServices in ThisBuild := Map("cas_native" -> "http://localhost:9042")
