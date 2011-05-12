import sbt._
import maven._

class SimpleSpecProject(info: ProjectInfo) extends DefaultProject(info)
                                            with IdeaProject
                                            with MavenDependencies {

  /**
   * Publish the source as well as the class files.
   */
  override def compileOptions = super.compileOptions ++ Seq(Unchecked)

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
  val specs2 = crossScalaVersionString match {
    case "2.8.1" => "org.specs2" % "specs2_2.8.1" % "1.2"
    case "2.9.0" => "org.specs2" % "specs2_2.9.0.RC2" % "1.2"
  }

  def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")
  override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)
}
