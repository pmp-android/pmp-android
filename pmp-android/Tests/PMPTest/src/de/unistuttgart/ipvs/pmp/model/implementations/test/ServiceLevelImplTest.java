package de.unistuttgart.ipvs.pmp.model.implementations.test;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import de.unistuttgart.ipvs.pmp.model.DatabaseOpenHelper;
import de.unistuttgart.ipvs.pmp.model.DatabaseSingleton;
import de.unistuttgart.ipvs.pmp.model.ModelSingleton;
import de.unistuttgart.ipvs.pmp.model.interfaces.IServiceLevel;

public class ServiceLevelImplTest extends AndroidTestCase {
    
    private static final String TEST_APP1_IDENT = "TEST_APP1";
    private static final String TEST_APP1_NAME = "TEST_APP1_NAME";
    private static final String TEST_APP1_DESCR = "TEST_APP1_DESCR";
    
    private static final String TEST_RG1_IDENT = "TEST_RG1";
    private static final String TEST_RG1_NAME = "TEST_RG1_NAME";
    private static final String TEST_RG1_DESC = "TEST_RG1_DESC";
    
    private static final String TEST_APP1_SL0_NAME = "TEST_APP1_SL0_NAME";
    private static final String TEST_APP1_SL0_DESC = "TEST_APP1_SL0_DESC";
    private static final String TEST_PL1_IDENT = "TEST_PL1";
    private static final String TEST_PL1_NAME = "TEST_PL1_NAME";
    private static final String TEST_PL1_DESC = "TEST_PL1_DESC";
    private static final String TEST_PL1_VALUE = "TEST_PL1_VALUE";
    
    IServiceLevel slevel = null;
    
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        /*
         * Filling the DB with Apps
         */
        DatabaseOpenHelper doh = DatabaseSingleton.getInstance().getDatabaseHelper();
        doh.cleanTables();
        SQLiteDatabase DB = doh.getWritableDatabase();
        
        DB.execSQL("INSERT INTO \"App\" VALUES(?, ?, ?, 0);", new String[] { TEST_APP1_IDENT, TEST_APP1_NAME,
                TEST_APP1_DESCR });
        
        DB.execSQL("INSERT INTO \"ResourceGroup\" VALUES(?, ?, ?);", new String[] { TEST_RG1_IDENT, TEST_RG1_NAME,
                TEST_RG1_DESC });
        
        DB.execSQL("INSERT INTO \"ServiceLevel\" VALUES(?, 0, ?, ?);", new String[] { TEST_APP1_IDENT,
                TEST_APP1_SL0_NAME, TEST_APP1_SL0_DESC });
        
        DB.execSQL("INSERT INTO \"PrivacyLevel\" VALUES(?, ?, ?, ?);", new String[] { TEST_RG1_IDENT, TEST_PL1_IDENT,
                TEST_PL1_NAME, TEST_PL1_DESC });
        
        DB.execSQL("INSERT INTO \"ServiceLevel_PrivacyLevels\" VALUES(?, 0, ?, ?, ?);", new String[] { TEST_APP1_IDENT,
                TEST_RG1_IDENT, TEST_PL1_IDENT, TEST_PL1_VALUE });
    }
    
    
    public void testGetDescription() {
        assertNull(this.slevel);
        this.slevel = ModelSingleton.getInstance().getModel().getApp(TEST_APP1_IDENT).getServiceLevel(0);
        assertNotNull(this.slevel);
        assertEquals(TEST_APP1_SL0_DESC, this.slevel.getDescription());
    }
    
    
    public void testGetLevel() {
        assertNull(this.slevel);
        this.slevel = ModelSingleton.getInstance().getModel().getApp(TEST_APP1_IDENT).getServiceLevel(0);
        assertNotNull(this.slevel);
        assertEquals(0, this.slevel.getLevel());
    }
    
    
    public void testGetName() {
        assertNull(this.slevel);
        this.slevel = ModelSingleton.getInstance().getModel().getApp(TEST_APP1_IDENT).getServiceLevel(0);
        assertNotNull(this.slevel);
        assertEquals(TEST_APP1_SL0_NAME, this.slevel.getName());
    }
    
    
    public void testGetPrivacyLevels() {
        assertNull(this.slevel);
        this.slevel = ModelSingleton.getInstance().getModel().getApp(TEST_APP1_IDENT).getServiceLevel(0);
        assertNotNull(this.slevel);
        assertEquals(1, this.slevel.getPrivacyLevels().length);
        assertEquals(TEST_PL1_IDENT, this.slevel.getPrivacyLevels()[0].getIdentifier());
    }
    
    
    public void testIsAvailable() {
        assertNull(this.slevel);
        this.slevel = ModelSingleton.getInstance().getModel().getApp(TEST_APP1_IDENT).getServiceLevel(0);
        assertNotNull(this.slevel);
        assertEquals(true, this.slevel.isAvailable());
    }
}