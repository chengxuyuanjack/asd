package com.xlw.page4.model;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import com.xlw.page4.model.Feeling;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table FEELING.
*/
public class FeelingDao extends AbstractDao<Feeling, Long> {

    public static final String TABLENAME = "FEELING";

    /**
     * Properties of entity Feeling.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Content = new Property(1, String.class, "content", false, "CONTENT");
        public final static Property ServerId = new Property(2, Long.class, "serverId", false, "SERVER_ID");
        public final static Property StatusFlag = new Property(3, Integer.class, "statusFlag", false, "STATUS_FLAG");
        public final static Property LocationId = new Property(4, long.class, "locationId", false, "LOCATION_ID");
    };

    private DaoSession daoSession;

    private Query<Feeling> location_FeelingsQuery;

    public FeelingDao(DaoConfig config) {
        super(config);
    }
    
    public FeelingDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'FEELING' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'CONTENT' TEXT," + // 1: content
                "'SERVER_ID' INTEGER," + // 2: serverId
                "'STATUS_FLAG' INTEGER," + // 3: statusFlag
                "'LOCATION_ID' INTEGER NOT NULL );"); // 4: locationId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'FEELING'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Feeling entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(2, content);
        }
 
        Long serverId = entity.getServerId();
        if (serverId != null) {
            stmt.bindLong(3, serverId);
        }
 
        Integer statusFlag = entity.getStatusFlag();
        if (statusFlag != null) {
            stmt.bindLong(4, statusFlag);
        }
        stmt.bindLong(5, entity.getLocationId());
    }

    @Override
    protected void attachEntity(Feeling entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Feeling readEntity(Cursor cursor, int offset) {
        Feeling entity = new Feeling( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // content
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // serverId
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // statusFlag
            cursor.getLong(offset + 4) // locationId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Feeling entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setContent(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setServerId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setStatusFlag(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setLocationId(cursor.getLong(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Feeling entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Feeling entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "feelings" to-many relationship of Location. */
    public List<Feeling> _queryLocation_Feelings(long locationId) {
        synchronized (this) {
            if (location_FeelingsQuery == null) {
                QueryBuilder<Feeling> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.LocationId.eq(null));
                queryBuilder.orderRaw("_id ASC");
                location_FeelingsQuery = queryBuilder.build();
            }
        }
        Query<Feeling> query = location_FeelingsQuery.forCurrentThread();
        query.setParameter(0, locationId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getLocationDao().getAllColumns());
            builder.append(" FROM FEELING T");
            builder.append(" LEFT JOIN LOCATION T0 ON T.'LOCATION_ID'=T0.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Feeling loadCurrentDeep(Cursor cursor, boolean lock) {
        Feeling entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Location location = loadCurrentOther(daoSession.getLocationDao(), cursor, offset);
         if(location != null) {
            entity.setLocation(location);
        }

        return entity;    
    }

    public Feeling loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<Feeling> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Feeling> list = new ArrayList<Feeling>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<Feeling> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Feeling> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
