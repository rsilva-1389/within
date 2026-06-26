package com.within.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.within.app.data.local.entity.JourneyProgress;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class JourneyProgressDao_Impl implements JourneyProgressDao {
  private final RoomDatabase __db;

  private final EntityUpsertionAdapter<JourneyProgress> __upsertionAdapterOfJourneyProgress;

  public JourneyProgressDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__upsertionAdapterOfJourneyProgress = new EntityUpsertionAdapter<JourneyProgress>(new EntityInsertionAdapter<JourneyProgress>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `journey_progress` (`journeyId`,`startedEpochDay`,`status`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final JourneyProgress entity) {
        statement.bindString(1, entity.getJourneyId());
        statement.bindLong(2, entity.getStartedEpochDay());
        statement.bindString(3, entity.getStatus());
      }
    }, new EntityDeletionOrUpdateAdapter<JourneyProgress>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `journey_progress` SET `journeyId` = ?,`startedEpochDay` = ?,`status` = ? WHERE `journeyId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final JourneyProgress entity) {
        statement.bindString(1, entity.getJourneyId());
        statement.bindLong(2, entity.getStartedEpochDay());
        statement.bindString(3, entity.getStatus());
        statement.bindString(4, entity.getJourneyId());
      }
    });
  }

  @Override
  public Object upsert(final JourneyProgress progress,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfJourneyProgress.upsert(progress);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<JourneyProgress> progress(final String journeyId) {
    final String _sql = "SELECT * FROM journey_progress WHERE journeyId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, journeyId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journey_progress"}, new Callable<JourneyProgress>() {
      @Override
      @Nullable
      public JourneyProgress call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfJourneyId = CursorUtil.getColumnIndexOrThrow(_cursor, "journeyId");
          final int _cursorIndexOfStartedEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "startedEpochDay");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final JourneyProgress _result;
          if (_cursor.moveToFirst()) {
            final String _tmpJourneyId;
            _tmpJourneyId = _cursor.getString(_cursorIndexOfJourneyId);
            final long _tmpStartedEpochDay;
            _tmpStartedEpochDay = _cursor.getLong(_cursorIndexOfStartedEpochDay);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _result = new JourneyProgress(_tmpJourneyId,_tmpStartedEpochDay,_tmpStatus);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object progressOnce(final String journeyId,
      final Continuation<? super JourneyProgress> $completion) {
    final String _sql = "SELECT * FROM journey_progress WHERE journeyId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, journeyId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<JourneyProgress>() {
      @Override
      @Nullable
      public JourneyProgress call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfJourneyId = CursorUtil.getColumnIndexOrThrow(_cursor, "journeyId");
          final int _cursorIndexOfStartedEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "startedEpochDay");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final JourneyProgress _result;
          if (_cursor.moveToFirst()) {
            final String _tmpJourneyId;
            _tmpJourneyId = _cursor.getString(_cursorIndexOfJourneyId);
            final long _tmpStartedEpochDay;
            _tmpStartedEpochDay = _cursor.getLong(_cursorIndexOfStartedEpochDay);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _result = new JourneyProgress(_tmpJourneyId,_tmpStartedEpochDay,_tmpStatus);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
