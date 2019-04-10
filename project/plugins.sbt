logLevel := sbt.Level.Warn

addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.4")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.4.1")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.2.7")

resolvers += Resolver.url("Kevin's sbt Plugins", url("https://dl.bintray.com/kevinlee/sbt-plugins"))(Resolver.ivyStylePatterns)

addSbtPlugin("kevinlee" % "sbt-devoops" % "0.2.0")
