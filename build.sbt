val scalaVer = "2.12.7"

val runtimeVersion = "3.3.2-SNAPSHOT"

val sireumScalacVersion = "3.3.3"

ThisBuild / scalaVersion := scalaVer

ThisBuild / scalacOptions := Seq("-target:jvm-1.8", "-deprecation", "-Yrangepos",
  "-Ydelambdafy:method", "-feature", "-unchecked", "-Xfatal-warnings")

ThisBuild / javacOptions := Seq("-source", "1.8", "-target", "1.8", "-encoding", "utf8")

val commonSettings = Seq(
  organization := "org.sireum",
  incOptions := incOptions.value.withLogRecompileOnMacro(false),
  retrieveManaged := true,
  version := runtimeVersion,
  Test / parallelExecution := true,
  resolvers += Resolver.sonatypeRepo("public"),
  publishMavenStyle := true,
  publishTo := Some(
    if (isSnapshot.value)
      Opts.resolver.sonatypeSnapshots
    else
      Opts.resolver.sonatypeStaging
  ),
  Test / publishArtifact := false,
  pomIncludeRepository := { _ => false },
  pomExtra :=
    <url>https://github.com/sireum/v3-runtime/</url>
      <licenses>
        <license>
          <name>Simplified BSD License</name>
          <url>https://github.com/sireum/v3-runtime/blob/master/license.md</url>
        </license>
      </licenses>
      <scm>
        <url>https://github.com/sireum/v3-runtime.git</url>
        <connection>scm:git:https://github.com/sireum/v3-runtime.git</connection>
      </scm>
      <developers>
        <developer>
          <id>robby-phd</id>
          <name>Robby</name>
          <url>http://cs.ksu.edu/~robby</url>
        </developer>
      </developers>
)

lazy val sireumRuntime = {
  val `sireum-runtime` = project.in(file(".")).
    aggregate(macrosJvm, macrosJs, libraryJvm, libraryJs).
    settings(
      publishArtifact := false
    )
  `sireum-runtime`
}

lazy val macros = crossProject.in(file("macros")).settings(commonSettings: _*).settings(
  name := "macros",
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect" % scalaVer
  )
)

lazy val macrosJvm = macros.jvm
lazy val macrosJs = macros.js

lazy val library = crossProject.in(file("library")).settings(commonSettings: _*).settings(
  name := "library",
  libraryDependencies ++= Seq(
    "org.scalatest" %%% "scalatest" % "3.0.4" % "test",
    "org.spire-math" %%% "spire" % "0.13.0" % "test"
  ),
  addCompilerPlugin("org.sireum" %% "scalac-plugin" % sireumScalacVersion)
).dependsOn(macros)

lazy val libraryJvm = library.jvm
lazy val libraryJs = library.js
