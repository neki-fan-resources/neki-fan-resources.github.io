import Common._

lazy val `fan-resources` = project
  .in(file("."))
  .settings(buildSettings)
  .settings(
    name := "fan-resources",
    version := "0.1.0-SNAPSHOT",

    // 2.13 compatibility
    libraryDependencies ++= Seq(
      "org.virtuslab" %% "scala-yaml" % "0.3.0"
    ),
  )
