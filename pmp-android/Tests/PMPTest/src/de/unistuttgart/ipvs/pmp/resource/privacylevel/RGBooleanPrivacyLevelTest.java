package de.unistuttgart.ipvs.pmp.resource.privacylevel;

import java.util.HashMap;

import android.test.AndroidTestCase;
import de.unistuttgart.ipvs.pmp.Constants;

/**
 * A test specifically for {@link BooleanPrivacyLevel} down to {@link PrivacyLevel}.
 * 
 * @author Tobias Kuhn
 * 
 */
public class RGBooleanPrivacyLevelTest extends AndroidTestCase {
    
    /**
     * Default name and description
     */
    private static final String BPL_NAME = "BPL_NAME";
    private static final String BPL_DESC = "BPL_DESC";
    
    /**
     * Further locales, undefined should be undefined
     */
    private static final String STUPID_LOCALE = "ed";
    private static final String UNDEFINED_LOCALE = "ede";
    
    /**
     * Further name and description
     */
    private static final String BPL_STUPID_NAME = "BPL_EMAN";
    private static final String BPL_STUPID_DESC = "BPL_CSED";
    
    /**
     * App idents, NO_APP_IDENT should not be an app
     */
    private static final String APP_1_IDENT = "APP_1";
    private static final String APP_2_IDENT = "APP_2";
    private static final String NO_APP_IDENT = "NO_APP";
    
    /**
     * Not a boolean value
     */
    private static final String NON_BOOLEAN_VALUE = "MAYBE";
    
    private HashMap<String, String> hm;
    private BooleanPrivacyLevel bpl;
    
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.bpl = new BooleanPrivacyLevel(BPL_NAME, BPL_DESC);
        this.hm = new HashMap<String, String>();
    }
    
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    
    public void testParseValueString() throws PrivacyLevelValueException {
        assertEquals(Boolean.TRUE, this.bpl.parseValue(Boolean.TRUE.toString()));
    }
    
    
    /**
     * Tests whether parseValue() correctly throws exceptions
     */
    public void testParseValueStringFaulty() {
        try {
            this.bpl.parseValue(NON_BOOLEAN_VALUE);
            fail();
        } catch (PrivacyLevelValueException plve) {
        }
    }
    
    
    public void testGetName() {
        assertEquals(BPL_NAME, this.bpl.getName(Constants.DEFAULT_LOCALE));
    }
    
    
    public void testGetDescription() {
        assertEquals(BPL_NAME, this.bpl.getName(Constants.DEFAULT_LOCALE));
    }
    
    
    public void testSetNames() {
        this.hm.put(Constants.DEFAULT_LOCALE, BPL_NAME);
        this.hm.put(STUPID_LOCALE, BPL_STUPID_NAME);
        this.bpl.setNames(this.hm);
        
        assertEquals(BPL_NAME, this.bpl.getName(Constants.DEFAULT_LOCALE));
        assertEquals(BPL_STUPID_NAME, this.bpl.getName(STUPID_LOCALE));
        assertEquals(BPL_NAME, this.bpl.getName(UNDEFINED_LOCALE));
    }
    
    
    /**
     * Tests whether setNames() throws exceptions
     */
    public void testSetNamesFaulty() {
        this.hm.put(STUPID_LOCALE, BPL_STUPID_NAME);
        
        try {
            this.bpl.setNames(this.hm);
            fail();
        } catch (IllegalArgumentException iae) {
        }
    }
    
    
    public void testSetDescriptions() {
        this.hm.put(Constants.DEFAULT_LOCALE, BPL_DESC);
        this.hm.put(STUPID_LOCALE, BPL_STUPID_DESC);
        this.bpl.setDescriptions(this.hm);
        
        assertEquals(BPL_DESC, this.bpl.getDescription(Constants.DEFAULT_LOCALE));
        assertEquals(BPL_STUPID_DESC, this.bpl.getDescription(STUPID_LOCALE));
        assertEquals(BPL_DESC, this.bpl.getDescription(UNDEFINED_LOCALE));
    }
    
    
    /**
     * Tests whether setDescriptions() throws exceptions
     */
    public void testSetDescriptionsFaulty() {
        this.hm.put(STUPID_LOCALE, BPL_STUPID_NAME);
        
        try {
            this.bpl.setDescriptions(this.hm);
            fail();
        } catch (IllegalArgumentException iae) {
        }
    }
    
    
    public void testGetHumanReadableValue() throws PrivacyLevelValueException {
        assertEquals(Boolean.FALSE.toString(),
                this.bpl.getHumanReadableValue(Constants.DEFAULT_LOCALE, Boolean.FALSE.toString()));
    }
    
    
    /**
     * Tests whether testGetHumanReadableValue() throws exceptions
     */
    public void testGetHumanReadableValueFaulty() {
        try {
            this.bpl.getHumanReadableValue(Constants.DEFAULT_LOCALE, NON_BOOLEAN_VALUE);
            fail();
        } catch (PrivacyLevelValueException plve) {
        }
    }
    
    
    public void testPermitsStringString() throws PrivacyLevelValueException {
        assertTrue(this.bpl.permits(Boolean.TRUE.toString(), Boolean.FALSE.toString()));
    }
    
    
    /**
     * Tests wheter permits() throws exceptions
     */
    public void testPermitsStringStringFaulty() {
        try {
            this.bpl.permits(Boolean.TRUE.toString(), NON_BOOLEAN_VALUE);
            fail();
        } catch (PrivacyLevelValueException plve) {
        }
    }
    
    
    public void testPermitsStringT() throws PrivacyLevelValueException {
        this.hm.put(APP_1_IDENT, Boolean.TRUE.toString());
        this.hm.put(APP_2_IDENT, Boolean.FALSE.toString());
        
        this.bpl.setValues(this.hm);
        
        assertTrue(this.bpl.permits(APP_1_IDENT, true));
        assertFalse(this.bpl.permits(APP_2_IDENT, true));
        assertFalse(this.bpl.permits(NO_APP_IDENT, true));
    }
    
    
    public void testSetValues() throws PrivacyLevelValueException {
        this.hm.put(APP_1_IDENT, Boolean.TRUE.toString());
        this.hm.put(APP_2_IDENT, Boolean.FALSE.toString());
        
        this.bpl.setValues(this.hm);
        
        assertEquals(Boolean.TRUE, this.bpl.getValue(APP_1_IDENT));
        assertEquals(Boolean.FALSE, this.bpl.getValue(APP_2_IDENT));
        assertNull(this.bpl.getValue(NO_APP_IDENT));
    }
    
    
    /**
     * Tests whether setValues() throws exceptions
     */
    public void testSetValuesFaulty() {
        this.hm.put(APP_1_IDENT, Boolean.TRUE.toString());
        this.hm.put(APP_2_IDENT, NON_BOOLEAN_VALUE);
        
        try {
            this.bpl.setValues(this.hm);
            fail();
        } catch (PrivacyLevelValueException plve) {
        }
    }
    
}