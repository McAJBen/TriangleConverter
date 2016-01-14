# TriangleConverter
This program will convert .png and .jpg images into a version of the image drawn only using triangles.
![Alt text](Examples/earth.png?raw=true "Earth")
![Alt text](Examples/hue.png?raw=true "Hue")
![Alt text](Examples/starrynight.png?raw=true "Starry Night")

##How it works:

1. Each image in the same directory as the java executable is read and 'converted' when one is completed the program looks for another, if there are none it will wait for 10 seconds before looking again.
2. Once it has found an image it will divide it into almost equal portions to 'convert' separately called Blocks.
3. Each Block has its own thread and will attempt to draw triangles to match the original image.
4. When all Block files are completed the program will compile all of the images into a new image and save it, along with a ".trifi" file which contains the triangle data.

##How comparisons work:

1. The only input Block has is the 'Score' it gets when its solution is compared to the original.
2. The Block file 'submits' a set of triangles to be scored.
3. The Block file gets back a score based on the pixel color values and how far off or how close they are to the original.
4. This score is represented as a double value from 0 -> 1;

##How the Block File works:

1. The Block file will place a random triangle on the blank 'field'.
2. The triangle goes through multiple stages until it is deemed 'stagnant' or when it has not improved for a set amount of comparisons.
  1. First a Triangle is randomly moved and the color is changed to find a rough area that needs to be improved on. 
  2. Next the color is modified by a factor of at most 10%, this narrows down the average color that the triangle is covering
  3. The shape of the triangle is modified until it has found any better locations.
  4. The shape is modified further by a factor of 10% to narrow down the exact perfect position.
  5. The triangle is checked if it can be removed without decreasing the score, so if the triangle is useless or behind another triangle.
  6. Another Triangle is added and the process repeats until the maximum number of triangles is hit.
3. The Block file then outputs an image along with a file of the triangle data.

###Using this method this program can artificially copy an image without actually knowing what the image is.

Based on [http://alteredqualia.com/visualization/evolve/](http://alteredqualia.com/visualization/evolve/).


