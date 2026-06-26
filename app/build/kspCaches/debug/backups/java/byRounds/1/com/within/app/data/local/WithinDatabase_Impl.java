package com.within.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.within.app.data.local.dao.DayProgressDao;
import com.within.app.data.local.dao.DayProgressDao_Impl;
import com.within.app.data.local.dao.JournalEntryDao;
import com.within.app.data.local.dao.JournalEntryDao_Impl;
import com.within.app.data.local.dao.JourneyProgressDao;
import com.within.app.data.local.dao.JourneyProgressDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class WithinDatabase_Impl extends WithinDatabase {
  private volatile JournalEntryDao _journalEntryDao;

  private volatile JourneyProgressDao _journeyProgressDao;

  private volatile DayProgressDao _dayProgressDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `journal_entries` (`journeyId` TEXT NOT NULL, `day` INTEGER NOT NULL, `field` TEXT NOT NULL, `text` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`journeyId`, `day`, `field`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `journey_progress` (`journeyId` TEXT NOT NULL, `startedEpochDay` INTEGER NOT NULL, `status` TEXT NOT NULL, PRIMARY KEY(`journeyId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `day_progress` (`journeyId` TEXT NOT NULL, `day` INTEGER NOT NULL, `completed` INTEGER NOT NULL, `mindfulActionDone` INTEGER NOT NULL, `completedAt` INTEGER, PRIMARY KEY(`journeyId`, `day`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '29bf7dcec548b090700f5a263d5216c2')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `journal_entries`");
        db.execSQL("DROP TABLE IF EXISTS `journey_progress`");
        db.execSQL("DROP TABLE IF EXISTS `day_progress`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsJournalEntries = new HashMap<String, TableInfo.Column>(5);
        _columnsJournalEntries.put("journeyId", new TableInfo.Column("journeyId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("day", new TableInfo.Column("day", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("field", new TableInfo.Column("field", "TEXT", true, 3, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("text", new TableInfo.Column("text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysJournalEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesJournalEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoJournalEntries = new TableInfo("journal_entries", _columnsJournalEntries, _foreignKeysJournalEntries, _indicesJournalEntries);
        final TableInfo _existingJournalEntries = TableInfo.read(db, "journal_entries");
        if (!_infoJournalEntries.equals(_existingJournalEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "journal_entries(com.within.app.data.local.entity.JournalEntry).\n"
                  + " Expected:\n" + _infoJournalEntries + "\n"
                  + " Found:\n" + _existingJournalEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsJourneyProgress = new HashMap<String, TableInfo.Column>(3);
        _columnsJourneyProgress.put("journeyId", new TableInfo.Column("journeyId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJourneyProgress.put("startedEpochDay", new TableInfo.Column("startedEpochDay", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJourneyProgress.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysJourneyProgress = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesJourneyProgress = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoJourneyProgress = new TableInfo("journey_progress", _columnsJourneyProgress, _foreignKeysJourneyProgress, _indicesJourneyProgress);
        final TableInfo _existingJourneyProgress = TableInfo.read(db, "journey_progress");
        if (!_infoJourneyProgress.equals(_existingJourneyProgress)) {
          return new RoomOpenHelper.ValidationResult(false, "journey_progress(com.within.app.data.local.entity.JourneyProgress).\n"
                  + " Expected:\n" + _infoJourneyProgress + "\n"
                  + " Found:\n" + _existingJourneyProgress);
        }
        final HashMap<String, TableInfo.Column> _columnsDayProgress = new HashMap<String, TableInfo.Column>(5);
        _columnsDayProgress.put("journeyId", new TableInfo.Column("journeyId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayProgress.put("day", new TableInfo.Column("day", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayProgress.put("completed", new TableInfo.Column("completed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayProgress.put("mindfulActionDone", new TableInfo.Column("mindfulActionDone", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayProgress.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDayProgress = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDayProgress = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDayProgress = new TableInfo("day_progress", _columnsDayProgress, _foreignKeysDayProgress, _indicesDayProgress);
        final TableInfo _existingDayProgress = TableInfo.read(db, "day_progress");
        if (!_infoDayProgress.equals(_existingDayProgress)) {
          return new RoomOpenHelper.ValidationResult(false, "day_progress(com.within.app.data.local.entity.DayProgress).\n"
                  + " Expected:\n" + _infoDayProgress + "\n"
                  + " Found:\n" + _existingDayProgress);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "29bf7dcec548b090700f5a263d5216c2", "670f9ac2d10eaf9da1bf797019a3eb49");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "journal_entries","journey_progress","day_progress");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `journal_entries`");
      _db.execSQL("DELETE FROM `journey_progress`");
      _db.execSQL("DELETE FROM `day_progress`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(JournalEntryDao.class, JournalEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(JourneyProgressDao.class, JourneyProgressDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DayProgressDao.class, DayProgressDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public JournalEntryDao journalEntryDao() {
    if (_journalEntryDao != null) {
      return _journalEntryDao;
    } else {
      synchronized(this) {
        if(_journalEntryDao == null) {
          _journalEntryDao = new JournalEntryDao_Impl(this);
        }
        return _journalEntryDao;
      }
    }
  }

  @Override
  public JourneyProgressDao journeyProgressDao() {
    if (_journeyProgressDao != null) {
      return _journeyProgressDao;
    } else {
      synchronized(this) {
        if(_journeyProgressDao == null) {
          _journeyProgressDao = new JourneyProgressDao_Impl(this);
        }
        return _journeyProgressDao;
      }
    }
  }

  @Override
  public DayProgressDao dayProgressDao() {
    if (_dayProgressDao != null) {
      return _dayProgressDao;
    } else {
      synchronized(this) {
        if(_dayProgressDao == null) {
          _dayProgressDao = new DayProgressDao_Impl(this);
        }
        return _dayProgressDao;
      }
    }
  }
}
