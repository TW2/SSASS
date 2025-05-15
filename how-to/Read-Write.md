# Read and Write
## The library
To read or write an ASS file in this library,
you have to import ASS class :
```
import org.wingate.ssass.ass.ASS;
```
### Read
```
ASS ass = ASS.read("your-path-to-file");
```
### Write
```
ASS.write(ass, "a-dest-path-to-file", "software", "www");
```
or
```
ASS.write(ass, "a-dest-path-to-file");
```
### Edit
To access...
- information, use ```ass.getInfos()```.
- styles, use ```ass.getStyles()```.
- events, use ```ass.getEvents()```.
- actors, use ```ass.getActors()```.
- effects, use ```ass.getEffects()```.