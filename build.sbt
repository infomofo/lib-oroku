name := """lib-oroku"""

version := "1.0"

scalaVersion := "2.11.1"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "org.slf4j"                   %   "slf4j-api"       % "1.7.5",
  "org.slf4j"                   %   "slf4j-simple"    % "1.7.5",
  "com.typesafe.scala-logging"  %%  "scala-logging" % "3.1.0",
  "org.jsoup"                   %   "jsoup"         % "1.8.1",
  "joda-time"                   %   "joda-time"     % "2.5",
  "org.scalatest"               %%  "scalatest"     % "2.1.6" % "test"
)

