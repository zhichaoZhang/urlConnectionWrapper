package in.joye.urlconnection.base;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Created by joye on 2017/11/14.
 */

public class BaseJunitTest extends TestCase {

    @Test
    public void testLog() {
        log("tag", "content");
        assertTrue(true);
    }

    protected void log(String desc, Object o) {
        System.out.println(desc + " : " + o);
    }
}
