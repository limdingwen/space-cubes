### Welcome to the Github repo! ###

Versions > 12w441

### Binary sources ###

Releases of binary files
Compiling from git

### Compilation from git ###

Run ./compile.sh
Binary will be ./SpaceCubes.jar
Please remember that the jar depends on lib/, so you have to copy it to whatever folder you move the jar to.

### Running .jar ###

You will need:
|
|- SpaceCubes.jar
|- lib/
   |- lwjgl.jar
   |- lwjgl_util.jar
   |- etc
|- native/
   |- macosx
      |- *.so
      |- *.dylib
      |- etc
   |- etc

You can get the lib from either the git, or from lwjgl and other libraries such as guava. Check the manifest (SpaceCubes.jar/META-INF/MANIFEST.MF) to see what jar files it must have. You can edit that too to change which folder the jar files will be in.

You can get the native from either the git, or from lwjgl. You can copy it directly from lwjgldownloadedfoldersomething/native.

The libraries and natives are the correct versions that are used to develop the program. However it may not be the latest versions.

java -Djava.library.path=native/<os> -jar SpaceCubes.jar
