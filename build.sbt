ThisBuild / version := "1.0-RELEASE"

ThisBuild / scalaVersion := "2.12.15"


logLevel := Level.Debug

resolvers += Resolver.mavenLocal
resolvers += Resolver.DefaultMavenRepository
resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers +=
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"


lazy val root = (project in file("."))
  .settings(
    name := "HolidayCheck"
  )


libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.2.1",
  "org.apache.spark" %% "spark-sql" % "3.2.1",
  "org.postgresql" % "postgresql" % "42.3.6"
)

mainClass := Some("br.gsj.challenge.ReportGenerator")

