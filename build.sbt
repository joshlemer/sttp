import org.scalajs.sbtplugin.cross.CrossProject

val commonSettings = Seq(
  organization := "com.softwaremill.sttp",
  scalaVersion := "2.12.4",
  crossScalaVersions := Seq(scalaVersion.value, "2.11.11"),
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xlint"),
  scalafmtOnCompile := true,
  scalafmtVersion := "1.3.0",
  // publishing
  publishTo := Some(
    if (isSnapshot.value)
      Opts.resolver.sonatypeSnapshots
    else
      Opts.resolver.sonatypeStaging
  ),
  publishArtifact in Test := false,
  publishMavenStyle := true,
  scmInfo := Some(
    ScmInfo(url("https://github.com/softwaremill/sttp"),
            "scm:git:git@github.com/softwaremill/sttp.git")),
  developers := List(
    Developer("adamw", "Adam Warski", "", url("https://softwaremill.com"))),
  licenses := ("Apache-2.0",
               url("http://www.apache.org/licenses/LICENSE-2.0.txt")) :: Nil,
  homepage := Some(url("http://softwaremill.com/open-source")),
  sonatypeProfileName := "com.softwaremill",
  // sbt-release
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  releaseIgnoreUntrackedFiles := true,
  releaseProcess := SttpRelease.steps,
  // silence transitivie eviction warnings
  evictionWarningOptions in update := EvictionWarningOptions.default
    .withWarnTransitiveEvictions(false)
)

val akkaHttpVersion = "10.0.10"
val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion

val monixVersion = "2.3.2"
val monix = "io.monix" %% "monix" % monixVersion

val circeVersion = "0.8.0"

val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4"

lazy val rootProject = (project in file("."))
//  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings: _*)
  .settings(publishArtifact := false, name := "sttp")
  .aggregate(
    coreJVM,
    coreJS,
    akkaHttpBackend,
    asyncHttpClientBackend,
    asyncHttpClientFutureBackend,
    asyncHttpClientScalazBackend,
    asyncHttpClientMonixBackend,
    asyncHttpClientCatsBackend,
    asyncHttpClientFs2Backend,
    okhttpBackend,
    okhttpMonixBackend,
    circeJVM,
    circeJS,
    json4s,
    testsJVM,
    testsJS
  )

lazy val core: CrossProject = (crossProject in file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      "org.scalacheck" %% "scalacheck" % "1.13.5" % "test",
      "org.scalatest" %%% "scalatest" % "3.0.4" % "test",
      scalaTest % "test"
    )
  )

lazy val coreJVM = core.jvm

lazy val coreJS = core.js

lazy val akkaHttpBackend: Project = (project in file("akka-http-backend"))
  .settings(commonSettings: _*)
  .settings(
    name := "akka-http-backend",
    libraryDependencies ++= Seq(
      akkaHttp
    )
  ) dependsOn coreJVM

lazy val asyncHttpClientBackend: Project = (project in file(
  "async-http-client-backend"))
  .settings(commonSettings: _*)
  .settings(
    name := "async-http-client-backend",
    libraryDependencies ++= Seq(
      "org.asynchttpclient" % "async-http-client" % "2.0.37"
    )
  ) dependsOn coreJVM

lazy val asyncHttpClientFutureBackend: Project = (project in file(
  "async-http-client-backend/future"))
  .settings(commonSettings: _*)
  .settings(
    name := "async-http-client-backend-future"
  ) dependsOn asyncHttpClientBackend

lazy val asyncHttpClientScalazBackend: Project = (project in file(
  "async-http-client-backend/scalaz"))
  .settings(commonSettings: _*)
  .settings(
    name := "async-http-client-backend-scalaz",
    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-concurrent" % "7.2.16"
    )
  ) dependsOn asyncHttpClientBackend

lazy val asyncHttpClientMonixBackend: Project = (project in file(
  "async-http-client-backend/monix"))
  .settings(commonSettings: _*)
  .settings(
    name := "async-http-client-backend-monix",
    libraryDependencies ++= Seq(monix)
  ) dependsOn asyncHttpClientBackend

lazy val asyncHttpClientCatsBackend: Project = (project in file(
  "async-http-client-backend/cats"))
  .settings(commonSettings: _*)
  .settings(
    name := "async-http-client-backend-cats",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "0.5"
    )
  ) dependsOn asyncHttpClientBackend

lazy val asyncHttpClientFs2Backend: Project = (project in file(
  "async-http-client-backend/fs2"))
  .settings(commonSettings: _*)
  .settings(
    name := "async-http-client-backend-fs2",
    libraryDependencies ++= Seq(
      "com.github.zainab-ali" %% "fs2-reactive-streams" % "0.2.5"
    )
  ) dependsOn asyncHttpClientBackend

lazy val okhttpBackend: Project = (project in file("okhttp-backend"))
  .settings(commonSettings: _*)
  .settings(
    name := "okhttp-backend",
    libraryDependencies ++= Seq(
      "com.squareup.okhttp3" % "okhttp" % "3.9.0"
    )
  ) dependsOn coreJVM

lazy val okhttpMonixBackend: Project = (project in file("okhttp-backend/monix"))
  .settings(commonSettings: _*)
  .settings(
    name := "okhttp-backend-monix",
    libraryDependencies ++= Seq(monix)
  ) dependsOn okhttpBackend

lazy val circe: CrossProject = (crossProject in file("json/circe"))
  .settings(commonSettings: _*)
  .settings(
    name := "circe",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion,
      scalaTest % "test"
    )
  ) dependsOn core

lazy val circeJVM = circe.jvm

lazy val circeJS = circe.js

lazy val json4s: Project = (project in file("json/json4s"))
  .settings(commonSettings: _*)
  .settings(
    name := "json4s",
    libraryDependencies ++= Seq(
      "org.json4s" %% "json4s-native" % "3.5.3",
      scalaTest % "test"
    )
  ) dependsOn coreJVM

lazy val tests: CrossProject = (crossProject in file("tests"))
  .settings(commonSettings: _*)
  .settings(
    publishArtifact := false,
    name := "tests",
    libraryDependencies ++= Seq(
      akkaHttp,
      scalaTest,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
      "com.github.pathikrit" %% "better-files" % "2.17.1",
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    ).map(_ % "test"),
    libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value % "test"
  ) dependsOn core

lazy val testsJVM = tests.jvm.dependsOn(
  akkaHttpBackend,
  asyncHttpClientFutureBackend,
  asyncHttpClientScalazBackend,
  asyncHttpClientMonixBackend,
  asyncHttpClientCatsBackend,
  asyncHttpClientFs2Backend,
  okhttpMonixBackend
)

lazy val testsJS = tests.js
