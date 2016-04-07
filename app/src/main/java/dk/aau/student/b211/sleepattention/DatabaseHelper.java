package dk.aau.student.b211.sleepattention;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alexk on 03-04-2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SleepAttention.db";
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper instance;
    private final Context context;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        executeSQLScript(db, "database_setup.sql");
        Log.v(TAG, "onCreate execute setup script");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            switch (oldVersion) {
                case 1: {
                    //executeSQLScript(db, "upgrade_v2.sql");
                    //DATABASE_VERSION = 2;
                }
                case 2: {
                    // Run next upgrade script;
                    break;
                }
            }
        }
    }

    private void executeSQLScript(SQLiteDatabase db, String fileName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buffer[] = new byte[1024];
        int length;
        InputStream inputStream;
        try {
            inputStream = context.getAssets().open(fileName);
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            String[] createScript = outputStream.toString().split(";");
            for (String s : createScript) {
                String sqlStatement = s.trim();
                if(sqlStatement.length() > 0) {
                    db.execSQL(sqlStatement + ";");
                }
            }
        }
        catch(IOException e) {
            Log.e(TAG,"Unknown IOException: " + e.getMessage());
            e.printStackTrace();
        } catch(SQLException e) {
            Log.e(TAG, "Wrong SQL file mate: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
