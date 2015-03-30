#!/bin/bash
#
# replace android.util.Log with blablabla.
# bysong@tudou.com
# 

# with this text, it means sb use android.util.Log method, fix it!!!
#Robot_R="\/\*Robot\*\/"
Robot_R=""

export PREFIX=com.example.android.apis.stub.Base_
for file in `find . -name "*.java"`; do

echo "file: $file"

# Activity
for a in Activity TabActivity PreferenceActivity	
do
#ExpandableListActivity
#TabActivity
cp $file ${file}.bak
sed  "/^.*extends\s*${a}.*/ s/\(.*extends\s*\)\(${a}\)\(.*\)/\1${PREFIX}\2\3/" ${file}.bak > $file
rm ${file}.bak
done


done

