import Dependencies._
import ProjectInfo._
import kevinlee.sbt.SbtCommon.crossVersionProps
import kevinlee.semver.{Major, Minor, SemanticVersion}
import org.scoverage.coveralls.Imports.CoverallsKeys._

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
  , crossScalaVersions := CrossScalaVersions
  , scalacOptions :=
      crossVersionProps(Seq.empty, SemanticVersion.parseUnsafe(scalaVersion.value)) {
        case (Major(2), Minor(12)) =>
          scalacOptions.value ++ commonScalacOptions
        case (Major(2), Minor(11)) =>
          (scalacOptions.value ++ commonScalacOptions).filter(_ != "-Ywarn-unused-import")
        case _ =>
          (scalacOptions.value ++ commonScalacOptions)
            .filter(option =>
              option != "-Ywarn-unused-import" && option != "-Ywarn-numeric-widen" 
            )
      }.distinct
  , wartremoverErrors in (Compile, compile) ++= commonWarts
  , wartremoverErrors in (Test, compile) ++= commonWarts
  , resolvers += hedgehogRepo
  , libraryDependencies ++= hedgehogLibs
  , dependencyOverrides ++= crossVersionProps(Seq.empty[ModuleID], SemanticVersion.parseUnsafe(scalaVersion.value)) {
      case (Major(2), Minor(10)) =>
        Seq("org.wartremover" %% "wartremover" % "2.3.7")
      case x =>
        Seq.empty
    }
  , testFrameworks ++= Seq(TestFramework("hedgehog.sbt.Framework"))

  /* Bintray { */
  , bintrayPackageLabels := Seq("Scala", "SemanticVersion", "SemVer")
  , bintrayVcsUrl := Some("""git@github.com:Kevin-Lee/just-semver.git""")
  /* } Bintray */

  , initialCommands in console := """import kevinlee.semver._"""

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
