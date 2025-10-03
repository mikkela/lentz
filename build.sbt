import sbt._
import Keys._

ThisBuild / version         := "0.1.0-SNAPSHOT"
ThisBuild / organization    := "org.mikadocs"
ThisBuild / scalaVersion    := "3.7.3"

lazy val fastparseV = "3.1.1"
lazy val munitV     = "1.1.1"

lazy val root = (project in file("."))
  .settings(
    name := "lentz",
    idePackagePrefix := Some("org.mikadadocs.lentz"),
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "fastparse" % fastparseV,
      "org.scalameta" %% "munit" % munitV % Test
    ),
    Test / testFrameworks += new TestFramework("munit.Framework"),
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Wnonunit-statement",
      "-Wvalue-discard",
      "-Xfatal-warnings"
    ),
    Compile / console / scalacOptions --= Seq("-Xfatal-warnings")
  )
