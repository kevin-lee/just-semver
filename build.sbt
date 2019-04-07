import Dependencies._
import ProjectInfo._
import BuildTools._

lazy val root = (project in file("."))
  .settings(
    organization := "kevinlee"
  , name         := "just-semver"
  , scalaVersion := ProjectScalaVersion
  , version      := ProjectVersion
  , description  := "Semantic Versioning (SemVer) for Scala"
  , developers   := List(
      Developer("Kevin-Lee", "Kevin Lee", "kevin.code@kevinlee.io", url("https://github.com/Kevin-Lee"))
    )
  , scalacOptions ++=
      crossVersionProps(commonScalacOptions, scalaVersion.value) {
        case Some((2, 12)) =>
          Seq("-Ywarn-unused-import")
        case _ =>
          Nil
      }
  , wartremoverErrors in (Compile, compile) ++= commonWarts
  , wartremoverErrors in (Test, compile) ++= commonWarts
  , resolvers += hedgehogRepo
  , libraryDependencies ++= hedgehogLibs
  , testFrameworks ++= Seq(TestFramework("hedgehog.sbt.Framework"))
  , addSbtPlugin(wartRemover)
  , addSbtPlugin(scoverage)
  , bintrayPackageLabels := Seq("Scala", "SemanticVersion")
  , bintrayVcsUrl := Some("""git@github.com:Kevin-Lee/just-semver.git""")
  , initialCommands in console := """import kevinlee.semver._"""


)
