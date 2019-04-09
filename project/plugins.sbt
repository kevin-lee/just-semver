logLevel := sbt.Level.Warn

addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.1")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.4.1")

resolvers += Resolver.url("Kevin's sbt Plugins", url("https://dl.bintray.com/kevinlee/sbt-plugins"))(Resolver.ivyStylePatterns)

addSbtPlugin("kevinlee" % "sbt-devoops" % "0.2.0")
