import sbt.Setting
import scoverage.ScoverageKeys.*

object CodeCoverageSettings {

  private val excludedPackages: Seq[String] = Seq("<empty>;.*components.*;.*Routes.*")

  val settings: Seq[Setting[_]] = Seq(
    coverageExcludedPackages := excludedPackages.mkString(";"),
    coverageMinimumStmtTotal := 99,
    coverageFailOnMinimum := true,
    coverageHighlighting := true
  )
}
