import sbt.*

object AppDependencies {

  private lazy val mongoHmrcVersion     = "2.0.0"
  private lazy val bootstrapPlayVersion = "8.6.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % mongoHmrcVersion,
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapPlayVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % "10.1.0"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % mongoHmrcVersion,
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapPlayVersion,
    "org.jsoup"          % "jsoup"                   % "1.17.2",
    "org.mockito"       %% "mockito-scala-scalatest" % "1.17.31",
    "org.scalatestplus" %% "scalacheck-1-17"         % "3.2.18.0"
  ).map(_ % Test)

  val itDependencies: Seq[ModuleID]    = Seq.empty

  def apply(): Seq[ModuleID] = compile ++ test
}
