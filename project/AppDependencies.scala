import sbt.*

object AppDependencies {

  private lazy val mongoHmrcVersion     = "2.11.0"
  private lazy val bootstrapPlayVersion = "10.4.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % mongoHmrcVersion,
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapPlayVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % "12.22.0"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % mongoHmrcVersion,
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapPlayVersion,
    "uk.gov.hmrc"       %% "domain-test-play-30"     % "13.0.0",
    "org.jsoup"          % "jsoup"                   % "1.21.2",
    "org.scalatestplus" %% "scalacheck-1-18"         % "3.2.19.0",
    "com.googlecode.libphonenumber" % "libphonenumber" % "8.13.37"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
