#!/usr/bin/env bash

set -x

function genLib(){
	dir=$1
	library=$2
	
 	oldDir=`pwd`
	cd export 
	
	ANDROID_JAR="/mnt/windows_share/download/adt-bundle-linux-x86_64-20140702/sdk/platforms/android-21/android.jar"
	CLASSPATH="../bin/apklauncher_sdk.jar:../libs/android-support-v4.jar"
	CLASSPATH=${CLASSPATH}:$ANDROID_JAR

	mkdir class
	java_source=$dir/org/bbs/apklauncher/api/*.java
	clazz=org/bbs/apklauncher/api/*.class
	if [[ "$dir" == "app" ]] ; then
		echo "pwd: `pwd`"
		src=../src/org/bbs/apklauncher/emb/IntentHelper.java
		dst=$dir/org/bbs/apklauncher/emb/IntentHelper.java
		mkdir `dirname $dst`
		cp -f  $src $dst
		#sed "/^package/ s/.*/package org.bbs.apklauncher.api;/" $src > $dst

		java_source="$java_source $dir/org/bbs/apklauncher/emb/*.java"
		clazz="$clazz org/bbs/apklauncher/emb/*.class"
	fi
	javac -d class -classpath $CLASSPATH  $java_source 
	cd class
	jar cf ../../$library   $clazz
	cd ..
	rm -rf class
	
	cd $oldDir
}

#version=_v0.2
genLib app apklauncher_app$version.jar
genLib plugin apklauncher_plugin$version.jar
