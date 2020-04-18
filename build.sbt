import com.typesafe.config.ConfigFactory
import com.typesafe.sbt.packager.docker.Cmd

val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()

lazy val commonSettings = Seq(
  name := conf.getString("app.name"),
  organization := conf.getString("app.org"),
  version := conf.getString("app.version"),
  scalaVersion := "2.12.8",
  autoAPIMappings := true,
  swaggerDomainNameSpaces := Seq("models")
)

lazy val dockerSettings = Seq(
  packageName in Docker := conf.getString("docker.package.name"),
  dockerCommands := Seq(
    Cmd("FROM", "openjdk:8-jre-alpine"),
    Cmd("LABEL", s"""maintainer="${conf.getString("docker.maintainer")}""""),
    Cmd("RUN", "apk --no-cache add bash"),
    Cmd("WORKDIR", "/opt/docker"),
    Cmd("ADD", "--chown=daemon:daemon opt /opt"),
    Cmd("USER", "daemon"),
    Cmd("ENTRYPOINT", s"""["/opt/docker/bin/${conf.getString("app.name")}"]"""),
    Cmd("CMD", """[]""")
  )
)

// Dependencies
val config    = "com.typesafe" % "config" % "1.3.2"
val swaggerUi = "org.webjars" % "swagger-ui" % "2.2.0"
val scalaTest = "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2"

lazy val root = (project in file("."))
  .enablePlugins(
    PlayScala,
    DockerPlugin,
    SwaggerPlugin)
  .settings(
    commonSettings,
    dockerSettings,
    swaggerDomainNameSpaces := Seq("models"),
    libraryDependencies ++= Seq(
      guice,
      config,
      swaggerUi,
      scalaTest % Test
    )
  )
