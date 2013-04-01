cspace
======

Configuration space visualization in 2D + 3D

Compiling
---------
You must have Apache Ant and a Java Development Kit (JDK) installed. In the
terminal, enter the following:

$ ant

You should see output placed in the bin/ directory.


Running
-------
The program must be started from the terminal with a command line argument: the
path to a directory containing the cspace model and path files. A bash script
is provided for Unix-like systems. Run one of the examples like so:

$ ./cspace scenes/2rooms

or, alternatively (on any system):

$ java -jar bin/CSpace.jar scenes/3pin
