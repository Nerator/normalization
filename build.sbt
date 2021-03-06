// For JavaFX
val osName: SettingKey[String] = SettingKey[String]("osName")

osName := (System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
})

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    inThisBuild(List(
      organization       := "ru.dovzhikov",
      scalaVersion       := "2.12.8",
      crossScalaVersions := Seq("2.11.12", "2.12.8"),
      version            := "0.2-SNAPSHOT",
      scalacOptions ++= Seq(
        "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
        "-encoding", "utf-8",                // Specify character encoding used by source files.
        "-explaintypes",                     // Explain type errors in more detail.
        "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
        "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
        "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
        "-language:higherKinds",             // Allow higher-kinded types
        "-language:implicitConversions",     // Allow definition of implicit functions called views
        "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
        "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
        "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
        "-Xfuture",                          // Turn on future language features.
        "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
        "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
        "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
        "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
        "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
        "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
        "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
        "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
        "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
        "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
        "-Xlint:option-implicit",            // Option.apply used implicit view.
        "-Xlint:package-object-classes",     // Class or object defined in package object.
        "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
        "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
        "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
        "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
        "-Xlint:unsound-match",              // Pattern match may not be typesafe.
        "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
        "-Ypartial-unification",             // Enable partial unification in type constructor inference
        "-Ywarn-dead-code",                  // Warn when dead code is identified.
        "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
        "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
        "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
        "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
        "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
        "-Ywarn-numeric-widen",              // Warn when numerics are widened.
        "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
        "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
        "-Ywarn-unused:locals",              // Warn if a local definition is unused.
        "-Ywarn-unused:params",              // Warn if a value parameter is unused.
        "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
        "-Ywarn-unused:privates",            // Warn if a private member is unused.
        "-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
      )
    )),
    name := "normalization",
    resolvers += Resolver.sonatypeRepo("releases"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
    libraryDependencies ++= Seq(
      // JavaFX 11
      "org.openjfx"         % "javafx-base"         % "11.0.2" classifier osName.value,
      "org.openjfx"         % "javafx-controls"     % "11.0.2" classifier osName.value,
      "org.openjfx"         % "javafx-fxml"         % "11.0.2" classifier osName.value,
      "org.openjfx"         % "javafx-graphics"     % "11.0.2" classifier osName.value,
      "org.openjfx"         % "javafx-media"        % "11.0.2" classifier osName.value,
      "org.openjfx"         % "javafx-web"          % "11.0.2" classifier osName.value,
      //"org.scalanlp"       %% "breeze"              % "1.0-RC2",
      "org.scalafx"        %% "scalafx"             % "11-R16",
      "org.scalafx"        %% "scalafxml-core-sfx8" % "0.4",
      "com.github.junrar"   % "junrar"              % "4.0.0",
      "org.apache.poi"      % "poi"                 % "4.1.0",
      //"org.apache.poi"      % "poi-ooxml"           % "4.1.0",
      //"org.apache.poi"      % "poi-ooxml-schemas"   % "4.1.0",
      "com.typesafe.slick" %% "slick"               % "3.3.1",
      //"com.typesafe.slick" %% "slick-codegen"       % ver,
      "org.slf4j"           % "slf4j-nop"           % "1.7.9",
      "com.typesafe.slick" %% "slick-hikaricp"      % "3.3.1",
      "org.xerial"          % "sqlite-jdbc"         % "3.27.2.1",
      "org.scalatest"      %% "scalatest"           % "3.0.7"  % Test,
      "org.scalacheck"     %% "scalacheck"          % "1.14.0" % "test"
    ),

    // set the main class for the main 'run' task
    // change Compile to Test to set it for 'test:run'
    mainClass in (Compile, run) := Some("ru.dovzhikov.normalization.MainApp"),

    // Fork a new JVM for 'run' and 'test:run' to avoid JavaFX double initialization problems
    fork := true
  )

// Fix for sfxml error
Compile / scalacOptions -= "-Ywarn-value-discard"

// Fix for 2.11
Compile / scalacOptions --= {
  scalaBinaryVersion.value match {
    case "2.11" => Seq(
      "-Xlint:constant",
      "-Ywarn-extra-implicit",
      "-Ywarn-unused:implicits",
      "-Ywarn-unused:imports",
      "-Ywarn-unused:locals",
      "-Ywarn-unused:params",
      "-Ywarn-unused:patvars",
      "-Ywarn-unused:privates"
    )
    case _      => Nil
  }
}

// Fix for SAM syntax in 2.11
Compile / scalacOptions ++= {
  scalaBinaryVersion.value match {
    case "2.11" => Seq("-Xexperimental")
    case _      => Nil
  }
}

// add javafx-swing in 2.11
ThisBuild / libraryDependencies ++= {
  scalaBinaryVersion.value match {
    case "2.11" => Seq(
      "org.openjfx" % "javafx-swing" % "11.0.2" classifier osName.value,
      "org.openjfx" % "javafx-web"   % "11.0.2" classifier osName.value
      )
    case _      => Nil
  }
}

lazy val packageVer = taskKey[File]("package zip file")

packageVer := {
  val output = baseDirectory.value / "package" / s"normalization_${(scalaBinaryVersion in root).value}-${(version in root).value}.zip"
  val genfile = (packageBin in Universal).value
  IO.move(genfile, output)
  output
}
