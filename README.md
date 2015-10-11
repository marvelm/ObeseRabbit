# ObeseRabbit

ObeseRabbit is an example of [Lift](http://liftweb.net)'s HTTP streaming capabilities.

I am currently just playing with the **`StreamingResponse`** class to return a chunked response of a video.

Chrome and IOS have been tested. The only difference on the server side is the video container being served.

* Chrome - MKV with a `video/webm` type attribute in the source tag.
* IOS - MP4 with `video/mp4`
* The video codec in both cases is h.264 and the audio codec is mp3.

## How to use
    ./init.sh
    ./sbt
    container:start

## Requirements
FFmpeg for converting videos
