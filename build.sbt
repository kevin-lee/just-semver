import ProjectInfo._
import just.semver.SemVer
import SemVer.Major
import kevinlee.sbt.SbtCommon.crossVersionProps

ThisBuild / scalaVersion       := props.ProjectScalaVersion
ThisBuild / organization       := "io.kevinlee"
ThisBuild / crossScalaVersions := props.CrossScalaVersions

ThisBuild / developers := List(
  Developer(
    "Kevin-Lee",
    "Kevin Lee",
    "kevin.code@kevinlee.io",
    url("https://github.com/Kevin-Lee")
  )
)
ThisBuild / homepage   := url("https://github.com/Kevin-Lee/just-semver").some
ThisBuild / scmInfo    :=
  ScmInfo(
    url("https://github.com/Kevin-Lee/just-semver"),
    "git@github.com:Kevin-Lee/just-semver.git"
  ).some
ThisBuild / licenses   := List("MIT" -> url("http://opensource.org/licenses/MIT"))

ThisBuild / resolvers += "sonatype-snapshots" at s"https://${props.SonatypeCredentialHost}/content/repositories/snapshots"

lazy val justSemVer = (project in file("."))
  .enablePlugins(DevOopsGitHubReleasePlugin)
  .settings(
    name                                    := "just-semver",
    description                             := "Semantic Versioning (SemVer) for Scala",
    Compile / unmanagedSourceDirectories    := {
      val sharedSourceDir = baseDirectory.value / "src/main"
      val moreSrcs        =
        if (scalaVersion.value.startsWith("2.13") || scalaVersion.value.startsWith("2.12"))
          Seq(sharedSourceDir / "scala-2.12_2.13")
        else
          Seq.empty[File]
      ((Compile / unmanagedSourceDirectories).value ++ moreSrcs).distinct
    },
//    useAggressiveScalacOptions := true,
    libraryDependencies                     :=
      crossVersionProps(Seq.empty[ModuleID], SemVer.parseUnsafe(scalaVersion.value)) {
        case (Major(3), _, _) =>
          libs.hedgehogLibs(props.hedgehogVersion) ++
            libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover")
        case x                =>
          libs.hedgehogLibs(props.hedgehogVersion) ++ libraryDependencies.value
      },
    libraryDependencies                     := (
      if (isScala3(scalaVersion.value)) {
        libraryDependencies
          .value
          .filterNot(props.removeDottyIncompatible)
      } else {
        libraryDependencies.value
      }
    ),
    /* WartRemover and scalacOptions { */
//      Compile / compile / wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),
//      Test / compile / wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),
    wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),
    //      wartremoverErrors ++= Warts.all,
    Compile / console / wartremoverErrors   := List.empty,
    Compile / console / wartremoverWarnings := List.empty,
    Compile / console / scalacOptions       :=
      (console / scalacOptions)
        .value
        .filterNot(option => option.contains("wartremover") || option.contains("import")),
    Test / console / wartremoverErrors      := List.empty,
    Test / console / wartremoverWarnings    := List.empty,
    Test / console / scalacOptions          :=
      (console / scalacOptions)
        .value
        .filterNot(option => option.contains("wartremover") || option.contains("import")),
    /* } WartRemover and scalacOptions */
    testFrameworks ~= (testFws => (TestFramework("hedgehog.sbt.Framework") +: testFws).distinct),
    licenses                                := List("MIT" -> url("http://opensource.org/licenses/MIT")),
    console / initialCommands               := """import just.semver.SemVer""",
  )
  .settings(mavenCentralPublishSettings)

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
    // final val ProjectScalaVersion: String     = "2.13.3"
    final val CrossScalaVersions: Seq[String] =
      Seq(
        "2.11.12",
        "2.12.13",
        "2.13.3",
        ProjectScalaVersion
      ).distinct

    val SonatypeCredentialHost = "s01.oss.sonatype.org"
    val SonatypeRepository     = s"https://$SonatypeCredentialHost/service/local"

    final val hedgehogVersion = "0.8.0"

  }

lazy val libs =
  new {

    def hedgehogLibs(hedgehogVersion: String): Seq[ModuleID] = Seq(
      "qa.hedgehog" %% "hedgehog-core"   % hedgehogVersion % Test,
      "qa.hedgehog" %% "hedgehog-runner" % hedgehogVersion % Test,
      "qa.hedgehog" %% "hedgehog-sbt"    % hedgehogVersion % Test
    )

  }

def isScala3(scalaVersion: String): Boolean = scalaVersion.startsWith("3.")

lazy val mavenCentralPublishSettings: SettingsDefinition = List(
  /* Publish to Maven Central { */
  sonatypeCredentialHost := props.SonatypeCredentialHost,
  sonatypeRepository     := props.SonatypeRepository,
  /* } Publish to Maven Central */
)
