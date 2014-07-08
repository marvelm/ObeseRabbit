package code.snippet


import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml
import code.model.Video
import code.rest.Streamer
import java.io.File

/**
 * @author ssb
 *         21/04/14
 */
class Authorized {
  var file = ""
  var title = ""

  def titleI = SHtml.onSubmit(title = _)

  def fileI = SHtml.onSubmit(file_ => {
    printf("Loaded video file(%s) title(%s)", file.toString, title)
    Streamer.loadVideo(Video(title, new File(file_)))
  })
}
