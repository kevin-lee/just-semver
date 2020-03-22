import ProjectInfo._
import kevinlee.sbt.SbtCommon.crossVersionProps
import just.semver.SemVer, SemVer.{Major, Minor}
import org.scoverage.coveralls.Imports.CoverallsKeys._

val ProjectScalaVersion: String = "2.13.1"
val CrossScalaVersions: Seq[String] = Seq("2.10.7", "2.11.12", "2.12.11", ProjectScalaVersion)

ThisBuild / scalaVersion := ProjectScalaVersion
ThisBuild / organization := "io.kevinlee"
ThisBuild / version      := ProjectVersion
ThisBuild / crossScalaVersions := CrossScalaVersions
ThisBuild / developers   := List(
    Developer("Kevin-Lee", "Kevin Lee", "kevin.code@kevinlee.io", url("https://github.com/Kevin-Lee"))
  )
ThisBuild / homepage := Some(url("https://github.com/Kevin-Lee/just-semver"))
ThisBuild / scmInfo :=
    Some(ScmInfo(
        url("https://github.com/Kevin-Lee/just-semver")
      , "git@github.com:Kevin-Lee/just-semver.git"
    ))

lazy val hedgehogVersion = "6dba7c9ba065e423000e9aa2b6981ce3d70b74cb"
lazy val hedgehogRepo =
  "bintray-scala-hedgehog" at "https://dl.bintray.com/hedgehogqa/scala-hedgehog"

lazy val hedgehogLibs: Seq[ModuleID] = Seq(
  "hedgehog" %% "hedgehog-core" % hedgehogVersion
  , "hedgehog" %% "hedgehog-runner" % hedgehogVersion
  , "hedgehog" %% "hedgehog-sbt" % hedgehogVersion
).map(_ % Test)

lazy val justFp: ModuleID = "io.kevinlee" %% "just-fp" % "1.3.4"

lazy val justSemVer = (project in file("."))
  .enablePlugins(DevOopsGitReleasePlugin)
  .settings(
    name         := "just-semver"
  , description  := "Semantic Versioning (SemVer) for Scala"
  , unmanagedSourceDirectories in Compile ++= {
      val sharedSourceDir = (baseDirectory in ThisBuild).value / "src/main"
      if (scalaVersion.value.startsWith("2.13") || scalaVersion.value.startsWith("2.12"))
        Seq(sharedSourceDir / "scala-2.12_2.13")
      else
        Seq(sharedSourceDir / "scala-2.10_2.11")
    }
  , resolvers += hedgehogRepo
  , libraryDependencies := hedgehogLibs ++ Seq(justFp) ++
      crossVersionProps(Seq.empty[ModuleID], SemVer.parseUnsafe(scalaVersion.value)) {
        case (Major(2), Minor(10)) =>
          libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover")
        case x =>
          libraryDependencies.value
      }
  /* Ammonite-REPL { */
  , libraryDependencies ++=
      (scalaBinaryVersion.value match {
        case "2.10" =>
          Seq("com.lihaoyi" % "ammonite" % "1.0.3" % Test cross CrossVersion.full)
          Seq.empty[ModuleID]
        case "2.11" =>
          Seq("com.lihaoyi" % "ammonite" % "1.6.7" % Test cross CrossVersion.full)
        case "2.12" =>
          Seq.empty[ModuleID] // TODO: add ammonite when it supports Scala 2.12.11
        case _ =>
          Seq("com.lihaoyi" % "ammonite" % "1.7.4" % Test cross CrossVersion.full)
      })
  , sourceGenerators in Test +=
      (scalaBinaryVersion.value match {
        case "2.10" =>
          task(Seq.empty[File])
        case "2.12" =>
          task(Seq.empty[File]) // TODO: add ammonite when it supports Scala 2.12.11
        case _ =>
          task {
            val file = (sourceManaged in Test).value / "amm.scala"
            IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
            Seq(file)
          }
      })
  /* } Ammonite-REPL */
  , wartremoverErrors in (Compile, compile) ++= commonWarts((scalaBinaryVersion in update).value)
  , wartremoverErrors in (Test, compile) ++= commonWarts((scalaBinaryVersion in update).value)
  , testFrameworks ++= Seq(TestFramework("hedgehog.sbt.Framework"))

  /* Bintray { */
  , bintrayPackageLabels := Seq("Scala", "SemanticVersion", "SemVer")
  , bintrayVcsUrl := Some("""git@github.com:Kevin-Lee/just-semver.git""")
  , licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
  /* } Bintray */

  , initialCommands in console := """import just.semver.SemVer"""

  /* Coveralls { */
  , coverageHighlighting := (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 10)) =>
      false
    case _ =>
      true
  })
  , coverallsTokenFile := Option(s"""${Path.userHome.absolutePath}/.coveralls-credentials""")
  /* } Coveralls */

)
