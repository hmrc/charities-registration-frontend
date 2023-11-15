import sbt.Keys.scalacOptions
import uk.gov.hmrc.DefaultBuildSettings

lazy val appName: String = "charities-registration-frontend"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"

// To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(libraryDependencies ++= AppDependencies())
  .settings(CodeCoverageSettings.settings)
  .settings(scalacOptions += "-Wconf:src=routes/.*:s")
  .settings(PlayKeys.playDefaultPort := 9457)
  // To resolve dependency clash between flexmark v0.64.4+ and play-language to run accessibility tests, remove when versions align
  .settings(dependencyOverrides += "com.ibm.icu" % "icu4j" % "69.1")
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
            "lib/govuk-frontend/govuk/all.js",
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
  .settings(DefaultBuildSettings.itSettings)
  .settings(libraryDependencies ++= AppDependencies.itDependencies)

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt it/Test/scalafmt A11y/scalafmt")
addCommandAlias("scalastyleAll", "all scalastyle Test/scalastyle it/Test/scalastyle A11y/scalastyle")
