#!/usr/bin/env bash

function update(){
	set -x
	keystore=~/w/repo.tv/ott_tv/Youku_TV/youku_android_key.keystore
	keystoreAlias=youku_android_key.keystore
	keystorePass=wuxian_youku_android
	
	echo $1 
	apk=$1
	destApkName=`basename $apk`	
	destApk=./$destApkName
	cp -f $apk $destApk 
	tmpD=tmp
	mkdir $tmpD
	unzip $destApk assets/META-INF/MANIFEST.MF -d $tmpD
	metainfoD=META-INF
	mkdir $metainfoD
	cp $tmpD/assets/META-INF/MANIFEST.MF $metainfoD/
        zip -r ./$destApkName $metainfoD/ 
	jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore $keystore ./$destApk $keystoreAlias -storepass $keystorePass
	
	rm -rf $tmpD
	rm -rf $metainfoD
}

update $@
