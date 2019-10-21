import Dependencies._
import ProjectInfo._
import kevinlee.sbt.SbtCommon.crossVersionProps
import kevinlee.semver.{Major, Minor, SemanticVersion}
import org.scoverage.coveralls.Imports.CoverallsKeys._

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

lazy val justSemVer = (project in file("."))
  .settings(
    name         := "just-semver"
  , description  := "Semantic Versioning (SemVer) for Scala"
  , scalacOptions :=
      crossVersionProps(Seq.empty, SemanticVersion.parseUnsafe(scalaVersion.value)) {
        case (Major(2), Minor(10)) =>
          scalacOptions.value.filter(option => option != "-Ywarn-numeric-widen")
        case _ =>
          scalacOptions.value
      }.distinct
  , unmanagedSourceDirectories in Compile ++= {
      val sharedSourceDir = (baseDirectory in ThisBuild).value / "src/main"
      if (scalaVersion.value.startsWith("2.13") || scalaVersion.value.startsWith("2.12"))
        Seq(sharedSourceDir / "scala-2.12_2.13")
      else
        Seq(sharedSourceDir / "scala-2.10_2.11")
    }
  , resolvers += hedgehogRepo
  , libraryDependencies := hedgehogLibs ++ Seq(justFp) ++
      crossVersionProps(Seq.empty[ModuleID], SemanticVersion.parseUnsafe(scalaVersion.value)) {
        case (Major(2), Minor(10)) =>
          libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover")
        case x =>
          libraryDependencies.value
      }
  /* Ammonite-REPL { */
  , libraryDependencies ++=
      (scalaBinaryVersion.value match {
        case "2.10" =>
          Seq.empty[ModuleID]
        case "2.11" =>
          Seq("com.lihaoyi" % "ammonite" % "1.6.7" % Test cross CrossVersion.full)
        case _ =>
          Seq("com.lihaoyi" % "ammonite" % "1.7.4" % Test cross CrossVersion.full)
      })
  , sourceGenerators in Test +=
      (scalaBinaryVersion.value match {
        case "2.10" =>
          task(Seq.empty[File])
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
