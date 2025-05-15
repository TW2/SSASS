# Use the renderer
### What you need ?
You can use the default reader of this library
to open your ASS file and do the rendering.
This is the way to integrate the process
directly in your Java program without do extra
coding.

You need the ASS file, the time in microseconds.
Note that the AssTime class use milliseconds only
in a double value.

### How to get image ?

The output package contains ```OutputImage.java```
which is used to create a blended image or
per-layer images map in static call.

#### Here is the two ways :
One image :
```
BufferedImage getImage(ASS ass, AssEvent event, int width, int height)
```
Per-layer images map :
```
Map<Integer, BufferedImage> getImages(ASS ass, long micros, int width, int height)
```