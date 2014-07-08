package code.snippet

import code.rest.Streamer

/**
 * @author ssb
 *         21/04/14
 */
class Index {
  def render =
    <ul class="list-group">
      { Streamer.videos.map {
          video => {
            <li class="list-group-item">
              <a href={"/video/" + video._1}>
                {video._2.title}
              </a>
            </li>
          }
      } }
    </ul>
}
