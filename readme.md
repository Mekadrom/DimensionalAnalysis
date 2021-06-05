To build, run: `./gradlew build`

To run, run: `./gradlew run`

To package a shadow jar: `./gradlew shadowJar`

<h1>Dimensional Analysis</h1>

<p>Dimensional Analysis is an exploratory project that uses IND4J n-dimensional python-style arrays in Java.</p>
<p>This project allows the user to visualize an n-dimensional rectangular prism projected down to 2D space.</p>
<p>This program also allows the user to:</p>

* Change the angle of any n-dimensional object about a (n-2)-dimensional feature of that object.
    * For example, a square (2D) rotates about a point (0D), a cube (3D) rotates about a line (1D), and a tesseract (4D) would rotate about planes (2D).
* Animate the object by a constant rotation around any (n-2)-dimensional feature.
* Change the length of any side of the n-dimensional object.
* Choose either perspective or orthographic projection for any projection from one dimension down to another.
* Change the distance the camera is from the subject along any axis

As well as some aesthetic choices such as line length, point size, and zoom.

<p>Known issues:</p>

* While the dimensions are theoretically only programmatically limited by the number of letters that can be used to represent each axis/dimension, the effective limit for most computers is about 7 dimensions due to the processing requirements of IND4J, the matrix math involved, and the recursive drawing.
* For some reason the "toggle progression" button also massively lags the application

<p>This is lazily maintained.</p>
