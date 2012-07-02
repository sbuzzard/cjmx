import AssemblyKeys._

name := "JMX Command Line Client"

version := "1.0"

scalaVersion := "2.9.2"

licenses += ("Three-clause BSD-style license", url("http://github.com/mpilquist/cjmx/blob/master/LICENSE"))

unmanagedResources in Compile <++= baseDirectory map { base => (base / "NOTICE") +: (base / "LICENSE") +: ((base / "licenses") * "LICENSE_*").get }

resolvers += "Typesafe" at "http://repo.typesafe.com/typesafe/repo"

// SBT 0.12.0-RC2 is only available in the Ivy Releases repository
resolvers += Resolver.url("Typesafe Ivy Releases", url("http://repo.typesafe.com/typesafe/repo"))(Resolver.ivyStylePatterns)

libraryDependencies ++=
  "org.scalaz" %% "scalaz-core" % "7.0-SNAPSHOT" ::
  "org.scala-sbt" % "completion" % "0.12.0-RC2" ::
  "org.scalatest" % "scalatest_2.9.0" % "1.8" % "test" ::
  Nil

unmanagedClasspath in Compile += Path(Path.fileProperty("java.home").asFile.getParent) / "lib" / "tools.jar"

assemblySettings

jarName in assembly := "cjmx.jar"
