package dev.sandesh.kharcha.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import dev.sandesh.kharcha.core.database.Converters;
import dev.sandesh.kharcha.core.database.entity.TransactionEntity;
import dev.sandesh.kharcha.core.model.TransactionSource;
import dev.sandesh.kharcha.core.model.TransactionStatus;
import dev.sandesh.kharcha.core.model.TransactionType;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
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
public final class TransactionDao_Impl implements TransactionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TransactionEntity> __insertionAdapterOfTransactionEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<TransactionEntity> __updateAdapterOfTransactionEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfPurgeRawSmsText;

  private final SharedSQLiteStatement __preparedStmtOfPurgeRawSmsForConfirmedTransactions;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public TransactionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTransactionEntity = new EntityInsertionAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `transactions` (`id`,`account_id`,`amount`,`type`,`merchant_raw`,`merchant_normalized`,`category_id`,`txn_ref`,`timestamp`,`source`,`parse_confidence`,`status`,`is_duplicate_of`,`raw_sms_text`,`note`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAccountId());
        statement.bindDouble(3, entity.getAmount());
        final String _tmp = __converters.fromTransactionType(entity.getType());
        statement.bindString(4, _tmp);
        statement.bindString(5, entity.getMerchantRaw());
        statement.bindString(6, entity.getMerchantNormalized());
        statement.bindLong(7, entity.getCategoryId());
        statement.bindString(8, entity.getTxnRef());
        statement.bindLong(9, entity.getTimestamp());
        final String _tmp_1 = __converters.fromTransactionSource(entity.getSource());
        statement.bindString(10, _tmp_1);
        statement.bindDouble(11, entity.getParseConfidence());
        final String _tmp_2 = __converters.fromTransactionStatus(entity.getStatus());
        statement.bindString(12, _tmp_2);
        if (entity.isDuplicateOf() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.isDuplicateOf());
        }
        if (entity.getRawSmsText() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getRawSmsText());
        }
        statement.bindString(15, entity.getNote());
      }
    };
    this.__updateAdapterOfTransactionEntity = new EntityDeletionOrUpdateAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `transactions` SET `id` = ?,`account_id` = ?,`amount` = ?,`type` = ?,`merchant_raw` = ?,`merchant_normalized` = ?,`category_id` = ?,`txn_ref` = ?,`timestamp` = ?,`source` = ?,`parse_confidence` = ?,`status` = ?,`is_duplicate_of` = ?,`raw_sms_text` = ?,`note` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAccountId());
        statement.bindDouble(3, entity.getAmount());
        final String _tmp = __converters.fromTransactionType(entity.getType());
        statement.bindString(4, _tmp);
        statement.bindString(5, entity.getMerchantRaw());
        statement.bindString(6, entity.getMerchantNormalized());
        statement.bindLong(7, entity.getCategoryId());
        statement.bindString(8, entity.getTxnRef());
        statement.bindLong(9, entity.getTimestamp());
        final String _tmp_1 = __converters.fromTransactionSource(entity.getSource());
        statement.bindString(10, _tmp_1);
        statement.bindDouble(11, entity.getParseConfidence());
        final String _tmp_2 = __converters.fromTransactionStatus(entity.getStatus());
        statement.bindString(12, _tmp_2);
        if (entity.isDuplicateOf() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.isDuplicateOf());
        }
        if (entity.getRawSmsText() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getRawSmsText());
        }
        statement.bindString(15, entity.getNote());
        statement.bindLong(16, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM transactions WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfPurgeRawSmsText = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE transactions SET raw_sms_text = NULL WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfPurgeRawSmsForConfirmedTransactions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE transactions SET raw_sms_text = NULL WHERE status = 'CONFIRMED' AND raw_sms_text IS NOT NULL";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM transactions";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final TransactionEntity transaction,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTransactionEntity.insertAndReturnId(transaction);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final TransactionEntity transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTransactionEntity.handle(transaction);
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
  public Object purgeRawSmsText(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfPurgeRawSmsText.acquire();
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
          __preparedStmtOfPurgeRawSmsText.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object purgeRawSmsForConfirmedTransactions(
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfPurgeRawSmsForConfirmedTransactions.acquire();
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfPurgeRawSmsForConfirmedTransactions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
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
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id, final Continuation<? super TransactionEntity> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TransactionEntity>() {
      @Override
      @Nullable
      public TransactionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_raw");
          final int _cursorIndexOfMerchantNormalized = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_normalized");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfTxnRef = CursorUtil.getColumnIndexOrThrow(_cursor, "txn_ref");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfParseConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "parse_confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsDuplicateOf = CursorUtil.getColumnIndexOrThrow(_cursor, "is_duplicate_of");
          final int _cursorIndexOfRawSmsText = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_sms_text");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final TransactionEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final TransactionType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toTransactionType(_tmp);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantNormalized;
            _tmpMerchantNormalized = _cursor.getString(_cursorIndexOfMerchantNormalized);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final String _tmpTxnRef;
            _tmpTxnRef = _cursor.getString(_cursorIndexOfTxnRef);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final TransactionSource _tmpSource;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toTransactionSource(_tmp_1);
            final float _tmpParseConfidence;
            _tmpParseConfidence = _cursor.getFloat(_cursorIndexOfParseConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTransactionStatus(_tmp_2);
            final Long _tmpIsDuplicateOf;
            if (_cursor.isNull(_cursorIndexOfIsDuplicateOf)) {
              _tmpIsDuplicateOf = null;
            } else {
              _tmpIsDuplicateOf = _cursor.getLong(_cursorIndexOfIsDuplicateOf);
            }
            final String _tmpRawSmsText;
            if (_cursor.isNull(_cursorIndexOfRawSmsText)) {
              _tmpRawSmsText = null;
            } else {
              _tmpRawSmsText = _cursor.getString(_cursorIndexOfRawSmsText);
            }
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _result = new TransactionEntity(_tmpId,_tmpAccountId,_tmpAmount,_tmpType,_tmpMerchantRaw,_tmpMerchantNormalized,_tmpCategoryId,_tmpTxnRef,_tmpTimestamp,_tmpSource,_tmpParseConfidence,_tmpStatus,_tmpIsDuplicateOf,_tmpRawSmsText,_tmpNote);
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
  public Flow<List<TransactionEntity>> getAllFlow() {
    final String _sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_raw");
          final int _cursorIndexOfMerchantNormalized = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_normalized");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfTxnRef = CursorUtil.getColumnIndexOrThrow(_cursor, "txn_ref");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfParseConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "parse_confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsDuplicateOf = CursorUtil.getColumnIndexOrThrow(_cursor, "is_duplicate_of");
          final int _cursorIndexOfRawSmsText = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_sms_text");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final TransactionType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toTransactionType(_tmp);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantNormalized;
            _tmpMerchantNormalized = _cursor.getString(_cursorIndexOfMerchantNormalized);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final String _tmpTxnRef;
            _tmpTxnRef = _cursor.getString(_cursorIndexOfTxnRef);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final TransactionSource _tmpSource;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toTransactionSource(_tmp_1);
            final float _tmpParseConfidence;
            _tmpParseConfidence = _cursor.getFloat(_cursorIndexOfParseConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTransactionStatus(_tmp_2);
            final Long _tmpIsDuplicateOf;
            if (_cursor.isNull(_cursorIndexOfIsDuplicateOf)) {
              _tmpIsDuplicateOf = null;
            } else {
              _tmpIsDuplicateOf = _cursor.getLong(_cursorIndexOfIsDuplicateOf);
            }
            final String _tmpRawSmsText;
            if (_cursor.isNull(_cursorIndexOfRawSmsText)) {
              _tmpRawSmsText = null;
            } else {
              _tmpRawSmsText = _cursor.getString(_cursorIndexOfRawSmsText);
            }
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpAmount,_tmpType,_tmpMerchantRaw,_tmpMerchantNormalized,_tmpCategoryId,_tmpTxnRef,_tmpTimestamp,_tmpSource,_tmpParseConfidence,_tmpStatus,_tmpIsDuplicateOf,_tmpRawSmsText,_tmpNote);
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
  public Flow<List<TransactionEntity>> getByStatusFlow(final TransactionStatus status) {
    final String _sql = "SELECT * FROM transactions WHERE status = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromTransactionStatus(status);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_raw");
          final int _cursorIndexOfMerchantNormalized = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_normalized");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfTxnRef = CursorUtil.getColumnIndexOrThrow(_cursor, "txn_ref");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfParseConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "parse_confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsDuplicateOf = CursorUtil.getColumnIndexOrThrow(_cursor, "is_duplicate_of");
          final int _cursorIndexOfRawSmsText = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_sms_text");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final TransactionType _tmpType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toTransactionType(_tmp_1);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantNormalized;
            _tmpMerchantNormalized = _cursor.getString(_cursorIndexOfMerchantNormalized);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final String _tmpTxnRef;
            _tmpTxnRef = _cursor.getString(_cursorIndexOfTxnRef);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final TransactionSource _tmpSource;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toTransactionSource(_tmp_2);
            final float _tmpParseConfidence;
            _tmpParseConfidence = _cursor.getFloat(_cursorIndexOfParseConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTransactionStatus(_tmp_3);
            final Long _tmpIsDuplicateOf;
            if (_cursor.isNull(_cursorIndexOfIsDuplicateOf)) {
              _tmpIsDuplicateOf = null;
            } else {
              _tmpIsDuplicateOf = _cursor.getLong(_cursorIndexOfIsDuplicateOf);
            }
            final String _tmpRawSmsText;
            if (_cursor.isNull(_cursorIndexOfRawSmsText)) {
              _tmpRawSmsText = null;
            } else {
              _tmpRawSmsText = _cursor.getString(_cursorIndexOfRawSmsText);
            }
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpAmount,_tmpType,_tmpMerchantRaw,_tmpMerchantNormalized,_tmpCategoryId,_tmpTxnRef,_tmpTimestamp,_tmpSource,_tmpParseConfidence,_tmpStatus,_tmpIsDuplicateOf,_tmpRawSmsText,_tmpNote);
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
  public Object getByAccount(final long accountId,
      final Continuation<? super List<TransactionEntity>> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, accountId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_raw");
          final int _cursorIndexOfMerchantNormalized = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_normalized");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfTxnRef = CursorUtil.getColumnIndexOrThrow(_cursor, "txn_ref");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfParseConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "parse_confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsDuplicateOf = CursorUtil.getColumnIndexOrThrow(_cursor, "is_duplicate_of");
          final int _cursorIndexOfRawSmsText = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_sms_text");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final TransactionType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toTransactionType(_tmp);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantNormalized;
            _tmpMerchantNormalized = _cursor.getString(_cursorIndexOfMerchantNormalized);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final String _tmpTxnRef;
            _tmpTxnRef = _cursor.getString(_cursorIndexOfTxnRef);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final TransactionSource _tmpSource;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toTransactionSource(_tmp_1);
            final float _tmpParseConfidence;
            _tmpParseConfidence = _cursor.getFloat(_cursorIndexOfParseConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTransactionStatus(_tmp_2);
            final Long _tmpIsDuplicateOf;
            if (_cursor.isNull(_cursorIndexOfIsDuplicateOf)) {
              _tmpIsDuplicateOf = null;
            } else {
              _tmpIsDuplicateOf = _cursor.getLong(_cursorIndexOfIsDuplicateOf);
            }
            final String _tmpRawSmsText;
            if (_cursor.isNull(_cursorIndexOfRawSmsText)) {
              _tmpRawSmsText = null;
            } else {
              _tmpRawSmsText = _cursor.getString(_cursorIndexOfRawSmsText);
            }
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpAmount,_tmpType,_tmpMerchantRaw,_tmpMerchantNormalized,_tmpCategoryId,_tmpTxnRef,_tmpTimestamp,_tmpSource,_tmpParseConfidence,_tmpStatus,_tmpIsDuplicateOf,_tmpRawSmsText,_tmpNote);
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
  public Object getByDateRange(final long from, final long to,
      final Continuation<? super List<TransactionEntity>> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, from);
    _argIndex = 2;
    _statement.bindLong(_argIndex, to);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_raw");
          final int _cursorIndexOfMerchantNormalized = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_normalized");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfTxnRef = CursorUtil.getColumnIndexOrThrow(_cursor, "txn_ref");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfParseConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "parse_confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsDuplicateOf = CursorUtil.getColumnIndexOrThrow(_cursor, "is_duplicate_of");
          final int _cursorIndexOfRawSmsText = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_sms_text");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final TransactionType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toTransactionType(_tmp);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantNormalized;
            _tmpMerchantNormalized = _cursor.getString(_cursorIndexOfMerchantNormalized);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final String _tmpTxnRef;
            _tmpTxnRef = _cursor.getString(_cursorIndexOfTxnRef);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final TransactionSource _tmpSource;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toTransactionSource(_tmp_1);
            final float _tmpParseConfidence;
            _tmpParseConfidence = _cursor.getFloat(_cursorIndexOfParseConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTransactionStatus(_tmp_2);
            final Long _tmpIsDuplicateOf;
            if (_cursor.isNull(_cursorIndexOfIsDuplicateOf)) {
              _tmpIsDuplicateOf = null;
            } else {
              _tmpIsDuplicateOf = _cursor.getLong(_cursorIndexOfIsDuplicateOf);
            }
            final String _tmpRawSmsText;
            if (_cursor.isNull(_cursorIndexOfRawSmsText)) {
              _tmpRawSmsText = null;
            } else {
              _tmpRawSmsText = _cursor.getString(_cursorIndexOfRawSmsText);
            }
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpAmount,_tmpType,_tmpMerchantRaw,_tmpMerchantNormalized,_tmpCategoryId,_tmpTxnRef,_tmpTimestamp,_tmpSource,_tmpParseConfidence,_tmpStatus,_tmpIsDuplicateOf,_tmpRawSmsText,_tmpNote);
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
  public Object getByCategory(final long categoryId,
      final Continuation<? super List<TransactionEntity>> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE category_id = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, categoryId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_raw");
          final int _cursorIndexOfMerchantNormalized = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_normalized");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfTxnRef = CursorUtil.getColumnIndexOrThrow(_cursor, "txn_ref");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfParseConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "parse_confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsDuplicateOf = CursorUtil.getColumnIndexOrThrow(_cursor, "is_duplicate_of");
          final int _cursorIndexOfRawSmsText = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_sms_text");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final TransactionType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toTransactionType(_tmp);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantNormalized;
            _tmpMerchantNormalized = _cursor.getString(_cursorIndexOfMerchantNormalized);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final String _tmpTxnRef;
            _tmpTxnRef = _cursor.getString(_cursorIndexOfTxnRef);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final TransactionSource _tmpSource;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toTransactionSource(_tmp_1);
            final float _tmpParseConfidence;
            _tmpParseConfidence = _cursor.getFloat(_cursorIndexOfParseConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTransactionStatus(_tmp_2);
            final Long _tmpIsDuplicateOf;
            if (_cursor.isNull(_cursorIndexOfIsDuplicateOf)) {
              _tmpIsDuplicateOf = null;
            } else {
              _tmpIsDuplicateOf = _cursor.getLong(_cursorIndexOfIsDuplicateOf);
            }
            final String _tmpRawSmsText;
            if (_cursor.isNull(_cursorIndexOfRawSmsText)) {
              _tmpRawSmsText = null;
            } else {
              _tmpRawSmsText = _cursor.getString(_cursorIndexOfRawSmsText);
            }
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpAmount,_tmpType,_tmpMerchantRaw,_tmpMerchantNormalized,_tmpCategoryId,_tmpTxnRef,_tmpTimestamp,_tmpSource,_tmpParseConfidence,_tmpStatus,_tmpIsDuplicateOf,_tmpRawSmsText,_tmpNote);
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
  public Object getByStatus(final TransactionStatus status,
      final Continuation<? super List<TransactionEntity>> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE status = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromTransactionStatus(status);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_raw");
          final int _cursorIndexOfMerchantNormalized = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_normalized");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfTxnRef = CursorUtil.getColumnIndexOrThrow(_cursor, "txn_ref");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfParseConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "parse_confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsDuplicateOf = CursorUtil.getColumnIndexOrThrow(_cursor, "is_duplicate_of");
          final int _cursorIndexOfRawSmsText = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_sms_text");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final TransactionType _tmpType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toTransactionType(_tmp_1);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantNormalized;
            _tmpMerchantNormalized = _cursor.getString(_cursorIndexOfMerchantNormalized);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final String _tmpTxnRef;
            _tmpTxnRef = _cursor.getString(_cursorIndexOfTxnRef);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final TransactionSource _tmpSource;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toTransactionSource(_tmp_2);
            final float _tmpParseConfidence;
            _tmpParseConfidence = _cursor.getFloat(_cursorIndexOfParseConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTransactionStatus(_tmp_3);
            final Long _tmpIsDuplicateOf;
            if (_cursor.isNull(_cursorIndexOfIsDuplicateOf)) {
              _tmpIsDuplicateOf = null;
            } else {
              _tmpIsDuplicateOf = _cursor.getLong(_cursorIndexOfIsDuplicateOf);
            }
            final String _tmpRawSmsText;
            if (_cursor.isNull(_cursorIndexOfRawSmsText)) {
              _tmpRawSmsText = null;
            } else {
              _tmpRawSmsText = _cursor.getString(_cursorIndexOfRawSmsText);
            }
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpAmount,_tmpType,_tmpMerchantRaw,_tmpMerchantNormalized,_tmpCategoryId,_tmpTxnRef,_tmpTimestamp,_tmpSource,_tmpParseConfidence,_tmpStatus,_tmpIsDuplicateOf,_tmpRawSmsText,_tmpNote);
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
  public Object getTotalByCategory(final long categoryId, final long from, final long to,
      final Continuation<? super Double> $completion) {
    final String _sql = "\n"
            + "        SELECT COALESCE(SUM(amount), 0.0) FROM transactions \n"
            + "        WHERE category_id = ? \n"
            + "        AND type = 'DEBIT'\n"
            + "        AND timestamp BETWEEN ? AND ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, categoryId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, from);
    _argIndex = 3;
    _statement.bindLong(_argIndex, to);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @NonNull
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final double _tmp;
            _tmp = _cursor.getDouble(0);
            _result = _tmp;
          } else {
            _result = 0.0;
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
  public Object findDuplicate(final double amount, final String accountLast4,
      final long timestampFrom, final long timestampTo,
      final Continuation<? super TransactionEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM transactions \n"
            + "        WHERE amount = ? \n"
            + "        AND account_id IN (SELECT id FROM accounts WHERE account_last4 = ?)\n"
            + "        AND timestamp BETWEEN ? AND ?\n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindDouble(_argIndex, amount);
    _argIndex = 2;
    _statement.bindString(_argIndex, accountLast4);
    _argIndex = 3;
    _statement.bindLong(_argIndex, timestampFrom);
    _argIndex = 4;
    _statement.bindLong(_argIndex, timestampTo);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TransactionEntity>() {
      @Override
      @Nullable
      public TransactionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_raw");
          final int _cursorIndexOfMerchantNormalized = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_normalized");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfTxnRef = CursorUtil.getColumnIndexOrThrow(_cursor, "txn_ref");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfParseConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "parse_confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsDuplicateOf = CursorUtil.getColumnIndexOrThrow(_cursor, "is_duplicate_of");
          final int _cursorIndexOfRawSmsText = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_sms_text");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final TransactionEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final TransactionType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toTransactionType(_tmp);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantNormalized;
            _tmpMerchantNormalized = _cursor.getString(_cursorIndexOfMerchantNormalized);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final String _tmpTxnRef;
            _tmpTxnRef = _cursor.getString(_cursorIndexOfTxnRef);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final TransactionSource _tmpSource;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toTransactionSource(_tmp_1);
            final float _tmpParseConfidence;
            _tmpParseConfidence = _cursor.getFloat(_cursorIndexOfParseConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTransactionStatus(_tmp_2);
            final Long _tmpIsDuplicateOf;
            if (_cursor.isNull(_cursorIndexOfIsDuplicateOf)) {
              _tmpIsDuplicateOf = null;
            } else {
              _tmpIsDuplicateOf = _cursor.getLong(_cursorIndexOfIsDuplicateOf);
            }
            final String _tmpRawSmsText;
            if (_cursor.isNull(_cursorIndexOfRawSmsText)) {
              _tmpRawSmsText = null;
            } else {
              _tmpRawSmsText = _cursor.getString(_cursorIndexOfRawSmsText);
            }
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _result = new TransactionEntity(_tmpId,_tmpAccountId,_tmpAmount,_tmpType,_tmpMerchantRaw,_tmpMerchantNormalized,_tmpCategoryId,_tmpTxnRef,_tmpTimestamp,_tmpSource,_tmpParseConfidence,_tmpStatus,_tmpIsDuplicateOf,_tmpRawSmsText,_tmpNote);
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
  public Object getRecentByMerchant(final String merchant,
      final Continuation<? super List<TransactionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM transactions \n"
            + "        WHERE merchant_normalized = ? \n"
            + "        ORDER BY timestamp DESC \n"
            + "        LIMIT 6\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, merchant);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_raw");
          final int _cursorIndexOfMerchantNormalized = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_normalized");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfTxnRef = CursorUtil.getColumnIndexOrThrow(_cursor, "txn_ref");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfParseConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "parse_confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsDuplicateOf = CursorUtil.getColumnIndexOrThrow(_cursor, "is_duplicate_of");
          final int _cursorIndexOfRawSmsText = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_sms_text");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final TransactionType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toTransactionType(_tmp);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantNormalized;
            _tmpMerchantNormalized = _cursor.getString(_cursorIndexOfMerchantNormalized);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final String _tmpTxnRef;
            _tmpTxnRef = _cursor.getString(_cursorIndexOfTxnRef);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final TransactionSource _tmpSource;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toTransactionSource(_tmp_1);
            final float _tmpParseConfidence;
            _tmpParseConfidence = _cursor.getFloat(_cursorIndexOfParseConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTransactionStatus(_tmp_2);
            final Long _tmpIsDuplicateOf;
            if (_cursor.isNull(_cursorIndexOfIsDuplicateOf)) {
              _tmpIsDuplicateOf = null;
            } else {
              _tmpIsDuplicateOf = _cursor.getLong(_cursorIndexOfIsDuplicateOf);
            }
            final String _tmpRawSmsText;
            if (_cursor.isNull(_cursorIndexOfRawSmsText)) {
              _tmpRawSmsText = null;
            } else {
              _tmpRawSmsText = _cursor.getString(_cursorIndexOfRawSmsText);
            }
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpAmount,_tmpType,_tmpMerchantRaw,_tmpMerchantNormalized,_tmpCategoryId,_tmpTxnRef,_tmpTimestamp,_tmpSource,_tmpParseConfidence,_tmpStatus,_tmpIsDuplicateOf,_tmpRawSmsText,_tmpNote);
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
  public Object getTopMerchants(final long from, final long to, final int limit,
      final Continuation<? super List<MerchantTotal>> $completion) {
    final String _sql = "\n"
            + "        SELECT merchant_normalized, SUM(amount) as total \n"
            + "        FROM transactions \n"
            + "        WHERE type = 'DEBIT' AND timestamp BETWEEN ? AND ?\n"
            + "        GROUP BY merchant_normalized \n"
            + "        ORDER BY total DESC \n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, from);
    _argIndex = 2;
    _statement.bindLong(_argIndex, to);
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MerchantTotal>>() {
      @Override
      @NonNull
      public List<MerchantTotal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMerchantNormalized = 0;
          final int _cursorIndexOfTotal = 1;
          final List<MerchantTotal> _result = new ArrayList<MerchantTotal>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MerchantTotal _item;
            final String _tmpMerchant_normalized;
            _tmpMerchant_normalized = _cursor.getString(_cursorIndexOfMerchantNormalized);
            final double _tmpTotal;
            _tmpTotal = _cursor.getDouble(_cursorIndexOfTotal);
            _item = new MerchantTotal(_tmpMerchant_normalized,_tmpTotal);
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
  public Object getRecent(final int limit,
      final Continuation<? super List<TransactionEntity>> $completion) {
    final String _sql = "SELECT * FROM transactions ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_raw");
          final int _cursorIndexOfMerchantNormalized = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant_normalized");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfTxnRef = CursorUtil.getColumnIndexOrThrow(_cursor, "txn_ref");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfParseConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "parse_confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsDuplicateOf = CursorUtil.getColumnIndexOrThrow(_cursor, "is_duplicate_of");
          final int _cursorIndexOfRawSmsText = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_sms_text");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final TransactionType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toTransactionType(_tmp);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantNormalized;
            _tmpMerchantNormalized = _cursor.getString(_cursorIndexOfMerchantNormalized);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final String _tmpTxnRef;
            _tmpTxnRef = _cursor.getString(_cursorIndexOfTxnRef);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final TransactionSource _tmpSource;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toTransactionSource(_tmp_1);
            final float _tmpParseConfidence;
            _tmpParseConfidence = _cursor.getFloat(_cursorIndexOfParseConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTransactionStatus(_tmp_2);
            final Long _tmpIsDuplicateOf;
            if (_cursor.isNull(_cursorIndexOfIsDuplicateOf)) {
              _tmpIsDuplicateOf = null;
            } else {
              _tmpIsDuplicateOf = _cursor.getLong(_cursorIndexOfIsDuplicateOf);
            }
            final String _tmpRawSmsText;
            if (_cursor.isNull(_cursorIndexOfRawSmsText)) {
              _tmpRawSmsText = null;
            } else {
              _tmpRawSmsText = _cursor.getString(_cursorIndexOfRawSmsText);
            }
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpAmount,_tmpType,_tmpMerchantRaw,_tmpMerchantNormalized,_tmpCategoryId,_tmpTxnRef,_tmpTimestamp,_tmpSource,_tmpParseConfidence,_tmpStatus,_tmpIsDuplicateOf,_tmpRawSmsText,_tmpNote);
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
