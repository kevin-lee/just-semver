import wartremover.WartRemover.autoImport.{Wart, Warts}

/**
  * @author Kevin Lee
  * @since 2018-05-21
  */
object ProjectInfo {

  val ProjectScalaVersion: String = "2.13.0"
  val CrossScalaVersions: Seq[String] = Seq("2.10.7", "2.11.12", "2.12.10", ProjectScalaVersion)

  val ProjectVersion: String = "0.1.0"

  val commonWarts: Seq[wartremover.Wart] = Warts.allBut(Wart.DefaultArguments, Wart.Overloading, Wart.Any, Wart.Nothing, Wart.NonUnitStatements)

}
