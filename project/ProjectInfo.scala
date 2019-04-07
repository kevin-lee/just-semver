import wartremover.WartRemover.autoImport.{Wart, Warts}

/**
  * @author Kevin Lee
  * @since 2018-05-21
  */
object ProjectInfo {

  val ProjectScalaVersion = "2.12.7"
  val CrossScalaVersions = Seq("2.11.12", ProjectScalaVersion)

  val ProjectVersion = "0.1.0-SNAPSHOT"

  val commonScalacOptions = Seq(
      "-deprecation"
    , "-unchecked"
    , "-feature"
    , "-Ywarn-value-discard"
    , "-Yno-adapted-args"
    , "-Xlint"
    , "-Xfatal-warnings"
    , "-Ywarn-dead-code"
    , "-Ywarn-inaccessible"
    , "-Ywarn-nullary-unit"
    , "-Ywarn-nullary-override"
    , "-Ywarn-numeric-widen"
    , "-encoding", "UTF-8"
  )

  val commonWarts = Warts.allBut(Wart.DefaultArguments, Wart.Overloading, Wart.Any, Wart.Nothing, Wart.NonUnitStatements)

}
