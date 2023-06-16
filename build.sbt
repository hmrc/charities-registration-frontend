import play.sbt.routes.RoutesKeys
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.addTestReportOption
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "charities-registration-frontend"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(DefaultBuildSettings.scalaSettings)
  .settings(DefaultBuildSettings.defaultSettings())
  .settings(inConfig(IntegrationTest)(DefaultBuildSettings.integrationTestSettings()))
  .configs(IntegrationTest)
  .settings(
    javaOptions += "-Dlogger.resource=logback-test.xml",
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory)(base => Seq(base / "it")).value,
    IntegrationTest / resourceDirectory := (IntegrationTest / baseDirectory)(base => base / "it" / "resources").value,
    IntegrationTest / parallelExecution := false,
    IntegrationTest / fork := true,
    addTestReportOption(IntegrationTest, "int-test-reports")
  )
  .settings(majorVersion := 0)
  // To resolve dependency clash between flexmark v0.64.4+ and play-language to run accessibility tests, remove when versions align
  .settings(dependencyOverrides += "com.ibm.icu" % "icu4j" % "69.1")
  .settings(
    scalaVersion := "2.13.10",
    name := appName,
    RoutesKeys.routesImport ++= Seq("models._", "models.OptionBinder._"),
    PlayKeys.playDefaultPort := 9457,
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
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;" +
      ".*models.Mode*;.*handlers.*;.*components.*;.*TimeMachine.*;" +
      ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;" +
      ".*ControllerConfiguration;.*testonly.*;",
    ScoverageKeys.coverageMinimumStmtTotal := 98,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    scalacOptions ++= Seq("-feature", "-Xlint:-unused", "-Wconf:src=routes/.*:s,src=views/.*:s"),
    libraryDependencies ++= AppDependencies(),
    // To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
    libraryDependencySchemes ++= Seq("org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always),
    retrieveManaged := true,
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

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt IntegrationTest/scalafmt")
addCommandAlias("scalastyleAll", "all scalastyle Test/scalastyle IntegrationTest/scalastyle")
