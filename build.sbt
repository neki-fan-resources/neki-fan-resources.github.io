import Common._

lazy val fanResources = Project(id = "fan-resources", base = file("fan-resources"))

lazy val `neki-site` = project
  .in(file("neki-site"))
  .settings(buildSettings)
  .settings(
    name := "neki-site",
    version := "0.1.0-SNAPSHOT",

    // 2.13 compatibility
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.4.3"
    ),
  )
  .dependsOn(fanResources)

lazy val root = (project in file("."))
  .aggregate(fanResources, `neki-site`)
  .settings(buildSettings)
  .settings(
    Compile / mainClass := Some("org.skyluc.neki_site.Main"),
    Compile / unmanagedResourceDirectories ++= Seq(
      baseDirectory.value / "data",
      baseDirectory.value / "static",
    ),
  )
  .dependsOn(`neki-site`)
