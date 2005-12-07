package org.h3270.test;

import java.io.File;

import junit.framework.TestCase;

import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

/**
 * @author Alphonse Bendt
 * @version $Id$
 */
abstract class AbstractServletTest extends TestCase {

    protected ServletRunner servletRunner;
    protected ServletUnitClient client;

        public AbstractServletTest(String name) {
            super(name);
        }

        public void setUp() throws Exception {            
            servletRunner = new ServletRunner(new File("WEB-INF/web.xml"));

            client = newClient();
        }

        protected ServletUnitClient newClient() {
                return servletRunner.newClient();
        }

        protected ServletUnitClient newClient(Cookie cookie) {
            ServletUnitClient client = newClient();

            client.addCookie(cookie.name, cookie.value);

            return client;
        }

        protected Cookie getFirstCookieFromResponse(WebResponse response) {
            String name = response.getNewCookieNames()[0];

            return new Cookie(name, response.getNewCookieValue(name));
        }

        protected static final String SERVLET_URL = "http://localhost/servlet";
}

