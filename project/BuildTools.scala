import sbt.Defaults.sbtPluginExtra
import sbt.Keys._
import sbt.{CrossVersion, _}

/**
  * @author Kevin Lee
  * @since 2018-05-21
  */
object BuildTools {

  type VersionSpecificFunction[T] = PartialFunction[Option[(Long, Long)], T]

  def envVar: String => Option[String] = sys.env.get

  def crossVersionProps[T](commonProps: Seq[T],
                           scalaVersion: String)(
                           versionSpecific: VersionSpecificFunction[Seq[T]]): Seq[T] =
    commonProps ++ versionSpecific(CrossVersion.partialVersion(scalaVersion))

  def crossVersionSbtPlugin(organization: String,
                            name: String)(
                            versionSpecific: VersionSpecificFunction[String]): Setting[Seq[ModuleID]] =
    libraryDependencies +=
      sbtPluginExtra(
        m = organization %% name % versionSpecific(CrossVersion.partialVersion(scalaVersion.value)),
        sbtV = (sbtBinaryVersion in pluginCrossBuild).value,
        scalaV = (scalaBinaryVersion in update).value
      )
}
