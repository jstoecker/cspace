Interactive Visualization of 3D Configuration Spaces
====================================================

Please refer to the website for information on how to use the software : http://jstoecker.github.io/cspace/

Compiling
---------
When cloning the repository, be careful that you also initialize the submodules that the code depends on. The easiest
way to download the source is using the recursive flag:

$ git clone --recursive git://github.com/jstoecker/cspace.git

You must have Apache Ant and a Java Development Kit (JDK) installed. After you have cloned the repository, enter the 
cspace directory and run ant to compile:

$ cd cspace
$ ant

You should see output placed in the bin/ directory.
