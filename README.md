# Play Framework with Scala.js and Scalajs-react

[![License](http://img.shields.io/:license-Apache%202-red.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)

This is a simple example application showing how you can integrate a Play project with a Scala.js project.

The application contains three directories:
* `server` Play application (server side)
* `client` Scala.js application (client side)
* `shared` Scala code that you want to share between the server and the client

## Run the application
```shell
$ sbt
> run
$ open http://localhost:9000
```

## Features

The application uses the [web-jars](https://github.com/webjars/webjars-play), [play-jsmessages](https://github.com/julienrf/play-jsmessages), [sbt-web-scalajs](https://github.com/vmunier/sbt-web-scalajs), [sbt-sassify](https://github.com/irundaia/sbt-sassify), [scalajs-react](https://github.com/japgolly/scalajs-react) and the [scalajs-scripts](https://github.com/vmunier/scalajs-scripts) library.

- Run your application like a regular Play app
  - `compile` triggers the Scala.js fastOptJS command, and SASS/SCSS compiling, for files located in assets.styles. (Note: Only using either SASS or SCSS works, not both at the same time.)
  - `run` triggers the Scala.js fastOptJS command on page refresh
  - `~compile`, `~run`, continuous compilation is also available
- Compilation errors from the Scala.js projects are also displayed in the browser
- Production archives (e.g. using `stage`, `dist`) contain the optimised javascript
- Source maps
  - Open your browser dev tool to set breakpoints or to see the guilty line of code when an exception is thrown
  - Source Maps is _disabled in production_ by default to prevent your users from seeing the source files. But it can easily be enabled in production too by setting `emitSourceMaps in fullOptJS := true` in the Scala.js projects.

## Cleaning

The root project aggregates all the other projects by default.
Use this root project, called `play-with-scalajs-example`, to clean all the projects at once.
```shell
$ sbt
> play-with-scalajs-example/clean
```

## IDE integration

### IntelliJ

In IntelliJ, open Project wizard, select `Import Project`, choose the root folder and click `OK`.
Select `Import project from external model` option, choose `SBT project` and click `Next`. Select additional import options and click `Finish`.
Make sure you use the IntelliJ Scala Plugin v1.3.3 or higher. There are known issues with prior versions of the plugin.

Make sure that your Play 2 run task module targets the root project and not the server module. Otherwise you will get a no main class found error.
