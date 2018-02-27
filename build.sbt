val scalaV = "2.12.4"

val scalaJSReactVersion = "1.1.1"
val scalaCssVersion     = "0.5.3"
val reactJSVersion      = "15.6.1"

lazy val server = (project in file("server"))
  .enablePlugins(PlayScala, WebScalaJSBundlerPlugin)
  .settings(
    scalaVersion := scalaV,
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    resolvers ++= Seq(
      "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
      Resolver.jcenterRepo
    ),
    libraryDependencies ++= Seq(
      "org.julienrf"      %% "play-jsmessages" % "3.0.0",
      specs2              % Test,
      guice,
      ws
    )
  )
  .dependsOn(sharedJvm)

lazy val client = (project in file("client"))
  .enablePlugins(ScalaJSBundlerPlugin, ScalaJSWeb)
  .settings(
    scalaVersion := scalaV,
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "org.scala-js"                      %%% "scalajs-dom"              % "0.9.3",
      "com.github.japgolly.scalajs-react" %%% "core"                     % scalaJSReactVersion withJavadoc () withSources (),
      "com.github.japgolly.scalajs-react" %%% "extra"                    % scalaJSReactVersion withJavadoc () withSources (),
      "com.github.japgolly.scalacss"      %%% "core"                     % scalaCssVersion withJavadoc () withSources (),
      "com.github.japgolly.scalacss"      %%% "ext-react"                % scalaCssVersion withJavadoc () withSources (),
      "com.olvind"                        %%% "scalajs-react-components" % "0.8.0"
    ),
    npmDependencies in Compile ++= Seq(
      "react"       -> reactJSVersion,
      "react-dom"   -> reactJSVersion,
      "material-ui" -> "0.20.0"
    )
  ).dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      "com.typesafe.play" %%% "play-json" % "2.6.7"
    )
  ).jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs  = shared.js

val play =
  project.in(file("."))
    .aggregate(client, server)

// loads the server project at sbt startup
onLoad in Global := (onLoad in Global).value andThen { s: State =>
  "project server" :: s
}
