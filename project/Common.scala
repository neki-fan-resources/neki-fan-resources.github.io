import sbt.Keys._
import sbt.*

object Common {

  lazy val buildSettings = Seq(
    organization := "org.skyluc",
    scalaVersion := "3.6.4",
    scalacOptions ++= Seq("-deprecation", "-Wunused:implicits,explicits,imports,locals,params,privates"),
  )

  object Versions {

    val Akka = "2.10.5"
    val AkkaHttp = "10.7.2"

  }

  object Dependencies {

    val AkkaHttp = Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % Versions.Akka,
      "com.typesafe.akka" %% "akka-stream" % Versions.Akka,
      "com.typesafe.akka" %% "akka-http" % Versions.AkkaHttp,
    )

    val TypesafeConfig = Seq(
      "com.typesafe" % "config" % "1.4.3"
    )
  }

}
