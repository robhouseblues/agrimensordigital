package br.edu.utfpr.agrimensordigital.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "agrimensor";

    private static DatabaseHandler databaseInstance;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHandler getInstance(Context context) {
        if (databaseInstance == null) {
            newInstance(context);
        }

        return databaseInstance;
    }

    public static void newInstance(Context context) {
        databaseInstance = new DatabaseHandler(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS pontos (id INTEGER PRIMARY KEY AUTOINCREMENT, latitude REAL, longitude REAL, id_area INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS areas (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, perimetro REAL, area REAL, imagem TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE pontos");
        db.execSQL("DROP TABLE areas");
        onCreate(db);
    }

}
