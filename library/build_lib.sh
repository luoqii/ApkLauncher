#!/usr/bin/env bash

set -x

function genLib(){
	dir=$1
	library=$2
	
 	oldDir=`pwd`
	cd export 
	
	ANDROID_JAR="/mnt/big_storage/android_sdk/platforms/android-21/android.jar"
	CLASSPATH="../bin/apklauncher_sdk.jar:../libs/android-support-v4.jar:../libs/android-support-v7-appcompat.jar"
	CLASSPATH=${CLASSPATH}:$ANDROID_JAR

	mkdir class
	java_source=$dir/org/bbs/apklauncher/api/*.java
	clazz=org/bbs/apklauncher/api/*.class
	if [[ "$dir" == "app" ]] ; then
		echo "pwd: `pwd`"
		src=../src/org/bbs/apklauncher/emb/
		dst=$dir/org/bbs/apklauncher/emb/
		mkdir `dirname $dst`

		for f in IntentHelper.java PendingIntentHelper.java ; do
			mkdir -p $dst
			cp -f  $src/$f $dst/$f
			#sed "/^package/ s/.*/package org.bbs.apklauncher.api;/" $src > $dst
			if [[ "$f" == "PendingIntentHelper.java" ]] ; then
				cp -f $dst/$f $dst/${f}.bak
				sed "/parseContext(c/ s/\(.*\)\(parseContext(c\)\(.*\)/\1(c\3/" $dst/${f}.bak > $dst/$f
				rm $dst/${f}.bak
			fi
		done
			

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

#rm -rf export/app export/plugin
groovy host_target_activity.groovy
groovy host_target_service.groovy

#version=_v0.2
genLib app apklauncher_app$version.jar
genLib plugin apklauncher_plugin$version.jar
