val scala3Version = "3.6.4"

lazy val root = project
  .in(file("."))
  .settings(
    name := "neki-fan-resources",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    scalacOptions ++= Seq("-deprecation"),

    // 2.13 compatibility
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.4.3",
      "org.virtuslab" %% "scala-yaml" % "0.3.0"
    ),

    Compile / unmanagedResourceDirectories ++= Seq(
      baseDirectory.value / "data",
      baseDirectory.value / "static",
    ),
  )

