

import org.bbs.apkparser.PackageInfoX.IntentFilterX;

import ext.com.android.server.IntentResolver;

public class BaseIntentResolver extends IntentResolver<IntentFilterX, IntentFilterX> {

	@Override
	protected boolean isPackageForFilter(String packageName,
			IntentFilterX filter) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected IntentFilterX[] newArray(int size) {
		return new IntentFilterX[size];
	}

}
