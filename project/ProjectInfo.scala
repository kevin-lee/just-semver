import wartremover.WartRemover.autoImport.{Wart, Warts}

/**
  * @author Kevin Lee
  * @since 2018-05-21
  */
object ProjectInfo {

  val ProjectScalaVersion: String = "2.12.8"
  val CrossScalaVersions: Seq[String] = Seq("2.10.7", "2.11.12", ProjectScalaVersion)

  val ProjectVersion: String = "0.1.0-SNAPSHOT"

  val commonScalacOptions: Seq[String] = Seq(
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
    , "-encoding", "UTF-8"
    , "-Ywarn-unused-import"
    , "-Ywarn-numeric-widen"
  )

  val commonWarts: Seq[wartremover.Wart] = Warts.allBut(Wart.DefaultArguments, Wart.Overloading, Wart.Any, Wart.Nothing, Wart.NonUnitStatements)

}
