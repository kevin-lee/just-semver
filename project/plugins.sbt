logLevel := sbt.Level.Warn

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.11.1")
addSbtPlugin("org.scoverage"  % "sbt-scoverage"  % "2.3.1")
addSbtPlugin("ch.epfl.scala"  % "sbt-scalafix"   % "0.12.0")

addSbtPlugin("org.scalameta" % "sbt-mdoc"     % "2.5.2")
addSbtPlugin("io.kevinlee"   % "sbt-docusaur" % "0.17.0")

addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "1.18.2")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.2")

addSbtPlugin("org.scala-native"   % "sbt-scala-native"              % "0.5.7")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.3.2")

addSbtPlugin("org.scalameta" % "sbt-native-image" % "0.3.4")

val sbtDevOopsVersion = "3.2.1"
addSbtPlugin("io.kevinlee" % "sbt-devoops-scala"     % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-sbt-extra" % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-github"    % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-starter"   % sbtDevOopsVersion)
