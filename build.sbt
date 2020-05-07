import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, scalaSettings}
import uk.gov.hmrc.SbtArtifactory
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "charities-registration-frontend"
scalaVersion := "2.12.11"

lazy val scoverageSettings: Seq[Def.Setting[_]] =
  Seq(
    ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;.*AuthService.*;models/.data/..*;audit.*;connectors.Links;view.*;models.*;app.Routes.*;prod.Routes.*;testOnlyDoNotUseInAppConf.Routes.*;controllers.audit.*;uk.gov.hmrc.*;controllers.passcode.*;",
    ScoverageKeys.coverageMinimum := 70,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true
  )
lazy val microservice: Project = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory, SbtWeb)
  .settings(
    scoverageSettings,
    scalaSettings,
    publishingSettings,
    defaultSettings(),
    majorVersion := 0,
    PlayKeys.playDefaultPort := 9457,
    libraryDependencies ++= AppDependencies.all,
    retrieveManaged := true,
    scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation", "-explaintypes", "-language:reflectiveCalls", "-language:postfixOps"),
    routesGenerator := InjectedRoutesGenerator,
    resolvers ++= Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.jcenterRepo
    ),
    uglifyCompressOptions := Seq("unused=false", "dead_code=false"),
    pipelineStages := Seq(digest),
    pipelineStages in Assets := Seq(concat,uglify)
  )

