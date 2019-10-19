import sbt._

object Dependencies {
  lazy val hedgehogVersion = "6dba7c9ba065e423000e9aa2b6981ce3d70b74cb"
  lazy val hedgehogRepo =
    "bintray-scala-hedgehog" at "https://dl.bintray.com/hedgehogqa/scala-hedgehog"

  lazy val hedgehogLibs: Seq[ModuleID] = Seq(
        "hedgehog" %% "hedgehog-core" % hedgehogVersion
      , "hedgehog" %% "hedgehog-runner" % hedgehogVersion
      , "hedgehog" %% "hedgehog-sbt" % hedgehogVersion
    ).map(_ % Test)

  lazy val justFp: ModuleID = "io.kevinlee" %% "just-fp" % "1.3.4"

}
