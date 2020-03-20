import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, integrationTestSettings, scalaSettings}
import uk.gov.hmrc.SbtArtifactory
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import scoverage.ScoverageKeys

val appName = "charities-registration-frontend"
scalaVersion := "2.12.10"

lazy val scoverageSettings: Seq[Def.Setting[_]] =
  Seq(
    ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;.*AuthService.*;models/.data/..*;audit.*;connectors.Links;view.*;models.*;app.Routes.*;charities.Routes.*;controllers.audit.*;uk.gov.hmrc.*;controllers.passcode.*;",
    ScoverageKeys.coverageMinimum := 80,
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
    //TODO when the bootstrap-play-25 or play26 story is played this can be removed as those repos will have the most up to date versions
    retrieveManaged := true,
    scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation", "-explaintypes", "-language:reflectiveCalls", "-language:postfixOps"),
    routesGenerator := InjectedRoutesGenerator,
    resolvers ++= Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.jcenterRepo
    )
  )
