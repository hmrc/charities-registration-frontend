import uk.gov.hmrc.DefaultBuildSettings

import scala.collection.immutable.Seq

lazy val appName: String = "charities-registration-frontend"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.4.2"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
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
    scalacOptions ++= Seq(
      "-feature",
//      "-Wconf:src=routes/.*:s",
//      "-Wconf:src=views/.*:s",
      "-source:3.4-migration",
      "-rewrite"
    ),
    Test / scalacOptions ++= Seq(
      "-feature",
      "-source:3.4-migration",
      "-rewrite"
    ),
    Concat.groups := Seq(
      "javascripts/application.js" ->
        group(
          Seq(
            "javascripts/app.js",
            "javascripts/autocomplete.js",
            "javascripts/jquery-3.7.1.slim.min.js"
          )
        )
    ),
    pipelineStages := Seq(digest),
    Assets / pipelineStages := Seq(concat)
  )

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings())

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt it/Test/scalafmt")
