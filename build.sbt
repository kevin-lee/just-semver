import ProjectInfo._
import kevinlee.sbt.SbtCommon.crossVersionProps
import just.semver.SemVer
import SemVer.{Major, Minor}
import org.scoverage.coveralls.Imports.CoverallsKeys._

ThisBuild / scalaVersion := props.ProjectScalaVersion
ThisBuild / organization := "io.kevinlee"
ThisBuild / version      := ProjectVersion
ThisBuild / crossScalaVersions := props.CrossScalaVersions
ThisBuild / developers   := List(
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
        url("https://github.com/Kevin-Lee/just-semver")
      , "git@github.com:Kevin-Lee/just-semver.git"
    ).some

lazy val justSemVer = (project in file("."))
  .enablePlugins(DevOopsGitReleasePlugin)
  .settings(
    name         := "just-semver",
    description  := "Semantic Versioning (SemVer) for Scala",
    scalacOptions := (SemVer.parseUnsafe(scalaVersion.value) match {
      case SemVer(SemVer.Major(2), SemVer.Minor(13), SemVer.Patch(patch), _, _) =>
        val options = scalacOptions.value
        if (patch >= 3)
          options.filterNot(_ == "-Xlint:nullary-override")
        else
          options
      case _: SemVer =>
        scalacOptions.value
    }),
    scalacOptions := (isDotty.value match {
      case true =>
        Seq(
          "-source:3.0-migration",
          props.scala3cLanguageOptions,
          "-Ykind-projector",
        )
      case false =>
        scalacOptions.value
    }),
    unmanagedSourceDirectories in Compile ++= {
      val sharedSourceDir = (baseDirectory in ThisBuild).value / "src/main"
      if (isDotty.value)
        Seq(sharedSourceDir / "scala-3")
      else if (scalaVersion.value.startsWith("2.13") || scalaVersion.value.startsWith("2.12")) 
        Seq(sharedSourceDir / "scala-2.12_2.13")
      else
        Seq(sharedSourceDir / "scala-2.10_2.11")
    },
    resolvers += props.hedgehogRepo,
    libraryDependencies := Seq(libs.justFp) ++
      crossVersionProps(Seq.empty[ModuleID], SemVer.parseUnsafe(scalaVersion.value)) {
        case (Major(2), Minor(10)) =>
          libs.hedgehogLibs(props.hedgehogVersionFor2_10) ++
          libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover")
        case x =>
          libs.hedgehogLibs(props.hedgehogVersion) ++ libraryDependencies.value
      },
  /* Ammonite-REPL { */
    libraryDependencies ++=
      (scalaBinaryVersion.value match {
        case "2.12" | "2.13" =>
          Seq("com.lihaoyi" % "ammonite" % "2.2.0" % Test cross CrossVersion.full)
        case "2.11" =>
          Seq("com.lihaoyi" % "ammonite" % "1.6.7" % Test cross CrossVersion.full)
        case "2.10" =>
          Seq.empty[ModuleID]
        case _ =>
          Seq.empty[ModuleID]
      }),
    libraryDependencies := (
      if (isDotty.value) {
        libraryDependencies.value
          .filterNot(props.removeDottyIncompatible)
      } else {
        libraryDependencies.value
      }),
    libraryDependencies := libraryDependencies.value.map(_.withDottyCompat(scalaVersion.value)),
    sourceGenerators in Test +=
      (scalaBinaryVersion.value match {
        case "2.11" | "2.12" | "2.13" =>
          task {
            val file = (sourceManaged in Test).value / "amm.scala"
            IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
            Seq(file)
          }
        case "2.10" =>
          task(Seq.empty[File])
        case _ =>
          task(Seq.empty[File])
      }),
  /* } Ammonite-REPL */
  /* WartRemover and scalacOptions { */
//      wartremoverErrors in (Compile, compile) ++= commonWarts((scalaBinaryVersion in update).value),
//      wartremoverErrors in (Test, compile) ++= commonWarts((scalaBinaryVersion in update).value),
  wartremoverErrors ++= commonWarts((scalaBinaryVersion in update).value),
    //      wartremoverErrors ++= Warts.all,
    Compile / console / wartremoverErrors := List.empty,
    Compile / console / wartremoverWarnings := List.empty,
    Compile / console / scalacOptions :=
      (console / scalacOptions).value
        .filterNot(option =>
          option.contains("wartremover") || option.contains("import")
        ),
    Test / console / wartremoverErrors := List.empty,
    Test / console / wartremoverWarnings := List.empty,
    Test / console / scalacOptions :=
      (console / scalacOptions).value
        .filterNot( option =>
          option.contains("wartremover") || option.contains("import")
        ),
    /* } WartRemover and scalacOptions */
  testFrameworks ++= Seq(TestFramework("hedgehog.sbt.Framework")),

  /* Bintray { */
  bintrayPackageLabels := Seq("Scala", "SemanticVersion", "SemVer"),
  bintrayVcsUrl := """git@github.com:Kevin-Lee/just-semver.git""".some,
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  /* } Bintray */

  console / initialCommands := """import just.semver.SemVer""",

  /* Coveralls { */
  coverageHighlighting := (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 10)) =>
      false
    case _ =>
      true
  }),
  coverallsTokenFile := s"""${Path.userHome.absolutePath}/.coveralls-credentials""".some,
  /* } Coveralls */

)

lazy val props = new {
  val removeDottyIncompatible: ModuleID => Boolean =
    m =>
      m.name == "wartremover" ||
        m.name == "ammonite" ||
        m.name == "kind-projector" ||
        m.name == "better-monadic-for" ||
        m.name == "mdoc"

  val ProjectScalaVersion: String = "3.0.0-RC1"
  val CrossScalaVersions: Seq[String] = Seq(
    "2.10.7",
    "2.11.12",
    "2.12.12",
    "2.13.3",
    "3.0.0-M1",
    "3.0.0-M2",
    "3.0.0-M3",
    ProjectScalaVersion,
  ).distinct

  val hedgehogVersionFor2_10 = "7bd29241fababd9a3e954fd38083ed280fc9e4e8"
  val hedgehogVersion = "0.6.5"

  val hedgehogRepo: MavenRepository =
    "bintray-scala-hedgehog" at "https://dl.bintray.com/hedgehogqa/scala-hedgehog"

  lazy val scala3cLanguageOptions = "-language:" + List(
    "dynamics",
    "existentials",
    "higherKinds",
    "reflectiveCalls",
    "experimental.macros",
    "implicitConversions"
  ).mkString(",")

}

lazy val libs = new {

  def hedgehogLibs(hedgehogVersion: String): Seq[ModuleID] = Seq(
    "qa.hedgehog" %% "hedgehog-core" % hedgehogVersion % Test,
    "qa.hedgehog" %% "hedgehog-runner" % hedgehogVersion % Test,
    "qa.hedgehog" %% "hedgehog-sbt" % hedgehogVersion % Test,
  )

  lazy val justFp: ModuleID = "io.kevinlee" %% "just-fp" % "1.3.5"

}
