package code.snippet

import scala.xml.NodeSeq
import net.liftweb.http.S
import code.rest.Streamer

/**
 * @author ssb
 *         21/04/14
 */
class VideoSnippet(id: String) {
  def render(): NodeSeq = {
    if (Streamer.has(id))
      <video controls=" ">
        <source src={"/mp4/" + id} type="video/mp4"/>
      </video>
    else
      <div>
        Invalid url
      </div>
  }
}
