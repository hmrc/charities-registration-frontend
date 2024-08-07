import uk.gov.hmrc.DefaultBuildSettings

lazy val appName: String = "charities-registration-frontend"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.14"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(libraryDependencies ++= AppDependencies())
  .settings(CodeCoverageSettings.settings)
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
    scalacOptions ++= Seq("-feature", "-Wconf:src=routes/.*:s,src=views/.*:s"),
    Concat.groups := Seq(
      "javascripts/application.js" ->
        group(
          Seq(
            "javascripts/jquery.min.js",
            "javascripts/app.js",
            "javascripts/autocomplete.js"
          )
        )
    ),
    uglifyCompressOptions := Seq("unused=false", "dead_code=false"),
    pipelineStages := Seq(digest),
    Assets / pipelineStages := Seq(concat, uglify),
    uglify / includeFilter := GlobFilter("application.js")
  )

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings())

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt it/Test/scalafmt A11y/scalafmt")
