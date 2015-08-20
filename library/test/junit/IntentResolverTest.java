import java.util.List;

import org.bbs.apkparser.PackageInfoX.IntentFilterX;

import android.content.Intent;
import junit.framework.TestCase;


public class IntentResolverTest extends TestCase {
	private Intent mIntent;
	private BaseIntentResolver mResolver;
	private List<IntentFilterX> mResult;

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		
	}
	
	public void testAction(){
		mResolver = new BaseIntentResolver();
		mResolver.addFilter(actionFilter("a"));		
		mIntent = new Intent();
		mIntent.setAction("a");
		mResult = mResolver.queryIntent(mIntent, null, true, 0);
		assertTrue(mResult != null && mResult.size() == 1);		

		mResolver = new BaseIntentResolver();
		mResolver.addFilter(actionFilter("a"));		
		mIntent = new Intent();
		mIntent.setAction("a");
		mResolver.addFilter(actionFilter("b"));
		mResolver.addFilter(actionFilter("c"));
		mResolver.addFilter(actionFilter("d"));
		mResult = mResolver.queryIntent(mIntent, null, true, 0);
		assertTrue(mResult != null && mResult.size() == 1);

		mResolver = new BaseIntentResolver();
		mResolver.addFilter(actionFilter("a"));		
		mIntent = new Intent();
		mIntent.setAction("a");
		mResolver.addFilter(actionFilter("a"));
		mResolver.addFilter(actionFilter("c"));
		mResolver.addFilter(actionFilter("d"));
		mResult = mResolver.queryIntent(mIntent, null, true, 0);
		assertTrue(mResult != null && mResult.size() == 2);	
	}

	private IntentFilterX actionFilter(String action) {
		IntentFilterX f = new IntentFilterX();
		f.addCategory(Intent.CATEGORY_DEFAULT);
		f.addAction(action);
		return f;
	}
}
