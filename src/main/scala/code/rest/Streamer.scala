package code.rest

import net.liftweb.http._
import net.liftweb.http.rest.RestHelper
import net.liftweb.common._
import net.liftweb.util.SecurityHelpers.randomString
import net.liftweb.util.StringHelpers.parseNumber
import scala.collection.mutable
import java.io.InputStream
import java.io.FileInputStream
import net.liftweb.http.StreamingResponse
import scala.Some
import code.model.Video
import sys.process._
import java.io.File

/**
 * @author ssb
 *         21/04/14
 */

object Streamer extends RestHelper {
  var videos = mutable.HashMap[String, Video]()

  //private val salt = SecurityHelpers.randomString(6)

  // intersperses the salt within the str then hashes it
  //private def hash(str: String) = SecurityHelpers.hash(str.mkString(salt)).substring(0, 3)

  val outDirectory = "out"

  def loadVideo(v: Video) {
    val outFilename = randomString(3)
    val outFile = new File(outDirectory + "/" + outFilename + ".mp4")
    val convertedVideo = Video(v.title, outFile)
    new Thread {
      override def run() {
        println(s"Converting ${v.file.toString} to ${outFile.toString}")
        println(s"ffmpeg -i ${v.file.toString} -vcodec libx264 -acodec mp3 ${outFile.toString}" !!)
        convertedVideo.conversionCompleted = true
      }
    }.start()
    videos += ((outFilename, convertedVideo))
  }

  def has(id: String) = videos.contains(id)

  def title(id: String) = videos.get(id).map(_.title).getOrElse("Video not found")

  private def stream(req: Req, video: Video): Box[(List[(String, String)], InputStream, Long)] = {
    var content_type = ("Content-Type" -> "video/mp4")
    val range: Box[String] = req.header("Range")
    var start: Long = 0L
    var end: Long = 0L
    val file = video.file

    range match {
      case Full(r) => {
        start = parseNumber(r.substring(r.indexOf("bytes=") + 6))

        end =
          if (r.endsWith("-"))
            file.length - 1
          else
            parseNumber(r.substring(r.indexOf("-") + 1))
      }

      case _ => end = file.length - 1
    }

    // end = file.length - 1

    val headers =
      ("Connection" -> "close") ::
      ("Transfer-Encoding" -> "chunked") ::
      content_type ::
      ("Content-Range" -> ("bytes " + start.toString + "-" + end.toString + "/" + file.length.toString)) ::
      Nil

    val fis = new FileInputStream(file)
    fis.skip(start)
    
    Full(headers, fis, end - start + 1)
  }

  serve {
    case req@Req(("video" :: id :: Nil), _, _) =>
      () => response(req, id)
  }

  implicit def option2box[T](o: Option[T]): Box[T] =
    o match {
      case Some(t: T) =>
        Full(t)
      case _ =>
        Empty
    }

  private def response(req: Req, id: String): Box[LiftResponse] = {
    val x = for {
      video <- videos.get(id)
      stream <- stream(req, video)
    } yield {
      if (video.conversionCompleted) {
        val headers = stream._1
        val fis = stream._2
        val size = stream._3

        StreamingResponse(
          data = fis,
          onEnd = fis.close,
          size,
          headers,
          cookies = Nil,
          code = 206
        )
      }
      else
        PlainTextResponse("The video has not been converted yet")
    }

    if (x.isDefined)
      Full(x.get)
    else
      Full(PlainTextResponse("error"))
  }
}
