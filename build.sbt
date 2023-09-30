import just.semver.SemVer
import SemVer.{Major, Minor}
import kevinlee.sbt.SbtCommon.crossVersionProps

ThisBuild / scalaVersion := props.ProjectScalaVersion
ThisBuild / organization := "io.kevinlee"
ThisBuild / crossScalaVersions := props.CrossScalaVersions

ThisBuild / developers := List(
  Developer(
    "Kevin-Lee",
    "Kevin Lee",
    "kevin.code@kevinlee.io",
    url("https://github.com/Kevin-Lee")
  )
)
ThisBuild / homepage := url("https://github.com/Kevin-Lee/just-semver").some
ThisBuild / scmInfo :=
  ScmInfo(
    url("https://github.com/Kevin-Lee/just-semver"),
    "git@github.com:Kevin-Lee/just-semver.git"
  ).some
ThisBuild / licenses := props.licenses

ThisBuild / resolvers += "sonatype-snapshots" at s"https://${props.SonatypeCredentialHost}/content/repositories/snapshots"
ThisBuild / publishTo := updateSnapshotPublishTo((ThisBuild / publishTo).value)

lazy val justSemVer = (project in file("."))
  .enablePlugins(DevOopsGitHubReleasePlugin)
  .settings(
    name := props.RepoName,
    description := "Semantic Versioning (SemVer) for Scala",
  )
  .settings(mavenCentralPublishSettings)
  .dependsOn(
    coreJvm,
    coreJs,
  )
  .aggregate(
    coreJvm,
    coreJs,
  )

import sbtcrossproject.CrossProject

lazy val core = module("core", crossProject(JVMPlatform, JSPlatform))
  .settings(
//    (Compile / compile) / scalacOptions ++= (if (isGhaPublishing) List.empty[String]
//                                           else ProjectInfo.commonWarts(scalaVersion.value)),
//    (Test / compile) / scalacOptions ++= (if (isGhaPublishing) List.empty[String]
//                                        else ProjectInfo.commonWarts(scalaVersion.value)),
//      (Compile / console / scalacOptions)
//        .value
//        .filterNot(option => option.contains("wartremover") || option.contains("import")),
//    Test / console / scalacOptions    :=
//      (Test / console / scalacOptions)
//        .value
//        .filterNot(option => option.contains("wartremover") || option.contains("import")),
// -----------

    /* WartRemover and scalacOptions { */
//      Compile / compile / wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),
//      Test / compile / wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),

    //      wartremoverErrors ++= Warts.all,
    /////////// wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),
    //    Compile / console / wartremoverErrors   := List.empty,
//    Compile / console / wartremoverWarnings := List.empty,
//    Compile / console / scalacOptions       :=
//      (console / scalacOptions)
//        .value
//        .filterNot(option => option.contains("wartremover") || option.contains("import")),
//    Test / console / wartremoverErrors      := List.empty,
//    Test / console / wartremoverWarnings    := List.empty,
//    Test / console / scalacOptions          :=
//      (console / scalacOptions)
//        .value
//        .filterNot(option => option.contains("wartremover") || option.contains("import")),
//    /* } WartRemover and scalacOptions */
    console / initialCommands := """import just.semver.SemVer""",
  )

lazy val coreJvm = core.jvm
lazy val coreJs  = core.js.settings(Test / fork := false)

lazy val docs = (project in file("docs-gen-tmp/docs"))
  .enablePlugins(MdocPlugin, DocusaurPlugin)
  .settings(
    scalaVersion := "2.13.8",
    name := prefixedProjectName("docs"),
    mdocIn := file("docs"),
    mdocOut := file("generated-docs/docs"),
    cleanFiles += file("generated-docs/docs"),
    libraryDependencies ++= {
      import sys.process._
      "git fetch --tags".!
      val tag           = "git rev-list --tags --max-count=1".!!.trim
      val latestVersion = s"git describe --tags $tag".!!.trim.stripPrefix("v")

      List(
        "io.kevinlee" %% "just-semver" % latestVersion,
      )
    },
    mdocVariables := Map(
      "VERSION"                  -> {
        import sys.process._
        "git fetch --tags".!
        val tag = "git rev-list --tags --max-count=1".!!.trim
        s"git describe --tags $tag".!!.trim.stripPrefix("v")
      },
      "SUPPORTED_SCALA_VERSIONS" -> {
        val versions = props
          .CrossScalaVersions
          .map(CrossVersion.binaryScalaVersion)
          .distinct
          .map(binVer => s"`$binVer`")
        if (versions.length > 1)
          s"${versions.init.mkString(", ")} and ${versions.last}"
        else
          versions.mkString
      },
    ),
    docusaurDir := (ThisBuild / baseDirectory).value / "website",
    docusaurBuildDir := docusaurDir.value / "build",
  )
  .settings(noPublish)

