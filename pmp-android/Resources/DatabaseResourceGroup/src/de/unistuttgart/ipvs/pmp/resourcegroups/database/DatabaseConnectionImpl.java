package de.unistuttgart.ipvs.pmp.resourcegroups.database;

import java.util.Map;
import java.util.Set;

import de.unistuttgart.ipvs.pmp.resource.Resource;
import de.unistuttgart.ipvs.pmp.resource.privacylevel.BooleanPrivacyLevel;
import de.unistuttgart.ipvs.pmp.resourcegroups.database.IDatabaseConnection;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.RemoteException;

public class DatabaseConnectionImpl extends IDatabaseConnection.Stub {

    private String name;
    private int version;
    private String appID;
    private String exceptionMessage;

    private Context context;
    private DatabaseResource resource;
    private DatabaseOpenHelper helper;
    private SQLiteDatabase db;

    private boolean read;
    private boolean modify;
    private boolean create;
    private Cursor cursor;

    public DatabaseConnectionImpl(Context context, DatabaseResource resource,
	    String appIdentifier) {
	this.context = context;
	this.resource = resource;
	appID = appIdentifier;
	name = appIdentifier; // TODO Allow access to other databases
	exceptionMessage = context.getResources().getString(
		R.string.unauthorized_action_exception);
	// Set the Privacy Levels
	updatePrivacyLevel();
    }

    private void updatePrivacyLevel() {
	read = BooleanPrivacyLevel.valueOf(resource.getPrivacyLevelValue(appID,
		DatabaseResourceGroup.PRIVACY_LEVEL_READ));
	modify = BooleanPrivacyLevel.valueOf(resource.getPrivacyLevelValue(
		appID, DatabaseResourceGroup.PRIVACY_LEVEL_MODIFY));
	create = BooleanPrivacyLevel.valueOf(resource.getPrivacyLevelValue(
		appID, DatabaseResourceGroup.PRIVACY_LEVEL_CREATE));

	// Open a database connection
	if (helper == null) {
	    // TODO Feature: open a other databases
	    helper = new DatabaseOpenHelper(context, name, version);
	}
	if (read && !modify && !create) {
	    db = helper.getReadableDatabase();
	} else if (modify || create) {
	    db = helper.getWritableDatabase();
	} else {
	    db = null;
	}
	if (cursor != null) {
	    cursor.close();
	    cursor = null;
	}
    }

    private ContentValues getContentValues(Map values) {
	if (values == null) {
	    return null;
	}
	ContentValues cv = new ContentValues();
	Set keys = values.keySet();
	for (Object key : keys) {
	    cv.put(key.toString(), values.get(key).toString());
	}
	return cv;
    }

