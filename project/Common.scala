import sbt.Keys._
import sbt.{Project, file}

object Common {

  lazy val buildSettings = Seq(
    organization := "org.skyluc",
    scalaVersion := "3.6.4",
    scalacOptions ++= Seq("-deprecation", "-Wunused:implicits,explicits,imports,locals,params,privates"),
  )

}
