

packagePath="org/bbs/apklauncher/api"

def usage(){
	println "useage:"
	println "${0} "
}


usage()

def gen_app_library(){
 	println "gen library for app development."
 	
	["PreferenceActivity", "Activity", "FragmentActivity",
	 "TabActivity", "ListActivity"]
	.each(){
		gen_app_library(it)
	}
}

def gen_app_library(String activity){
	println "gen library for app developement." + " activity: " + activity
	
	templateActivityFile = new File("template/Library_App_Activity.java.template")
	File f = new File("export/app/" + packagePath + "/Base_" + activity + ".java")
	f.mkdirs()
	f.delete()
	f.createNewFile()
	
    REPLACE='SUPER_CLASS'
    templateActivityFile.eachLine() {
    	//println it
    	
        if (it.contains(REPLACE)) {
            it = it.replaceAll(REPLACE, activity)
        }
                
        f.append(it)
        f.append("\n")
    }
}

def gen_plugin_library(){
	["PreferenceActivity", "Activity", "FragmentActivity",
	 "TabActivity", "ListActivity"]
	.each(){
		gen_plugin_library(it)
	}
} 

def gen_plugin_library(String activity){
	println "gen library for plugin developement." + " activity: " + activity
	
	templateActivityFile = new File("template/Library_Plugin_Activity.java.template")
	File f = new File("export/plugin/" + packagePath + "/Base_" + activity + ".java")
	f.mkdirs()
	f.delete()
	f.createNewFile()
	
    REPLACE='SUPER_CLASS'
    templateActivityFile.eachLine() {
    	//println it
    	
        if (it.contains(REPLACE)) {
            it = it.replaceAll(REPLACE, activity)
        }
                
        f.append(it)
        f.append("\n")
    }
}


gen_app_library()
gen_plugin_library()
