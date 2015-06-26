import org.bbs.apklauncher.Version;

import junit.framework.TestCase;


public class VersionTest extends TestCase {
	public void testExtractVerion() {
		assertEquals("1.0", Version.extractVersion("v1.0aabb"));
		assertEquals("1.0.1", Version.extractVersion("v1.0.1aabb"));
		assertEquals("1.0", Version.extractVersion("v 1.0aabb"));
		assertEquals("1.0", Version.extractVersion("v1.0 aabb"));
		assertEquals("1.01", Version.extractVersion(" v1.01aabb"));
	}
	
	public void testNewer(){
		assertEquals(true, Version.isNewer("1.0", "0.8"));
		assertEquals(true, Version.isNewer("1.0", "0.8.9"));
		assertEquals(true, Version.isNewer("1.0", "0.0.0.0.1"));
		assertEquals(true, Version.isNewer("1.0", "0.111"));
		assertEquals(true, Version.isNewer("1.0", "0.88"));

		assertEquals(true, Version.isNewer("1.0.1", "1.0"));
		assertEquals(true, Version.isNewer("1.1", "1.0"));
		assertEquals(true, Version.isNewer("2.0", "1.0"));
		assertEquals(true, Version.isNewer("1.0.0.1", "1.0"));
		assertEquals(true, Version.isNewer("1001.0.1", "1.0"));
		assertEquals(true, Version.isNewer("2.0", "1.2"));
	}
}
