import sbt.Keys.baseDirectory
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._
import com.typesafe.sbt.web.Import._
import play.sbt.PlayImport.PlayKeys._
import play.sbt.PlayRunHook
import play.twirl.sbt.Import.TwirlKeys
import sbt.Keys._
import sbt._

object Beanstalk extends Plugin {

  lazy val ebDist = TaskKey[Unit]  ("eb-dist", "build for AWS Elastic Beanstalk")

  val ebSettings: Seq[Setting[_]] = Seq(
    ebDist := {
      val targetFile = s"./target/universal/${name.value}-${version.value}-eb.zip"
      val procFile   = "conf/beanstalk/Procfile"
      val extensions = "conf/beanstalk/.ebextensions"
      // Make Package
      val result = (packageBin in Universal).value
      val parent  = result.getParent
      val archive = Path.apply(result).base
      // Decompression
      IO.unzip(result, new File(s"$parent"))
      // Copy Files
      IO.copyFile(new File(procFile), new File(s"$parent/$archive/Procfile"))
      IO.copyDirectory(new File(extensions), new File(s"$parent/$archive/.ebextensions"))
      // Re-Compression
      IO.zip(Path.allSubpaths(new File(s"$parent/$archive")), new File(targetFile))
    },
    ebDist <<= ebDist.dependsOn(dist)

  )
}