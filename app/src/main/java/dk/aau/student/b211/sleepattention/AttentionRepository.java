package dk.aau.student.b211.sleepattention;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.test.ActivityTestCase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by alexk on 03-04-2016.
 */
public class AttentionRepository {

    private DatabaseHelper dbHelper;
    private List<Attention> attentionList;
    private static final String TAG = AttentionRepository.class.getSimpleName();

    public AttentionRepository(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean updateRecord(int attentionID, double time, @NonNull Date date, int score) {
        if (attentionID <= -1) {
            return false;
        }
        ContentValues cv = new ContentValues();
        cv.put(Attention.KEY_ID, attentionID);
        cv.put(Attention.KEY_SCORE, score);
        cv.put(Attention.KEY_TIME, time);
        cv.put(Attention.KEY_DATE, new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("da", "DK")).format(date));
        if (dbHelper.getWritableDatabase().update(Attention.TABLE_NAME, cv, Attention.KEY_ID + " = ? ", new String[]{attentionID + ""}) >= -1) {
            dbHelper.close();
            return true;
        }
        dbHelper.close();
        return false;
    }

    public boolean deleteRecord(int attentionID) {
        if(dbHelper.getWritableDatabase().delete(Attention.TABLE_NAME, Attention.KEY_ID + " = ? ", new String[]{Integer.toString(attentionID)}) >= 1) {
            dbHelper.close();
            return true;
        }
        dbHelper.close();
        return false;
    }

    public boolean insertRecord(double time, @NonNull Date date, int score) {
        ContentValues cv = new ContentValues();
        cv.put(Attention.KEY_TIME, time);
        cv.put(Attention.KEY_SCORE, score);
        cv.put(Attention.KEY_DATE, new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("da", "DK")).format(date));
        if(dbHelper.getWritableDatabase().insert(Attention.TABLE_NAME, null, cv) == -1) {
            dbHelper.close();
            return false;
        }
        dbHelper.close();
        return true;
    }

    public Attention getRecord(int attentionID) {
        return parseResults(dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + Attention.TABLE_NAME, new String[]{Integer.toString(attentionID)})).get(0);
    }

    public List<Attention> getAllRecords() {
        return parseResults(dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + Attention.TABLE_NAME, null));
    }

    private List<Attention> parseResults(Cursor results) {
        List<Attention> attentionList = new ArrayList<>();
        if(results.moveToFirst()) {
            do {
                try {
                    Attention a = new Attention(
                            results.getInt(results.getColumnIndex(Attention.KEY_ID)),
                            results.getDouble(results.getColumnIndex(Attention.KEY_TIME)),
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
        } else {
            attentionList.add(null);
        }
        dbHelper.close();
        results.close();
        return attentionList;
    }
}
