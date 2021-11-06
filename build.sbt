name := "mandelbrotMap"

version := "0.1"

scalaVersion := "2.13.7"

idePackagePrefix := Some("online.sachara.mandelbrot")

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % "0.23.6",
  "org.http4s" %% "http4s-dsl" % "0.23.6",
  "ch.qos.logback" % "logback-classic" % "1.2.6"
)
