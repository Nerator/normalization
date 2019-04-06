lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization       := "ru.dovzhikov",
      scalaVersion       := "2.12.8",
      crossScalaVersions := Seq("2.11.12", "2.12.8"),
      version            := "0.1-SNAPSHOT",
      scalacOptions      ++= Seq("-unchecked", "-deprecation", "-feature")
    )),
    name := "normalization",
    resolvers += Resolver.sonatypeRepo("releases"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
    libraryDependencies ++= Seq(
      "org.scalanlp"       %% "breeze"              % "1.0-RC2",
      "org.scalafx"        %% "scalafx"             % "8.0.102-R11",
      "org.scalafx"        %% "scalafxml-core-sfx8" % "0.4",
      "com.github.junrar"   % "junrar"              % "2.0.0",
      "org.apache.poi"      % "poi"                 % "4.0.1",
      "org.apache.poi"      % "poi-ooxml"           % "4.0.1",
      "org.apache.poi"      % "poi-ooxml-schemas"   % "4.0.1",
      "com.typesafe.slick" %% "slick"               % "3.2.3",
      //"com.typesafe.slick" %% "slick-codegen"       % ver,
      "org.slf4j"           % "slf4j-nop"           % "1.6.4",
      "com.typesafe.slick" %% "slick-hikaricp"      % "3.2.3",
      "org.xerial"          % "sqlite-jdbc"         % "3.23.1",
      "org.scalatest"      %% "scalatest"           % "3.0.5" % Test
    ),
    
    // set the main class for the main 'run' task
    // change Compile to Test to set it for 'test:run'
    mainClass in (Compile, run) := Some("ru.dovzhikov.normalization.TestFXML"),
    
    // Fork a new JVM for 'run' and 'test:run' to avoid JavaFX double initialization problems
    fork := true
  )

Compile / scalacOptions ++= {
  scalaBinaryVersion.value match {
    case "2.11" => Seq("-Xexperimental")
    case _      => Nil
  }
}
