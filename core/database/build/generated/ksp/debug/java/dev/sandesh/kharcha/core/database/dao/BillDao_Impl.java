package dev.sandesh.kharcha.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import dev.sandesh.kharcha.core.database.entity.BillEntity;
import java.lang.Class;
import java.lang.Double;
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
public final class BillDao_Impl implements BillDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BillEntity> __insertionAdapterOfBillEntity;

  private final EntityDeletionOrUpdateAdapter<BillEntity> __updateAdapterOfBillEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public BillDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBillEntity = new EntityInsertionAdapter<BillEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `bills` (`id`,`name`,`due_date`,`amount`,`is_recurring`,`recurrence_rule`,`linked_account_id`,`is_paid`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BillEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getDueDate());
        if (entity.getAmount() == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.getAmount());
        }
        final int _tmp = entity.isRecurring() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getRecurrenceRule() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getRecurrenceRule());
        }
        if (entity.getLinkedAccountId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getLinkedAccountId());
        }
        final int _tmp_1 = entity.isPaid() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
      }
    };
    this.__updateAdapterOfBillEntity = new EntityDeletionOrUpdateAdapter<BillEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `bills` SET `id` = ?,`name` = ?,`due_date` = ?,`amount` = ?,`is_recurring` = ?,`recurrence_rule` = ?,`linked_account_id` = ?,`is_paid` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BillEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getDueDate());
        if (entity.getAmount() == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.getAmount());
        }
        final int _tmp = entity.isRecurring() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getRecurrenceRule() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getRecurrenceRule());
        }
        if (entity.getLinkedAccountId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getLinkedAccountId());
        }
        final int _tmp_1 = entity.isPaid() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bills WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final BillEntity bill, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBillEntity.insertAndReturnId(bill);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final BillEntity bill, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfBillEntity.handle(bill);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BillEntity>> getAllFlow() {
    final String _sql = "SELECT * FROM bills ORDER BY due_date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bills"}, new Callable<List<BillEntity>>() {
      @Override
      @NonNull
      public List<BillEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfIsRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "is_recurring");
          final int _cursorIndexOfRecurrenceRule = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrence_rule");
          final int _cursorIndexOfLinkedAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_account_id");
          final int _cursorIndexOfIsPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "is_paid");
          final List<BillEntity> _result = new ArrayList<BillEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BillEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Double _tmpAmount;
            if (_cursor.isNull(_cursorIndexOfAmount)) {
              _tmpAmount = null;
            } else {
              _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            }
            final boolean _tmpIsRecurring;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRecurring);
            _tmpIsRecurring = _tmp != 0;
            final String _tmpRecurrenceRule;
            if (_cursor.isNull(_cursorIndexOfRecurrenceRule)) {
              _tmpRecurrenceRule = null;
            } else {
              _tmpRecurrenceRule = _cursor.getString(_cursorIndexOfRecurrenceRule);
            }
            final Long _tmpLinkedAccountId;
            if (_cursor.isNull(_cursorIndexOfLinkedAccountId)) {
              _tmpLinkedAccountId = null;
            } else {
              _tmpLinkedAccountId = _cursor.getLong(_cursorIndexOfLinkedAccountId);
            }
            final boolean _tmpIsPaid;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPaid);
            _tmpIsPaid = _tmp_1 != 0;
            _item = new BillEntity(_tmpId,_tmpName,_tmpDueDate,_tmpAmount,_tmpIsRecurring,_tmpRecurrenceRule,_tmpLinkedAccountId,_tmpIsPaid);
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

  @Override
  public Object getAll(final Continuation<? super List<BillEntity>> $completion) {
    final String _sql = "SELECT * FROM bills ORDER BY due_date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BillEntity>>() {
      @Override
      @NonNull
      public List<BillEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfIsRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "is_recurring");
          final int _cursorIndexOfRecurrenceRule = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrence_rule");
          final int _cursorIndexOfLinkedAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_account_id");
          final int _cursorIndexOfIsPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "is_paid");
          final List<BillEntity> _result = new ArrayList<BillEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BillEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Double _tmpAmount;
            if (_cursor.isNull(_cursorIndexOfAmount)) {
              _tmpAmount = null;
            } else {
              _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            }
            final boolean _tmpIsRecurring;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRecurring);
            _tmpIsRecurring = _tmp != 0;
            final String _tmpRecurrenceRule;
            if (_cursor.isNull(_cursorIndexOfRecurrenceRule)) {
              _tmpRecurrenceRule = null;
            } else {
              _tmpRecurrenceRule = _cursor.getString(_cursorIndexOfRecurrenceRule);
            }
            final Long _tmpLinkedAccountId;
            if (_cursor.isNull(_cursorIndexOfLinkedAccountId)) {
              _tmpLinkedAccountId = null;
            } else {
              _tmpLinkedAccountId = _cursor.getLong(_cursorIndexOfLinkedAccountId);
            }
            final boolean _tmpIsPaid;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPaid);
            _tmpIsPaid = _tmp_1 != 0;
            _item = new BillEntity(_tmpId,_tmpName,_tmpDueDate,_tmpAmount,_tmpIsRecurring,_tmpRecurrenceRule,_tmpLinkedAccountId,_tmpIsPaid);
            _result.add(_item);
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
  public Object getUpcoming(final long now, final long cutoff,
      final Continuation<? super List<BillEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM bills \n"
            + "        WHERE due_date BETWEEN ? AND ?\n"
            + "        ORDER BY due_date ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, now);
    _argIndex = 2;
    _statement.bindLong(_argIndex, cutoff);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BillEntity>>() {
      @Override
      @NonNull
      public List<BillEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfIsRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "is_recurring");
          final int _cursorIndexOfRecurrenceRule = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrence_rule");
          final int _cursorIndexOfLinkedAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_account_id");
          final int _cursorIndexOfIsPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "is_paid");
          final List<BillEntity> _result = new ArrayList<BillEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BillEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpDueDate;
            _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            final Double _tmpAmount;
            if (_cursor.isNull(_cursorIndexOfAmount)) {
              _tmpAmount = null;
            } else {
              _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            }
            final boolean _tmpIsRecurring;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRecurring);
            _tmpIsRecurring = _tmp != 0;
            final String _tmpRecurrenceRule;
            if (_cursor.isNull(_cursorIndexOfRecurrenceRule)) {
              _tmpRecurrenceRule = null;
            } else {
              _tmpRecurrenceRule = _cursor.getString(_cursorIndexOfRecurrenceRule);
            }
            final Long _tmpLinkedAccountId;
            if (_cursor.isNull(_cursorIndexOfLinkedAccountId)) {
              _tmpLinkedAccountId = null;
            } else {
              _tmpLinkedAccountId = _cursor.getLong(_cursorIndexOfLinkedAccountId);
            }
            final boolean _tmpIsPaid;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPaid);
            _tmpIsPaid = _tmp_1 != 0;
            _item = new BillEntity(_tmpId,_tmpName,_tmpDueDate,_tmpAmount,_tmpIsRecurring,_tmpRecurrenceRule,_tmpLinkedAccountId,_tmpIsPaid);
            _result.add(_item);
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
