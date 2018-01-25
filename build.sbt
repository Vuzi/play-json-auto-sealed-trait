import Dependencies._

lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.4"
)

lazy val root = (project in file("."))
  .aggregate(macros, example)


lazy val macros = (project in file("macros"))
  .settings(
    inThisBuild(commonSettings),
    name := "Macros",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.12.4",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.7"
  )


lazy val example = (project in file("example"))
  .settings(
    inThisBuild(commonSettings),
    name := "Examples",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.7"
  ).dependsOn(macros)
