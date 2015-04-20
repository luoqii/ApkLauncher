#!/usr/bin/env groovy

class Tag {
	String mName;
	Tag mSuper;
	Tag(String name, Tag superTag){
		mName=name;
                mSuper = superTag;
	}
}

activity = new Tag("Activity", null)
activityGroup = new Tag("ActivityGroup", activity)
accountAuthenticatorActivity = new Tag("AccountAuthenticatorActivity", activity)
aliasActivity = new Tag("AliasActivity", activity)
expandableListActivity = new Tag("ExpandableListActivity", activity)
fragmentActivity = new Tag("FragmentActivity", activity)
listActivity = new Tag("ListActivity", activity)
nativeActivity = new Tag("NativeActivity", activity)
tabActivity = new Tag("TabActivity", activity)
actionBarActivity = new Tag("ActionBarActivity", fragmentActivity)
launcherActivity = new Tag("LauncherActivity", listActivity)
preferenceActivity = new Tag("PreferenceActivity", listActivity)
tabActivity = new Tag("TabActivity", activityGroup)

service = new Tag("Service", null)
abstractInputMethodService = new Tag("AbstractInputMethodService", service)
accessibilityService = new Tag("AccessibilityService", service)
carrierMessagingService = new Tag("CarrierMessagingService", service)
dreamService = new Tag("DreamService", service)
hostApduService = new Tag("HostApduService", service)
intentService = new Tag("IntentService", service)
jobService = new Tag("JobService", service)
mediaBrowserService = new Tag("MediaBrowserService", service)
mediaRouteProviderService = new Tag("MediaRouteProviderService", service)
notificationCompatSideChannelService = new Tag("NotificationCompatSideChannelService", service)
notificationListenerService = new Tag("NotificationListenerService", service)
offHostApduService = new Tag("OffHostApduService", service)
printService = new Tag("PrintService", service)
recognitionService = new Tag("RecognitionService", service)
remoteViewsService = new Tag("RemoteViewsService", service)
settingInjectorService = new Tag("SettingInjectorService", service)
spellCheckerService = new Tag("SpellCheckerService", service)
textToSpeechService = new Tag("TextToSpeechService", service)
tvInputService = new Tag("TvInputService", service)
voiceInteractionService = new Tag("VoiceInteractionService", service)
voiceInteractionSessionService = new Tag("VoiceInteractionSessionService", service)
vpnService = new Tag("VpnService", service)
wallpaperService = new Tag("WallpaperService", service)
inputMethodService = new Tag("InputMethodService", wallpaperService)

broadcastReceiver = new Tag("BroadcastReceiver", null)
appWidgetProvider = new Tag("AppWidgetProvider", broadcastReceiver)
deviceAdminReceiver = new Tag("DeviceAdminReceiver", broadcastReceiver)
restrictionsReceiver = new Tag("RestrictionsReceiver", broadcastReceiver)
wakefulBroadcastReceiver = new Tag("WakefulBroadcastReceiver", broadcastReceiver)

allTags=[
//activity 
activity,activityGroup,accountAuthenticatorActivity,aliasActivity,
expandableListActivity,tabActivity,fragmentActivity,listActivity,
nativeActivity,actionBarActivity,launcherActivity,preferenceActivity,
tabActivity,

//service
service,abstractInputMethodService,accessibilityService,carrierMessagingService,
dreamService,hostApduService,intentService,jobService,mediaBrowserService,
mediaRouteProviderService,notificationCompatSideChannelService,notificationListenerService,
offHostApduService,printService,recognitionService,remoteViewsService,
settingInjectorService,spellCheckerService,textToSpeechService,tvInputService,
voiceInteractionService,voiceInteractionSessionService,vpnService,wallpaperService,
inputMethodService,

// broadcastreceiver
broadcastReceiver,appWidgetProvider,deviceAdminReceiver,restrictionsReceiver,
wakefulBroadcastReceiver

]

def getTags(baseTagName) {
	def tag
	allTags.each { if ("$it.mName" == baseTagName) {tag = it ; return it;}}
	if (null == tag) return
 	println "tag: " + tag
	List tags = new ArrayList();
	tags.add(tag);
	while (tag.mSuper != null) {
		tag = tag.mSuper
		tags.add(tag)
	}
	
    println('all tags for: ' + baseTagName)
	tags.each { println "tag: $it.mName"}
}

getTags("TabActivity")
getTags("ActionBarActivity")

getTags("VpnService")
getTags("InputMethodService")
getTags("AccessibilityService")