    @Override
    public boolean createTable(String tableName, Map columns)
	    throws RemoteException {

//	if (create) {
//	    if ("".equals(tableName) || columns == null) {
//		return false;
//	    }
//	    if (columns.size() == 0) {
//		return false;
//	    }
//	    Collection 
//	    for (int i = 0; i < columns.size(); i++) {
//
//	    }
//	}
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public long insert(String table, String nullColumnHack, Map values)
	    throws RemoteException {
	if (modify) {

	    return db.insert(table, nullColumnHack, getContentValues(values));
	} else {
	    RemoteException ex = new RemoteException();
	    ex.setStackTrace((new UnauthorizedActionException(exceptionMessage))
		    .getStackTrace()); // TODO Fix needed?
	    throw ex;
	}
    }

    @Override
    public int update(String table, Map values, String whereClause,
	    String[] whereArgs) throws RemoteException {
	if (modify) {
	    return db.update(table, getContentValues(values), whereClause,
		    whereArgs);
	} else {
	    RemoteException ex = new RemoteException();
	    ex.setStackTrace((new UnauthorizedActionException(exceptionMessage))
		    .getStackTrace()); // TODO Fix needed?
	    throw ex;
	}
    }

    @Override
    public int delete(String table, String whereClause, String[] whereArgs)
	    throws RemoteException {
	if (modify) {
	    return db.delete(table, whereClause, whereArgs);
	} else {
	    RemoteException ex = new RemoteException();
	    ex.setStackTrace((new UnauthorizedActionException(exceptionMessage))
		    .getStackTrace()); // TODO Fix needed?
	    throw ex;
	}
    }

    @Override
    public long queryWithLimit(String table, String[] columns,
	    String selection, String[] selectionArgs, String groupBy,
	    String having, String orderBy, String limit) throws RemoteException {
	if (read) {
	    cursor = db.query(table, columns, selection, selectionArgs,
		    groupBy, having, orderBy, limit);
	    return cursor.getCount();
	} else {
	    RemoteException ex = new RemoteException();
	    ex.setStackTrace((new UnauthorizedActionException(exceptionMessage))
		    .getStackTrace()); // TODO Fix needed?
	    throw ex;
	}
    }

    @Override
    public long query(String table, String[] columns, String selection,
	    String[] selectionArgs, String groupBy, String having,
	    String orderBy) throws RemoteException {
	if (read) {
	    cursor = db.query(table, columns, selection, selectionArgs,
		    groupBy, having, orderBy);
	    cursor.moveToFirst();
	    return cursor.getCount();
	} else {
	    RemoteException ex = new RemoteException();
	    ex.setStackTrace((new UnauthorizedActionException(exceptionMessage))
		    .getStackTrace()); // TODO Fix needed?
	    throw ex;
	}
    }

    @Override
    public long getRowPosition() throws RemoteException {
	if (cursor != null) {
	    return cursor.getPosition();
	} else
	    return -1;
    }

    @Override
    public String getString(int column) throws RemoteException {
	if (cursor == null) {
	    RemoteException ex = new RemoteException();
	    ex.setStackTrace((new UnauthorizedActionException(exceptionMessage))
		    .getStackTrace()); // TODO Fix needed?
	    throw ex;
	} else if (cursor.getColumnCount() <= column) {
	    RemoteException ex = new RemoteException();
	    ex.setStackTrace((new IllegalAccessException()).getStackTrace());
	    throw ex;
	} else {
	    return cursor.getString(column);
	}
    }

    @Override
    public int getInteger(int column) throws RemoteException {
	if (cursor == null) {
	    RemoteException ex = new RemoteException();
	    ex.setStackTrace((new UnauthorizedActionException(exceptionMessage))
		    .getStackTrace()); // TODO Fix needed?
	    throw ex;
	} else if (cursor.getColumnCount() <= column) {
	    RemoteException ex = new RemoteException();
	    ex.setStackTrace((new IllegalAccessException()).getStackTrace());
	    throw ex;
	} else {
	    return (cursor.getInt(column));
	}
    }

    @Override
    public double getDouble(int column) throws RemoteException {
	if (cursor == null) {
	    RemoteException ex = new RemoteException();
	    ex.setStackTrace((new UnauthorizedActionException(exceptionMessage))
		    .getStackTrace()); // TODO Fix needed?
	    throw ex;
	} else if (cursor.getColumnCount() <= column) {
	    RemoteException ex = new RemoteException();
	    ex.setStackTrace((new IllegalAccessException()).getStackTrace());
	    throw ex;
	} else {
	    return (cursor.getDouble(column));
	}
    }

    @Override
    public String[] getRowAt(int position) throws RemoteException {
	if (cursor == null) {
	    RemoteException ex = new RemoteException();
	    ex.setStackTrace((new UnauthorizedActionException(exceptionMessage))
		    .getStackTrace()); // TODO Fix needed?
	    throw ex;
	} else if ((cursor.getCount() <= position) || (position < 0)) {
	    return null;
	} else {
	    cursor.moveToPosition(position);
	    String[] row = new String[cursor.getColumnCount()];
	    for (int i = 0; i < cursor.getColumnCount(); i++) {
		row[i] = cursor.getString(i);
	    }
	    return row;
	}
    }

    @Override
    public String[] getCurrentRow() throws RemoteException {
	if (cursor == null) {
	    RemoteException ex = new RemoteException();
	    ex.setStackTrace((new UnauthorizedActionException(exceptionMessage))
		    .getStackTrace()); // TODO Fix needed?
	    throw ex;
	} else if (cursor.isAfterLast() || cursor.isBeforeFirst()) {
	    return null;
	} else {
	    String[] row = new String[cursor.getColumnCount()];
	    for (int i = 0; i < cursor.getColumnCount(); i++) {
		row[i] = cursor.getString(i);
	    }
	    return row;
	}
    }

    @Override
    public String[] getRowAndNext() throws RemoteException {
	if (cursor == null) {
	    RemoteException ex = new RemoteException();
	    ex.setStackTrace((new UnauthorizedActionException(exceptionMessage))
		    .getStackTrace()); // TODO Fix needed?
	    throw ex;
	} else if (cursor.isAfterLast() || cursor.isBeforeFirst()) {
	    return null;
	} else {
	    String[] row = new String[cursor.getColumnCount()];
	    for (int i = 0; i < cursor.getColumnCount(); i++) {
		row[i] = cursor.getString(i);
	    }
	    cursor.moveToNext();
	    return row;
	}

    }

    @Override
    public boolean next() throws RemoteException {
	if (cursor == null) {
	    return false;
	} else {
	    return cursor.moveToNext();
	}
    }

    @Override
    public boolean goToRowPosition(int position) throws RemoteException {
	if (cursor == null) {
	    return false;
	} else {
	    return cursor.moveToPosition(position);
	}
    }
}