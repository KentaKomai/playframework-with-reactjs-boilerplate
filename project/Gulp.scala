import sbt.Keys.baseDirectory
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._
import com.typesafe.sbt.web.Import._
import play.sbt.PlayImport.PlayKeys._
import play.sbt.PlayRunHook
import play.twirl.sbt.Import.TwirlKeys
import sbt.Keys._
import sbt._

object Gulp extends Plugin {

  // windowsか？
  lazy val isWindows = System.getProperty("os.name").startsWith("Windows")

  lazy val gulpDirectory = SettingKey[File]("gulp-directory", "gulp directory")
  lazy val gulpFile = SettingKey[String]("gulp-file", "gulpfile")
  lazy val gulpExcludes = SettingKey[Seq[String]]("gulp-excludes")

  /** activator からgulpのタスクを自由に叩けるように */
  lazy val gulp      = InputKey[Unit]("gulp", "Task to run gulp")

  /** activator から叩くgulp buildのキー */
  lazy val gulpBuild = TaskKey[Int]  ("gulp-dist", "Task to run gulp dist")
  /** activator から叩くgulp cleanのキー */
  lazy val gulpClean = TaskKey[Unit] ("gulp-clean", "Task to run gulp clean")
  /** activator から叩くgulp testのキー */
  lazy val gulpTest  = TaskKey[Unit] ("gulp-test", "Task to run gulp test")

  val gulpSettings: Seq[Setting[_]] = Seq(

    gulpDirectory <<= ( baseDirectory in Compile) { _ / "frontend" },
    gulpFile := "gulpfile.babel.js",

    gulp := {
      val base = (gulpDirectory in Compile).value
      val gulpFileName = (gulpFile in Compile).value
      runGulp(base, gulpFileName, Def.spaceDelimited("<arg>").parsed.toList).exitValue()
    },
    gulpBuild := {
      val base = (gulpDirectory in Compile).value
      val gulpFileName = (gulpFile in Compile).value
      val result = runGulp(base, gulpFileName, List("build")).exitValue()
      if(result == 0){
        result
      }else throw new Exception("gulp build failed")
    },
    gulpClean := {
      val base = (gulpDirectory in Compile).value
      val gulpFileName = (gulpFile in Compile).value
      val result = runGulp(base, gulpFileName, List("clean")).exitValue()

      if(result != 0) throw new Exception("gulp clean failed")
    },
    gulpTest := {
      val base = (gulpDirectory in Compile).value
      val gulpFileName = (gulpFile in Compile).value
      val result = runGulp(base, gulpFileName, List("test")).exitValue()
      if(result != 0) throw new Exception("gulp clean failed")
    },
    /** activator distは gulp build実行後に行う */
    dist <<= dist.dependsOn(gulpBuild),

    /** activator stageは gulp build実行後に行う */
    stage <<= stage.dependsOn(gulpBuild),

    /** activator cleanは gulp clean実行後に行う */
    clean <<= clean.dependsOn(gulpClean),

    unmanagedResourceDirectories in Assets <+= (gulpDirectory in Compile)(_ / "assets/dist"),

    /** activator run時にはgulp watchも実行する */
    playRunHooks <+= (gulpDirectory, gulpFile).map {
      (base, fileName) => GulpWatch(base, fileName)
    },

    commands <++= gulpDirectory {
      base => Seq("npm", "bower", "yo").map(cmd(_, base))
    }
  )


  private def runGulp(base:sbt.File, fileName:String, args:List[String] = List.empty):Process = {
    if (isWindows) {
      val process: ProcessBuilder = Process("cmd" :: "/c" :: "gulp" :: "--gulpfile=" + fileName :: args, base)
      println(s"Will run: ${process.toString} in ${base.getPath}")
      process.run()
    } else {
      val process: ProcessBuilder = Process("gulp" :: "--gulpfile=" + fileName :: args, base)
      println(s"Will run: ${process.toString} in ${base.getPath}")
      process.run()
    }
  }

  private def cmd(name:String, base:File): Command = {
    if(!base.exists()) base.mkdir()

    Command.args(name, "<" + name + "-command>") {
      (state, args) =>
        if (isWindows) {
          Process("cmd" :: "/c" :: name :: args.toList, base) !<
        } else {
          Process(name :: args.toList, base) !<
        }
        state
    }
  }


  object GulpWatch {
    def apply(base:File, fileName:String): PlayRunHook = {
      object GulpSubProcessHook extends PlayRunHook {
        var process: Option[Process] = None

        override def beforeStarted(): Unit = {
          process = Some(runGulp(base, fileName, "watch" :: Nil))
        }

        override def afterStopped(): Unit = {
          process.foreach(_.destroy())
          process = None
        }
      }
      GulpSubProcessHook
    }
  }
}
