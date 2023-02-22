import play.sbt.routes.RoutesKeys
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.addTestReportOption
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "charities-registration-frontend"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(DefaultBuildSettings.scalaSettings: _*)
  .settings(DefaultBuildSettings.defaultSettings(): _*)
  .settings(inConfig(Test)(testSettings): _*)
  .configs(IntegrationTest)
  .settings(
    inConfig(IntegrationTest)(
      Defaults.itSettings ++
        AutomateHeaderPlugin.autoImport.automateHeaderSettings(IntegrationTest)
    ): _*
  )
  .settings(
    javaOptions += "-Dlogger.resource=logback-test.xml",
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory) (base => Seq(base / "it")).value,
    IntegrationTest / resourceDirectory := (IntegrationTest / baseDirectory) (base => base / "it" / "resources").value,
    IntegrationTest / parallelExecution := false,
    IntegrationTest / Keys.fork := true,
    addTestReportOption(IntegrationTest, "int-test-reports")
  )
  .settings(majorVersion := 0)
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
    ScoverageKeys.coverageMinimumStmtTotal := 80,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    scalacOptions ++= Seq(
      "-feature",
      "-Xlint:-unused",
      "-Wconf:cat=unused-imports&site=.*views.html.*:s", // Silence import warnings in Play html files
      "-Wconf:cat=unused-imports&site=<empty>:s", // Silence import warnings on Play `routes` files
      "-Wconf:cat=unused-imports&site=router:s", // Silence import warnings on Play `routes` files
      "-Wconf:cat=unused-imports&site=v1:s", // Silence import warnings on Play `v1.routes` files
      "-Wconf:cat=unused-imports&site=v2:s", // Silence import warnings on Play `v2.routes` files
      "-Wconf:cat=unused-imports&site=views.v1:s", // Silence import warnings on Play `views.v1.routes` files
      "-Wconf:cat=deprecation&site=controllers\\.v1.*&origin=scala.util.Either.right:s", // Silence deprecations in generated Controller classes
      "-Wconf:cat=deprecation&site=.*v1.Routes.*&origin=scala.util.Either.right:s"
    ),
    libraryDependencies ++= AppDependencies(),
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

lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork := true,
  javaOptions ++= Seq(
    "-Dconfig.resource=test.application.conf",
    "-Dlogger.resource=logback-test.xml"
  )
)

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt IntegrationTest/scalafmt")
addCommandAlias("scalastyleAll", "all scalastyle Test/scalastyle IntegrationTest/scalastyle")
