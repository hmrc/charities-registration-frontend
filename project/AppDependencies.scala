import sbt.*

object AppDependencies {

  private lazy val mongoHmrcVersion     = "1.6.0"
  private lazy val bootstrapPlayVersion = "7.23.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"            % mongoHmrcVersion,
    "uk.gov.hmrc"       %% "play-conditional-form-mapping" % "1.13.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"    % bootstrapPlayVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"            % "7.29.0-play-28"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"   %% "hmrc-mongo-test-play-28"   % mongoHmrcVersion,
    "uk.gov.hmrc"         %% "bootstrap-test-play-28"    % bootstrapPlayVersion,
    "org.scalatest"       %% "scalatest"                 % "3.2.17",
    "org.jsoup"            % "jsoup"                     % "1.17.1",
    "org.mockito"         %% "mockito-scala-scalatest"   % "1.17.30",
    "org.scalatestplus"   %% "scalacheck-1-17"           % "3.2.17.0",
    "com.vladsch.flexmark" % "flexmark-all"              % "0.64.8",
    "com.stephenn"        %% "scalatest-json-jsonassert" % "0.2.5"
  ).map(_ % Test)

  val itDependencies: Seq[ModuleID]    = Seq.empty

  def apply(): Seq[ModuleID] = compile ++ test
}
