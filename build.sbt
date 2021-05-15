import ProjectInfo._
import kevinlee.sbt.SbtCommon.crossVersionProps
import just.semver.SemVer
import SemVer.{Major, Minor}
import org.scoverage.coveralls.Imports.CoverallsKeys._

ThisBuild / scalaVersion := props.ProjectScalaVersion
ThisBuild / organization := "io.kevinlee"
ThisBuild / crossScalaVersions := props.CrossScalaVersions
ThisBuild / developers := List(
  Developer(
    "Kevin-Lee",
    "Kevin Lee",
    "kevin.code@kevinlee.io",
    url("https://github.com/Kevin-Lee"),
  )
)
ThisBuild / homepage := url("https://github.com/Kevin-Lee/just-semver").some
ThisBuild / scmInfo :=
  ScmInfo(
    url("https://github.com/Kevin-Lee/just-semver"),
    "git@github.com:Kevin-Lee/just-semver.git"
  ).some
ThisBuild / licenses := List("MIT" -> url("http://opensource.org/licenses/MIT"))

lazy val justSemVer = (project in file("."))
  .enablePlugins(DevOopsGitHubReleasePlugin)
  .settings(
    name := "just-semver",
    description := "Semantic Versioning (SemVer) for Scala",
    scalacOptions := (scalaBinaryVersion.value match {
      case "3" =>
        props.scala3cOptions
      case _   =>
        scalacOptions.value
    }),
    Compile / unmanagedSourceDirectories ++= {
      val sharedSourceDir = (ThisBuild / baseDirectory).value / "src/main"
      if (isScala3(scalaVersion.value))
        Seq(sharedSourceDir / "scala-3")
      else if (scalaVersion.value.startsWith("2.13") || scalaVersion.value.startsWith("2.12"))
        Seq(sharedSourceDir / "scala-2.12_2.13")
      else
        Seq(sharedSourceDir / "scala-2.10_2.11")
    },
    libraryDependencies := Seq(libs.justFp) ++
      crossVersionProps(Seq.empty[ModuleID], SemVer.parseUnsafe(scalaVersion.value)) {
        case (Major(3), _, _) =>
          libs.hedgehogLibs(props.hedgehogVersion) ++
            libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover")
        case x                =>
          libs.hedgehogLibs(props.hedgehogVersion) ++ libraryDependencies.value
      },
    /* Ammonite-REPL { */
    libraryDependencies ++=
      (scalaBinaryVersion.value match {
        case "2.12" | "2.13" =>
          Seq("com.lihaoyi" % "ammonite" % "2.3.8-58-aa8b2ab1" % Test cross CrossVersion.full)
        case "2.11"          =>
          Seq("com.lihaoyi" % "ammonite" % "1.6.7" % Test cross CrossVersion.full)
        case _               =>
          Seq.empty[ModuleID]
      }),
    libraryDependencies := (
      if (isScala3(scalaVersion.value)) {
        libraryDependencies
          .value
          .filterNot(props.removeDottyIncompatible)
      } else {
        libraryDependencies.value
      }
    ),
    Test / sourceGenerators +=
      (scalaBinaryVersion.value match {
        case "2.11" | "2.12" | "2.13" =>
          task {
            val file = (Test / sourceManaged).value / "amm.scala"
            IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
            Seq(file)
          }
        case _                        =>
          task(Seq.empty[File])
      }),
    /* } Ammonite-REPL */
    /* WartRemover and scalacOptions { */
//      Compile / compile / wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),
//      Test / compile / wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),
    wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),
    //      wartremoverErrors ++= Warts.all,
    Compile / console / wartremoverErrors := List.empty,
    Compile / console / wartremoverWarnings := List.empty,
    Compile / console / scalacOptions :=
      (console / scalacOptions)
        .value
        .filterNot(option => option.contains("wartremover") || option.contains("import")),
    Test / console / wartremoverErrors := List.empty,
    Test / console / wartremoverWarnings := List.empty,
    Test / console / scalacOptions :=
      (console / scalacOptions)
        .value
        .filterNot(option => option.contains("wartremover") || option.contains("import")),
    /* } WartRemover and scalacOptions */
    testFrameworks ~= (testFws => (TestFramework("hedgehog.sbt.Framework") +: testFws).distinct),
    licenses := List("MIT" -> url("http://opensource.org/licenses/MIT")),
    console / initialCommands := """import just.semver.SemVer""",
    /* Coveralls { */
    coverageHighlighting := (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 10)) =>
        false
      case _             =>
        true
    }),
    coverallsTokenFile := s"""${Path.userHome.absolutePath}/.coveralls-credentials""".some,
    /* } Coveralls */

  )

lazy val props =
  new {
    val removeDottyIncompatible: ModuleID => Boolean =
      m =>
        m.name == "wartremover" ||
          m.name == "ammonite" ||
          m.name == "kind-projector" ||
          m.name == "better-monadic-for" ||
          m.name == "mdoc"

    final val ProjectScalaVersion: String     = "3.0.0"
    final val CrossScalaVersions: Seq[String] =
      Seq(
        "2.11.12",
        "2.12.13",
        "2.13.5",
        ProjectScalaVersion,
      ).distinct

    final val hedgehogVersion = "0.7.0"

    final val scala3cLanguageOptions =
      "-language:" + List(
        "dynamics",
        "existentials",
        "higherKinds",
        "reflectiveCalls",
        "experimental.macros",
        "implicitConversions"
      ).mkString(",")

    final val scala3cOptions = List(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-Xfatal-warnings",
      scala3cLanguageOptions,
      "-explain",
    )

  }

lazy val libs =
  new {

    def hedgehogLibs(hedgehogVersion: String): Seq[ModuleID] = Seq(
      "qa.hedgehog" %% "hedgehog-core"   % hedgehogVersion % Test,
      "qa.hedgehog" %% "hedgehog-runner" % hedgehogVersion % Test,
      "qa.hedgehog" %% "hedgehog-sbt"    % hedgehogVersion % Test,
    )

    lazy val justFp: ModuleID = "io.kevinlee" %% "just-fp-core" % "1.6.0"

  }

def isScala3(scalaVersion: String): Boolean = scalaVersion.startsWith("3.")
