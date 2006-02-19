package org.h3270.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.meterware.httpunit.Button;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebWindow;
import com.meterware.servletunit.ServletUnitClient;

/**
 * TODO this test uses the actual h3270-config.xml configuration
 * file. depending on the settings there this test may fail.
 * the configuration should somehow be mocked (ab 19.2.2006).
 * 
 * @author Alphonse Bendt
 * @version $Id$
 */
public class PreferencesTest extends AbstractServletTest {

    public PreferencesTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(PreferencesTest.class);
    }

    public void testSetActiveColorScheme() throws Exception {
        trySetAllOptionValues("colorscheme");
    }

    public void testSetFont() throws Exception {
        trySetAllOptionValues("font");
    }

    public void testSetRenderer() throws Exception {
        WebForm prefsForm = openPrefsForm(client);
        assertNotNull(prefsForm.getParameterValue("render"));

        prefsForm.setCheckbox("render", false);

        prefsForm.getButtonWithID("prefs-ok").click();

        WebResponse response = client.getCurrentPage();
        Cookie cookie = getFirstCookieFromResponse(response);

        ServletUnitClient anotherClient = newClient(cookie);

        prefsForm = getPrefsForm(openPrefsWindow(anotherClient));

        assertNull(prefsForm.getParameterValue("render"));
    }

    private void trySetAllOptionValues(String option) throws Exception {
        WebForm prefsForm = openPrefsForm(client);

        String[] validValues = prefsForm.getOptionValues(option);

        ServletUnitClient anotherClient = client;

        for (int x = 0; x < validValues.length; ++x) {
            String validValue = validValues[x];
            prefsForm.setParameter(option, validValue);

            prefsForm.getButtonWithID("prefs-apply").click();

            // fetch cookie
            WebResponse response = anotherClient.getCurrentPage();
            Cookie cookie = getFirstCookieFromResponse(response);

            anotherClient = newClient(cookie);

            prefsForm = openPrefsForm(anotherClient);
            String defaultValue = prefsForm.getParameterValue(option);

            assertEquals(validValue, defaultValue);
        }
    }

    private WebForm openPrefsForm(ServletUnitClient client) throws Exception {
        return getPrefsForm(openPrefsWindow(client));
    }

    private WebForm getPrefsForm(WebWindow prefsWindow) throws Exception {
        WebResponse prefs = prefsWindow.getCurrentPage();

        WebForm prefsForm = prefs.getFormWithName("prefs");

        return prefsForm;
    }

    private WebWindow openPrefsWindow(ServletUnitClient client)
            throws Exception {
        WebResponse screen = client.getResponse(SERVLET_URL);

        WebForm controlForm = screen.getFormWithName("control");

        Button button = controlForm.getButtonWithID("prefs");

        button.click();

        WebWindow prefsWindow = client.getOpenWindow("Preferences");

        return prefsWindow;
    }
}