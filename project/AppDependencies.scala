import sbt.*

object AppDependencies {

  private lazy val mongoHmrcVersion     = "2.6.0"
  private lazy val bootstrapPlayVersion = "9.11.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % mongoHmrcVersion,
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapPlayVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % "12.0.0"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % mongoHmrcVersion,
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapPlayVersion,
    "org.jsoup"          % "jsoup"                   % "1.19.1",
    "org.scalatestplus" %% "scalacheck-1-18"         % "3.2.19.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