lazy val props =
  new {
    val RepoName = "just-semver"

    val licenses = List("MIT" -> url("http://opensource.org/licenses/MIT"))

    val removeDottyIncompatible: ModuleID => Boolean =
      m =>
//        m.name == "wartremover" ||
        m.name == "ammonite" ||
          m.name == "kind-projector" ||
          m.name == "better-monadic-for" ||
          m.name == "mdoc"

    val isWartRemover: ModuleID => Boolean =
      m => m.name == "wartremover"

//    final val ProjectScalaVersion: String      = "3.1.3"
    final val ProjectScalaVersion: String     = "2.13.11"
    final val CrossScalaVersions: List[String] =
      (
        if (isGhaPublishing)
          (_: List[String]).diff(List(ProjectScalaVersion))
        else
          identity[List[String]] _
      ) (
        List(
          "2.12.17",
          "2.13.11",
          "3.1.3",
          ProjectScalaVersion
        ).distinct
      )

    val SonatypeCredentialHost = "s01.oss.sonatype.org"
    val SonatypeRepository     = s"https://$SonatypeCredentialHost/service/local"

    final val HedgehogVersion = "0.9.0"

    final val HedgehogLatestVersion = "0.10.1"

  }

lazy val libs =
  new {

    def hedgehogLibs(scalaVersion: String): List[ModuleID] = {
      val hedgehogVersion =
        if (scalaVersion.startsWith("3.0"))
          props.HedgehogVersion
        else
          props.HedgehogLatestVersion

      List(
        "qa.hedgehog" %% "hedgehog-core"   % hedgehogVersion % Test,
        "qa.hedgehog" %% "hedgehog-runner" % hedgehogVersion % Test,
        "qa.hedgehog" %% "hedgehog-sbt"    % hedgehogVersion % Test
      )
    }
  }

def isGhaPublishing: Boolean = sys.env.get("GHA_IS_PUBLISHING").fold(false)(_.toBoolean)

def isScala3(scalaVersion: String): Boolean = scalaVersion.startsWith("3.")

lazy val mavenCentralPublishSettings: SettingsDefinition = List(
  /* Publish to Maven Central { */
  sonatypeCredentialHost := props.SonatypeCredentialHost,
  sonatypeRepository := props.SonatypeRepository,
  /* } Publish to Maven Central */
)

// scalafmt: off
def prefixedProjectName(name: String) = s"${props.RepoName}${if (name.isEmpty) "" else s"-$name"}"
// scalafmt: on

def module(projectName: String, crossProject: CrossProject.Builder): CrossProject = {
  val prefixedName = prefixedProjectName(projectName)
  crossProject
    .in(file(s"modules/$prefixedName"))
    .settings(
      name := prefixedName,
      testFrameworks ~= (testFws => (TestFramework("hedgehog.sbt.Framework") +: testFws).distinct),
    )
    .settings(
      libraryDependencies ++= {
        if (isGhaPublishing) {
          List.empty[ModuleID]
        } else {
          SemVer.majorMinorPatch(SemVer.parseUnsafe(scalaVersion.value)) match {
            case (Major(2), Minor(11), _) =>
              List(compilerPlugin("org.wartremover" %% "wartremover" % "2.4.19" cross CrossVersion.full))
            case (_, _, _) =>
              List(compilerPlugin("org.wartremover" %% "wartremover" % "3.1.4" cross CrossVersion.full))
          }
        }
      },
      Compile / unmanagedSourceDirectories := {
        val sharedSourceDir = baseDirectory.value.getParentFile / "shared/src/main"
        val moreSrcs        =
          if (scalaVersion.value.startsWith("2.13") || scalaVersion.value.startsWith("2.12"))
            Seq(sharedSourceDir / "scala-2.12_2.13")
          else
            Seq.empty[File]
        ((Compile / unmanagedSourceDirectories).value ++ moreSrcs).distinct
      },
      //    useAggressiveScalacOptions := true,
      libraryDependencies :=
        crossVersionProps(Seq.empty[ModuleID], SemVer.parseUnsafe(scalaVersion.value)) {
          case (SemVer.Major(3), SemVer.Minor(0), _) =>
            libs.hedgehogLibs(scalaVersion.value) ++ libraryDependencies.value ++
              libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover")

          case (Major(3), _, _) =>
            libs.hedgehogLibs(scalaVersion.value) ++ libraryDependencies.value

          case x =>
            libs.hedgehogLibs(scalaVersion.value) ++ libraryDependencies.value
        },
      libraryDependencies := (
        if (isScala3(scalaVersion.value)) {
          libraryDependencies
            .value
            .filterNot(props.removeDottyIncompatible)
        } else {
          libraryDependencies.value
        }
      ),
      scalacOptions ++= (if (isGhaPublishing) List.empty[String]
                         else ProjectInfo.commonWarts(scalaVersion.value)),
      publishTo := updateSnapshotPublishTo(publishTo.value),
    )
    .settings(mavenCentralPublishSettings)
}

def updateSnapshotPublishTo(resolver: Option[Resolver]): Option[Resolver] = resolver match {
  case Some(resolver) =>
    if (resolver.name == "sonatype-snapshots")
      ("sonatype-snapshots" at s"https://${props.SonatypeCredentialHost}/content/repositories/snapshots").some
    else
      resolver.some
  case None =>
    none
}
