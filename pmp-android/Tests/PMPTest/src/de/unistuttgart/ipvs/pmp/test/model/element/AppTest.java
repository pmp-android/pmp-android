package de.unistuttgart.ipvs.pmp.test.model.element;

import java.util.Locale;

import android.test.InstrumentationTestCase;
import de.unistuttgart.ipvs.pmp.model.element.app.App;
import de.unistuttgart.ipvs.pmp.model.element.app.MockAppPeristenceProvider;
import de.unistuttgart.ipvs.pmp.test.ModelTestUtils;
import de.unistuttgart.ipvs.pmp.xmlutil.common.ILocalizedString;
import de.unistuttgart.ipvs.pmp.xmlutil.common.LocalizedString;

/**
 * Test to verify the correct functionality of {@link App} class.
 * 
 * @author Jakob Jarosch
 */
public class AppTest extends InstrumentationTestCase {
    
    private App app;
    private MockAppPeristenceProvider appPersistence;
    
    
    @Override
    protected void setUp() throws Exception {
        app = new App(ModelTestUtils.App.PMP_TEST_APP_1.toString());
        appPersistence = new MockAppPeristenceProvider(app);
        app.setPersistenceProvider(appPersistence);
        
        Locale.setDefault(Locale.GERMAN);
    }
    
    
    /**
     * Verify that name and description is returned correctly depending on what default locale is set.
     */
    public void test01_AppNameAndDescription() {
        ILocalizedString name_de = new LocalizedString();
        name_de.setLocale(Locale.GERMAN);
        name_de.setString("Anwendung");
        
        ILocalizedString name_en = new LocalizedString();
        name_en.setLocale(Locale.ENGLISH);
        name_en.setString("Application");
        
        ILocalizedString description_de = new LocalizedString();
        description_de.setLocale(Locale.GERMAN);
        description_de.setString("Beschreibung");
        
        ILocalizedString description_en = new LocalizedString();
        description_en.setLocale(Locale.ENGLISH);
        description_en.setString("Description");
        
        assertNull("No name should be available", app.getName());
        assertNull("No description should be available", app.getName());
        
        appPersistence.ais.addName(name_en);
        appPersistence.ais.addDescription(description_en);
        assertEquals("Name is not in english version", app.getName(), name_en.getString());
        assertEquals("Description is not in english version", app.getName(), name_en.getString());
        
        appPersistence.ais.addName(name_de);
        appPersistence.ais.addDescription(description_de);
        assertEquals("Description is not in german version", app.getName(), name_de.getString());
    }
}
