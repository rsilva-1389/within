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
import com.within.app.data.local.entity.DayProgress;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DayProgressDao_Impl implements DayProgressDao {
  private final RoomDatabase __db;

  private final EntityUpsertionAdapter<DayProgress> __upsertionAdapterOfDayProgress;

  public DayProgressDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__upsertionAdapterOfDayProgress = new EntityUpsertionAdapter<DayProgress>(new EntityInsertionAdapter<DayProgress>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `day_progress` (`journeyId`,`day`,`completed`,`mindfulActionDone`,`completedAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DayProgress entity) {
        statement.bindString(1, entity.getJourneyId());
        statement.bindLong(2, entity.getDay());
        final int _tmp = entity.getCompleted() ? 1 : 0;
        statement.bindLong(3, _tmp);
        final int _tmp_1 = entity.getMindfulActionDone() ? 1 : 0;
        statement.bindLong(4, _tmp_1);
        if (entity.getCompletedAt() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getCompletedAt());
        }
      }
    }, new EntityDeletionOrUpdateAdapter<DayProgress>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `day_progress` SET `journeyId` = ?,`day` = ?,`completed` = ?,`mindfulActionDone` = ?,`completedAt` = ? WHERE `journeyId` = ? AND `day` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DayProgress entity) {
        statement.bindString(1, entity.getJourneyId());
        statement.bindLong(2, entity.getDay());
        final int _tmp = entity.getCompleted() ? 1 : 0;
        statement.bindLong(3, _tmp);
        final int _tmp_1 = entity.getMindfulActionDone() ? 1 : 0;
        statement.bindLong(4, _tmp_1);
        if (entity.getCompletedAt() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getCompletedAt());
        }
        statement.bindString(6, entity.getJourneyId());
        statement.bindLong(7, entity.getDay());
      }
    });
  }

  @Override
  public Object upsert(final DayProgress dayProgress,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfDayProgress.upsert(dayProgress);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<DayProgress> dayFlow(final String journeyId, final int day) {
    final String _sql = "SELECT * FROM day_progress WHERE journeyId = ? AND day = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, journeyId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, day);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"day_progress"}, new Callable<DayProgress>() {
      @Override
      @Nullable
      public DayProgress call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfJourneyId = CursorUtil.getColumnIndexOrThrow(_cursor, "journeyId");
          final int _cursorIndexOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "day");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final int _cursorIndexOfMindfulActionDone = CursorUtil.getColumnIndexOrThrow(_cursor, "mindfulActionDone");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final DayProgress _result;
          if (_cursor.moveToFirst()) {
            final String _tmpJourneyId;
            _tmpJourneyId = _cursor.getString(_cursorIndexOfJourneyId);
            final int _tmpDay;
            _tmpDay = _cursor.getInt(_cursorIndexOfDay);
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            final boolean _tmpMindfulActionDone;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfMindfulActionDone);
            _tmpMindfulActionDone = _tmp_1 != 0;
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _result = new DayProgress(_tmpJourneyId,_tmpDay,_tmpCompleted,_tmpMindfulActionDone,_tmpCompletedAt);
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
  public Object dayOnce(final String journeyId, final int day,
      final Continuation<? super DayProgress> $completion) {
    final String _sql = "SELECT * FROM day_progress WHERE journeyId = ? AND day = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, journeyId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, day);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DayProgress>() {
      @Override
      @Nullable
      public DayProgress call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfJourneyId = CursorUtil.getColumnIndexOrThrow(_cursor, "journeyId");
          final int _cursorIndexOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "day");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final int _cursorIndexOfMindfulActionDone = CursorUtil.getColumnIndexOrThrow(_cursor, "mindfulActionDone");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final DayProgress _result;
          if (_cursor.moveToFirst()) {
            final String _tmpJourneyId;
            _tmpJourneyId = _cursor.getString(_cursorIndexOfJourneyId);
            final int _tmpDay;
            _tmpDay = _cursor.getInt(_cursorIndexOfDay);
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            final boolean _tmpMindfulActionDone;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfMindfulActionDone);
            _tmpMindfulActionDone = _tmp_1 != 0;
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _result = new DayProgress(_tmpJourneyId,_tmpDay,_tmpCompleted,_tmpMindfulActionDone,_tmpCompletedAt);
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

  @Override
  public Flow<List<DayProgress>> daysFor(final String journeyId) {
    final String _sql = "SELECT * FROM day_progress WHERE journeyId = ? ORDER BY day";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, journeyId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"day_progress"}, new Callable<List<DayProgress>>() {
      @Override
      @NonNull
      public List<DayProgress> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfJourneyId = CursorUtil.getColumnIndexOrThrow(_cursor, "journeyId");
          final int _cursorIndexOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "day");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final int _cursorIndexOfMindfulActionDone = CursorUtil.getColumnIndexOrThrow(_cursor, "mindfulActionDone");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final List<DayProgress> _result = new ArrayList<DayProgress>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DayProgress _item;
            final String _tmpJourneyId;
            _tmpJourneyId = _cursor.getString(_cursorIndexOfJourneyId);
            final int _tmpDay;
            _tmpDay = _cursor.getInt(_cursorIndexOfDay);
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            final boolean _tmpMindfulActionDone;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfMindfulActionDone);
            _tmpMindfulActionDone = _tmp_1 != 0;
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _item = new DayProgress(_tmpJourneyId,_tmpDay,_tmpCompleted,_tmpMindfulActionDone,_tmpCompletedAt);
            _result.add(_item);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
