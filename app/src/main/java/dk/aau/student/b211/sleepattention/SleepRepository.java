package dk.aau.student.b211.sleepattention;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Group B211, Aalborg University on 03-04-2016.
 * TODO: Remove 'quality' from database
 */
class SleepRepository {

    private final DatabaseHelper dbHelper;
    private static final String TAG = SleepRepository.class.getSimpleName();

    public SleepRepository(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean insertRecord(long duration, Date date, int quality) {
        ContentValues cv = new ContentValues();
        if (duration == 0.0 || date == null) {
            return false;
        }
        cv.put(Sleep.KEY_DURATION, duration);
        cv.put(Sleep.KEY_DATE, new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("da", "DK")).format(date));
        cv.put(Sleep.KEY_QUALITY, quality);

        if(dbHelper.getWritableDatabase().insert(Sleep.TABLE_NAME, null, cv) == -1) {
            dbHelper.closeDB();
            return false;
        }
        dbHelper.closeDB();
        return true;
    }

    public boolean deleteRecord(int sleepID) {
        if (dbHelper.getWritableDatabase().delete(Sleep.TABLE_NAME, Sleep.KEY_ID + " = ?", new String[]{Integer.toString(sleepID)}) >= 1) {
            dbHelper.closeDB();
            return true;
        }
        dbHelper.closeDB();
        return false;
    }

    /**
     *
     * @param sleepID
     * @return
     */
    public Sleep getRecord(int sleepID) {
        List<Sleep> results = parseResults(dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + Sleep.TABLE_NAME + " WHERE " + Sleep.KEY_ID + " = ? ",
                new String[]{Integer.toString(sleepID)}));
        if(results.size() != 0) {
            return results.get(0);
        }
        else {
            return null;
        }

    }

    public Sleep getLatestRecord() {
        List<Sleep> results = parseResults(dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + Sleep.TABLE_NAME + " ORDER BY " + Sleep.KEY_ID + " DESC LIMIT 1 ", null));
        if(results.size() != 0)
            return results.get(0);
        else
            return null;
    }

    public List<Sleep> getAllRecords() {
        return parseResults(dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + Sleep.TABLE_NAME, null));
    }

    private List<Sleep> parseResults(Cursor results) {
        List<Sleep> sleepList = new ArrayList<>();
        if(results.moveToFirst()) {
            do {
                try {
                    Sleep s = new Sleep(
                            results.getInt(results.getColumnIndex(Sleep.KEY_ID)),
                            results.getLong(results.getColumnIndex(Sleep.KEY_DURATION)),
                            new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("da", "DK")).parse(results.getString(results.getColumnIndex(Sleep.KEY_DATE))),
                            results.getInt(results.getColumnIndex(Sleep.KEY_QUALITY)));
                    sleepList.add(s);
                } catch(ParseException e) {
                    Log.e(TAG, "ParseException happened: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            while (results.moveToNext());
        }
        dbHelper.closeDB();
        results.close();
        return sleepList;
    }

    public boolean updateRecord(int sleepID, long duration, Date date, int quality) {
        ContentValues cv = new ContentValues();
        if (sleepID >= 0) {
            cv.put(Sleep.KEY_ID, sleepID);
            if(date != null) {
                cv.put(Sleep.KEY_DATE, new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("da", "DK")).format(date));
            }
            cv.put(Sleep.KEY_QUALITY, quality);
            cv.put(Sleep.KEY_DURATION, duration);
            if(dbHelper.getWritableDatabase().update(Sleep.TABLE_NAME, cv, Sleep.KEY_ID + " = ?", new String[]{sleepID + ""}) >= 1) {
                return true;
            }
        }
        return false;
    }
}
