package controllers

import java.io.{FileInputStream, File}
import javax.inject.Singleton
import play.api._
import play.api.mvc.{AnyContent, Action}
import play.api.Play.current
import play.api.http.LazyHttpErrorHandler
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

/**
  * public/ではなくfrontend/以下から各アセットを呼び出すためのAssetsBuilder
  * TODO:versionedには未対応
  */
@Singleton
class FrontendAssets  extends Assets(LazyHttpErrorHandler) {
  def index = FrontendAssets.index
  def at(file: String) = FrontendAssets.at(file:String)
}


object FrontendAssets extends Assets(LazyHttpErrorHandler) {
  private lazy val logger = Logger(getClass)

  def index = Action.async { request =>
    if(request.path.startsWith("/")){
      at("index.html").apply(request)
    }else{
      Future(Redirect(request.path + "/"))
    }
  }

  val runtimeDevDirs: Option[java.util.List[String]] = Play.configuration.getStringList("frontend.devDirs")
  // A directory in higher priority comes first.
  val basePaths: List[java.io.File] = runtimeDevDirs match {
    case Some(dirs:List[String]) => dirs.map(Play.application.getFile _)
    case _ => List(
      Play.application.getFile("frontend/.tmp/serve")
      , Play.application.getFile("frontend/assets/dist")
      //, Play.application.getFile("frontend/src")  //とりあえず問題があるまではオリジナルが置いてああるほうは見ない
    )
  }


  private[controllers] def devAssetHandler(file: String): Action[AnyContent] = Action { request =>
    // Generates a non-strict list of the full paths
    val targetPaths = basePaths.view map {
      new File(_, file)
    }

    // Generates responses returning the file in the dev and test modes only (not in the production mode)
    val responses = targetPaths filter { file =>
      file.exists()
    } map { file =>
      if (file.isFile) {
        logger.info(s"Serving $file")
        Ok.sendFile(file, inline = true).withHeaders(CACHE_CONTROL -> "no-store")
      } else {
        Forbidden(views.html.defaultpages.unauthorized())
      }
    }

    // Returns the first valid path if valid or NotFound otherwise
    responses.headOption getOrElse NotFound("404 - Page not found error\n" + request.path)
  }

  private[controllers] def prodAssetHandler(file: String): Action[AnyContent] = Assets.at("/public", file)

  lazy val atHandler:String => Action[AnyContent] = if(Play.isProd) prodAssetHandler(_:String) else devAssetHandler(_:String)

  def at(file:String): Action[AnyContent] = atHandler(file)

  /** 一旦存在しないファイルは考慮しない。 */
  def getStream(file:String) = {
    val targetPaths = basePaths.view map {
      new File(_, file)
    }

    val responses = targetPaths filter { file =>
      file.exists()
    } map { file =>
      file
    }
    new FileInputStream(responses.head)
  }
}

