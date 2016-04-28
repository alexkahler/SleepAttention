package dk.aau.student.b211.sleepattention;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Group B211, Aalborg University
 * Created on 03-04-2016.
 *
 */
class AttentionRepository {

    private final DatabaseHelper dbHelper;
    private static final String TAG = AttentionRepository.class.getSimpleName();

    public AttentionRepository(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean updateRecord(int attentionID, @NonNull Date date, int score) {
        if (attentionID <= -1) {
            return false;
        }
        ContentValues cv = new ContentValues();
        cv.put(Attention.KEY_ID, attentionID);
        cv.put(Attention.KEY_SCORE, score);
        cv.put(Attention.KEY_DATE, new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("da", "DK")).format(date));
        if (dbHelper.getWritableDatabase().update(Attention.TABLE_NAME, cv, Attention.KEY_ID + " = ? ", new String[]{attentionID + ""}) >= -1) {
            dbHelper.closeDB();
            return true;
        }
        dbHelper.closeDB();
        return false;
    }

    public boolean deleteRecord(int attentionID) {
        if(dbHelper.getWritableDatabase().delete(Attention.TABLE_NAME, Attention.KEY_ID + " = ? ", new String[]{Integer.toString(attentionID)}) >= 1) {
            dbHelper.closeDB();
            return true;
        }
        dbHelper.closeDB();
        return false;
    }

    public boolean insertRecord(@NonNull Date date, int score) {
        ContentValues cv = new ContentValues();
        cv.put(Attention.KEY_SCORE, score);
        cv.put(Attention.KEY_DATE, new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("da", "DK")).format(date));
        if(dbHelper.getWritableDatabase().insert(Attention.TABLE_NAME, null, cv) == -1) {
            dbHelper.closeDB();
            return false;
        }
        dbHelper.closeDB();
        return true;
    }

    public Attention getRecord(int attentionID) {
        List<Attention> result = parseResults(dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + Attention.TABLE_NAME, new String[]{Integer.toString(attentionID)}));
        if (result.size() != 0)
            return result.get(0);
        else
            return null;
    }

    public List<Attention> getAllRecords() {
        return parseResults(dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + Attention.TABLE_NAME, null));
    }

    public Attention getLatestRecord() {
        List<Attention> result = parseResults(dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + Attention.TABLE_NAME + " ORDER BY " + Attention.KEY_ID + " DESC LIMIT 1", null));
        if (result.size() != 0)
            return result.get(0);
        else
            return null;
    }

    private List<Attention> parseResults(Cursor results) {
        List<Attention> attentionList = new ArrayList<>();
        if(results.moveToFirst()) {
            do {
                try {
                    Attention a = new Attention(
                            results.getInt(results.getColumnIndex(Attention.KEY_ID)),
                            new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("da", "DK"))
                                    .parse(results.getString(results.getColumnIndex(Attention.KEY_DATE))),
                            results.getInt(results.getColumnIndex(Attention.KEY_SCORE)));
                    attentionList.add(a);
                } catch(ParseException e) {
                    Log.e(TAG, "ParseException happened: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            while (results.moveToNext());
        }
        dbHelper.closeDB();
        results.close();
        return attentionList;
    }
}
