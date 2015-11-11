#!/usr/bin/env bash

function update(){
	apk=$1

	destApkName=`basename $apk`	
	destApk=./$destApkName
	cp -f $apk $destApk 
	tmpD=tmp
	mkdir $tmpD
	unzip $destApk assets/META-INF/MANIFEST.MF -d $tmpD

	manifest=$tmpD/assets/META-INF/MANIFEST.MF

	update_with_manifest $apk $manifest
}

function update_with_manifest(){
        set -x

        apk=$1
	manifest=$2
	echo apk: $apk 
	echo manifest: $manifest

        #keystore=~/w/repo.tv/ott_tv/Youku_TV/youku_android_key.keystore
        #keystoreAlias=youku_android_key.keystore
        #keystorePass=wuxian_youku_android
        keystore=~/w/ApkLauncher/keystore
        keystoreAlias=CERT
        keystorePass=asdfasdf

        destApkName=`basename $apk`
        destApk=./$destApkName
        cp -f $apk $destApk
        tmpD=tmp
        mkdir -p $tmpD/assets/META-INF
	cp -f $manifest $tmpD/assets/META-INF
        metainfoD=META-INF
        mkdir $metainfoD
        cp $tmpD/assets/META-INF/MANIFEST.MF $metainfoD/
        zip -r ./$destApkName $metainfoD/
        jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore $keystore ./$destApk $keystoreAlias -storepass $keystorePass

        rm -rf $tmpD
        rm -rf $metainfoD

}

if [[ "$#" == "2" ]] ; then
	update_with_manifest $@ 
fi
if [[ "$#" == "1" ]] ; then
	update $@
fi
