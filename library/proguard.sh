#!/usr/bin/env bash

if [[ "x$ANDROID_HOME" == "x" ]] ; then
	echo "no ANDROID_HOME exported."
	exit 1
fi

if [[ "x$PROGUARD_HOME" == "x" ]] ; then
	echo "no PROGUARD_HOME exproted."
	exit 1
fi

$PROGUARD_HOME/bin/proguard.sh @proguard.config -libraryjar $ANDROID_HOME/platforms/android-21/android.jar
