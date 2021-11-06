name := "mandelbrotMap"

version := {
  val Tag = "refs/tags/(.*)".r
  sys.env.get("CI_VERSION").collect { case Tag(tag) => tag }
    .getOrElse("0.0.1-SNAPSHOT")
}

scalaVersion := "2.13.7"

idePackagePrefix := Some("online.sachara.mandelbrot")

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % "0.23.6",
  "org.http4s" %% "http4s-dsl" % "0.23.6",
  "ch.qos.logback" % "logback-classic" % "1.2.6"
)

assembly / assemblyJarName := s"${name.value}-${version.value}.sh.bat"

assembly / assemblyOption := (assembly / assemblyOption).value
  .withPrependShellScript(Some(AssemblyPlugin.defaultUniversalScript(shebang = false)))

assembly / assemblyMergeStrategy := {
  case PathList(paths@_*) if paths.last == "module-info.class" => MergeStrategy.discard
  case PathList("META-INF", "jpms.args") => MergeStrategy.discard
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}
