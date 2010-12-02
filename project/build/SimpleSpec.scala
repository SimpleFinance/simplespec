class SimpleSpec(info: sbt.ProjectInfo) extends sbt.DefaultProject(info) with IdeaProject with posterous.Publish with rsync.RsyncPublishing {
  /**
   * Publish the source as well as the class files.
   */
  override def packageSrcJar= defaultJarPath("-sources.jar")
  val sourceArtifact = sbt.Artifact(artifactID, "src", "jar", Some("sources"), Nil, None)
  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageSrc)

  /**
   * Publish via rsync.
   */
  def rsyncRepo = "codahale.com:/home/codahale/repo.codahale.com"
  
  /**
   * Dependencies
   */
  val scalaToolsReleases = "scala-tools.org Releases" at "http://scala-tools.org/repo-releases"
  val specs = "org.scala-tools.testing" %% "specs" % "1.6.6" withSources()
}