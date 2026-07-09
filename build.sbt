name := "saslprep"

ThisBuild / tlBaseVersion := "1.0"

ThisBuild / developers += tlGitHubDev("armanbilge", "Arman Bilge")
ThisBuild / startYear := Some(2021)

ThisBuild / crossScalaVersions := Seq("3.3.7", "2.12.21", "2.13.18")

ThisBuild / githubWorkflowBuildPreamble +=
  WorkflowStep.Run(
    List("sudo apt-get install libutf8proc-dev"),
    name = Some("Install libutf8proc"),
    cond = Some("matrix.project == 'rootNative'")
  )

lazy val root = tlCrossRootProject.aggregate(saslprep)

lazy val saslprep = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("saslprep"))
  .settings(
    libraryDependencies ++= Seq(
      "io.github.cquiroz" %%% "scala-java-locales" % "1.5.4" % Test,
      "org.scalameta" %%% "munit" % "1.3.4" % Test
    )
  )
