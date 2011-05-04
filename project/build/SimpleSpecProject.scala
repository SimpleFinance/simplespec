import sbt._
import maven._

class SimpleSpecProject(info: ProjectInfo) extends DefaultProject(info)
                                            with IdeaProject
                                            with MavenDependencies {
  /**
   * Publish the source as well as the class files.
   */
  override def packageSrcJar= defaultJarPath("-sources.jar")
  val sourceArtifact = sbt.Artifact(artifactID, "src", "jar", Some("sources"), Nil, None)
  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageSrc)

  /**
   * Publish to my repo.
   */
  lazy val publishTo = Resolver.sftp("Personal Repo",
                                     "codahale.com",
                                     "/home/codahale/repo.codahale.com/")
  
  /**
   * Dependencies
   */
  val scalaToolsReleases = "scala-tools.org Releases" at "http://scala-tools.org/repo-releases"
  val specs = "org.scala-tools.testing" %% "specs" % "1.6.6"
}
