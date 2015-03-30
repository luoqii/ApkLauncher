#!/bin/bash
#
# replace android.util.Log with blablabla.
# bysong@tudou.com
# 

# with this text, it means sb use android.util.Log method, fix it!!!
#Robot_R="\/\*Robot\*\/"
Robot_R=""

for file in `find . -name "*.java"`; do

echo "file: $file"

# Activity
#ExpandableListActivity
#TabActivity
cp $file ${file}.bak
sed  "/^.*new Intent(/ s/\(.*\)new Intent(\(.*\)/\1new org.bbs.apklauncher.emb.IntentHelper(\2/" ${file}.bak > $file
rm ${file}.bak


done

