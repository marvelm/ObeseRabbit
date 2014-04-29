package code.snippet


import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml
import code.lib.{Video, Streamer}
import java.io.File

/**
 * @author ssb
 *         21/04/14
 */
class Authorized {
  var mkv = ""
  var mp4 = ""
  var title = ""

  def newVideo() {
    printf("Loaded video mkv %s mp4 %s title %s", mkv, mp4, title)
    Streamer.loadVideo(Video(title, new File(mp4), new File(mkv)))
  }

  def titleI = SHtml.onSubmit(title = _)

  def mkvI = SHtml.onSubmit(mkv = _)

  def mp4I = SHtml.onSubmit {
    str =>
      mp4 = str
      newVideo()
  }
}
