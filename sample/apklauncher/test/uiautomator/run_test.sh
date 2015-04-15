#!/usr/bin/env bash

set -x

export PROJECT_NAME=uiautomator
appPackageName="org.bbs.apklauncher.demo"
appMainclassName="org.bbs.apklauncher.demo.ApkLuncherActivity"
testPackaeName="org.bbs.apklauncher.uiautomator"

if [[ "x$ANDROID_TARGET" == "x" ]] ; then
  echo "no ANDROID_TARGET, you should export it"
  exit 1
fi

# android/build/envsetup.sh
function pid()
{
    local prepend=''
    local append=''
    if [ "$1" = "--exact" ]; then
        prepend=' '
        append='$'
        shift
    fi
    local EXE="$1"
    if [ "$EXE" ] ; then
        local PID=`adb shell ps \
            | tr -d '\r' \
            | \grep "$prepend$EXE$append" \
            | sed -e 's/^[^ ]* *\([0-9]*\).*$/\1/'`
        echo "$PID"
    else
        echo "usage: pid [--exact] <process name>"
                return 255
    fi
}

PID=$(pid --exact $appPackageName)
adb shell kill -9 $PID
adb shell am start $appPackageName/$appMainclassName
sleep 3s

android create uitest-project -n $PROJECT_NAME -t $ANDROID_TARGET -p .
ant build
if [[ "$?" != "0" ]] ; then
	echo "build error, please fix this error."
	exit 1
fi
adb push bin/$PROJECT_NAME.jar /data/local/tmp/
#adb shell uiautomator runtest $PROJECT_NAME.jar -e class com.youku.tv.uiautomator.HomeUiTest#testFocusSaveAndRestore_top
#adb shell uiautomator runtest $PROJECT_NAME.jar -e class com.youku.tv.uiautomator.HomeUiTest#testFocusSaveAndRestore_left2right

#adb shell uiautomator runtest $PROJECT_NAME.jar -c ${testPackaeName}.ApiDemo_UiTest 
adb shell uiautomator runtest $PROJECT_NAME.jar -c ${testPackaeName}.ApiDemo_UiTest#testApiDemo_tranverse
#adb shell uiautomator dump /data/local/tmp/window_dump.xml
#adb pull /data/local/tmp/window_dump.xml .
#cat window_dump.xml
