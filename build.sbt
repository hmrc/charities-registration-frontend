import uk.gov.hmrc.DefaultBuildSettings

lazy val appName: String = "charities-registration-frontend"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.5.2"

val commonSettings: Seq[String] = Seq(
  "-unchecked",
  "-feature",
  "-deprecation",
  "-language:noAutoTupling",
  "-Wvalue-discard",
  "-Werror",
  "-Wconf:src=routes/.*:s",
  "-Wconf:src=views/.*:s",
  "-Wunused:unsafe-warn-patvars",
  "-Wconf:msg=Flag.*repeatedly:s"
)

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(inConfig(Test)(testSettings) *)
  .settings(libraryDependencies ++= AppDependencies())
  .settings(CodeCoverageSettings())
  .settings(PlayKeys.playDefaultPort := 9457)
  .settings(
    routesImport ++= Seq("models._", "models.OptionBinder._"),
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "views.ViewUtils._",
      "models.Index",
      "models.Mode",
      "models.OptionBinder._",
      "controllers.routes._"
    ),
    scalacOptions ++= commonSettings,
    Concat.groups := Seq(
      "javascripts/application.js" ->
        group(
          Seq(
            "javascripts/app.js",
            "javascripts/jquery-3.7.1.slim.min.js"
          )
        )
    ),
    pipelineStages := Seq(digest),
    Assets / pipelineStages := Seq(concat)
  )

lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork := true,
  unmanagedSourceDirectories += baseDirectory.value / "test-utils"
)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings(), scalacOptions ++= commonSettings)

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt it/Test/scalafmt")
