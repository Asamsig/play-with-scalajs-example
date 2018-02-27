// Comment to get more information during initialization
logLevel := Level.Warn

// Sbt plugins
addSbtPlugin("com.typesafe.play" % "sbt-plugin"              % "2.6.11")
addSbtPlugin("org.scala-js"      % "sbt-scalajs"             % "0.6.22")
addSbtPlugin("com.vmunier"       % "sbt-web-scalajs"         % "1.0.6")
addSbtPlugin("ch.epfl.scala"     % "sbt-web-scalajs-bundler" % "0.9.0")
addSbtPlugin("com.typesafe.sbt"  % "sbt-gzip"                % "1.0.2")
addSbtPlugin("com.typesafe.sbt"  % "sbt-digest"              % "1.1.3")
addSbtPlugin("org.irundaia.sbt"  % "sbt-sassify"             % "1.4.11")
