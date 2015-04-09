#!/usr/bin/env bash

set -x

function genLib(){
	dir=$1
	library=$2
	
 	oldDir=`pwd`
	cd export 
	
	ANDROID_JAR="/mnt/windows_share/download/adt-bundle-linux-x86_64-20140702/sdk/platforms/android-21/android.jar"
	CLASSPATH="../bin/apklauncher_lib.jar:../libs/android-support-v4.jar"
	CLASSPATH=${CLASSPATH}:$ANDROID_JAR

	mkdir class
	javac -d class -classpath $CLASSPATH $dir/org/bbs/apklauncher/api/*.java
	cd class
	jar cf ../../$library  org/bbs/apklauncher/api/*.class
	rm -rf class
	
	cd $oldDir
}

version=0.1
genLib app apklauncher_app_v$version.jar
genLib plugin apklauncher_plugin_v$version.jar
