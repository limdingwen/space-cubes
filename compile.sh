#!/bin/sh

javac -verbose -d . -classpath ".:lib/guava-13.0.1.jar:lib/jinput.jar:lib/jna.jar:lib/lwjgl.jar:lib/lwjgl_util.jar:lib/PNGDecoder.jar" `find src | grep .java`

rm SpaceCubes.jar
jar cvfm SpaceCubes.jar MANIFEST `find com | grep .class`
