val scalaV = "2.12.4"

val scalaJSReactVersion = "1.1.1"
val scalaCssVersion = "0.5.3"
val reactJSVersion = "15.6.1"

lazy val server = (project in file("server"))
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
      "com.vmunier" %% "scalajs-scripts" % "1.1.1",
      "org.julienrf" %% "play-jsmessages" % "3.0.0",
      specs2 % Test,
      "org.webjars" %% "webjars-play" % "2.6.1",
      "org.webjars.bower" % "compass-mixins" % "1.0.2",
      guice,
      ws,
      "org.webjars" % "foundation" % "6.2.3"
    )
  )
  .enablePlugins(PlayScala)
  .dependsOn(sharedJvm)

lazy val client = (project in file("client"))
  .settings(
    scalaVersion := scalaV,
    scalaJSUseMainModuleInitializer := true,
    scalaJSUseMainModuleInitializer in Test := false,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.3",
      "com.github.japgolly.scalajs-react" %%% "core" % scalaJSReactVersion withJavadoc () withSources (),
      "com.github.japgolly.scalajs-react" %%% "extra" % scalaJSReactVersion withJavadoc () withSources (),
      "com.github.japgolly.scalacss" %%% "core" % scalaCssVersion withJavadoc () withSources (),
      "com.github.japgolly.scalacss" %%% "ext-react" % scalaCssVersion withJavadoc () withSources ()
    ),
    jsDependencies ++= Seq(
      "org.webjars.npm" % "react" % reactJSVersion / "react-with-addons.js" commonJSName "React" minified "react-with-addons.min.js",
      "org.webjars.npm" % "react-dom" % reactJSVersion / "react-dom.js" commonJSName "ReactDOM" minified "react-dom.min.js" dependsOn "react-with-addons.js"
    )
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      "com.typesafe.play" %%% "play-json" % "2.6.0"
    )
  )
  .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the server project at sbt startup
onLoad in Global := (onLoad in Global).value andThen {s: State => "project server" :: s}