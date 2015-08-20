package ext.android.util;

//base on android 5.1.0_r5
import android.util.Log;

public class Slog {

	public static void v(String tag, String string) {
		Log.v(tag, string);
	}

	public static void w(String tag, String string) {
		Log.d(tag, string);
	}

}
