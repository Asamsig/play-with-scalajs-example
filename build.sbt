
val scalaV = "2.11.8"

val scalaJSReactVersion = "1.0.0"
val scalaCssVersion = "0.5.3-RC1"
val reactJSVersion = "15.5.4"

lazy val server = (project in file("server")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  libraryDependencies ++= Seq(
    "com.vmunier" %% "scalajs-scripts" % "1.0.0",
    "org.julienrf" %% "play-jsmessages" % "2.0.0",
    specs2 % Test,
    "org.webjars" %% "webjars-play" % "2.5.0",
    "org.webjars.bower" % "compass-mixins" % "1.0.2",
    "org.webjars" % "foundation" % "6.2.3"
  )).enablePlugins(PlayScala).dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(
  scalaVersion := scalaV,
  scalaJSUseMainModuleInitializer := true,
  scalaJSUseMainModuleInitializer in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.1",
    "com.github.japgolly.scalajs-react" %%% "core" % scalaJSReactVersion withJavadoc() withSources(),
    "com.github.japgolly.scalajs-react" %%% "extra" % scalaJSReactVersion withJavadoc() withSources(),
    "com.github.japgolly.scalacss" %%% "core" % scalaCssVersion withJavadoc() withSources(),
    "com.github.japgolly.scalacss" %%% "ext-react" % scalaCssVersion withJavadoc() withSources()
  ),
  jsDependencies ++= Seq(
    "org.webjars.npm" % "react" % reactJSVersion / "react-with-addons.js" commonJSName "React" minified "react-with-addons.min.js",
    "org.webjars.npm" % "react-dom" % reactJSVersion / "react-dom.js" commonJSName "ReactDOM" minified "react-dom.min.js" dependsOn "react-with-addons.js"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(
    scalaVersion := scalaV,
      libraryDependencies ++= Seq()
  ).
  jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the server project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value