package com.jawaadianinc.rubixcubesolver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class DatabaseTimes extends SQLiteOpenHelper {

    public static final String TIME_SOLVE = "TIME_SOLVE";
    public static final String DATE_TIME = "DATE_TIME";
    public static final String TIMES_TABLE = "TIMES_TABLE";
    public static final String SHUFFLE = "SHUFFLE";
    public static final String TYPE_OF_CUBE = "TYPE_OF_CUBE";

    public DatabaseTimes(@Nullable Context context) {
        super(context, "times.db", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TIMES_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + TIME_SOLVE + " TEXT, " + SHUFFLE + " TEXT, " + DATE_TIME + " TIMESTAMP, " + TYPE_OF_CUBE + " TEXT)";
        db.execSQL(createTable);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addTime(TimeModel timeModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TIME_SOLVE, timeModel.getTimeSolved());
        cv.put(SHUFFLE, timeModel.getShuffle());
        cv.put(DATE_TIME, String.valueOf(new java.sql.Timestamp(new java.util.Date().getTime())));
        cv.put(TYPE_OF_CUBE, timeModel.getTypeOfCube());
        final long insert = db.insert(TIMES_TABLE, null, cv);
        return insert != -1;
    }


    public int getId(String time) {
        int ID = 1;
        SQLiteDatabase db = this.getReadableDatabase();
        String SQLString = "SELECT ID FROM " + TIMES_TABLE + " WHERE " + TIME_SOLVE + " = '" + time + "'";

        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            ID = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return ID;
    }

//    public String getInformation(int i){
//        String returnData = "";
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        String SQLString = "SELECT SHUFFLE, DATE_TIME, TYPE_OF_CUBE FROM TIMES_TABLE WHERE ID = " + i;
//        Cursor cursor = db.rawQuery(SQLString, null);
//        if (cursor.moveToFirst()){
//            returnData = "On " + cursor.getString(1) + "\nShuffle:\n" + cursor.getString(0) + "\nType of Cube_Original: " + cursor.getString(2);
//        }
//
//        cursor.close();
//        db.close();
//
//        return returnData;
//    }

    public String getTimeID(int i) {
        String DateTime = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String SQLString = "SELECT " + DATE_TIME + " FROM " + TIMES_TABLE + " WHERE ID = " + i;

        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            DateTime = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return DateTime;
    }

    public String getShuffle(int i) {
        String Shuffle = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String SQLString = "SELECT " + SHUFFLE + " FROM " + TIMES_TABLE + " WHERE ID = " + i;

        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            Shuffle = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return Shuffle;
    }

    public String getTypeOfCube(int i) {
        String CubeType = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String SQLString = "SELECT " + TYPE_OF_CUBE + " FROM " + TIMES_TABLE + " WHERE ID = " + i;

        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            CubeType = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return CubeType;
    }

    public String getSolvedTime(int i) {
        String Time = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String SQLString = "SELECT " + TIME_SOLVE + " FROM " + TIMES_TABLE + " WHERE ID = " + i;

        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            Time = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return Time;
    }


    public boolean DeleteTime(String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TIMES_TABLE + " WHERE " + TIME_SOLVE + " = '" + time + "'");
        return true;
    }

    public List<TimeModel> get3x3Times() {
        List<TimeModel> returnList = new ArrayList<>();
        String SQLString = "SELECT * FROM " + TIMES_TABLE + " WHERE " + TYPE_OF_CUBE + " = '3x3'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            do {
                String TimeSolved = cursor.getString(1);
                String DateTime = cursor.getString(2);
                String Shuffle = cursor.getString(3);
                String TypeOfCube = cursor.getString(4);
                TimeModel newTime = new TimeModel(TimeSolved, DateTime, Shuffle, TypeOfCube);
                returnList.add(newTime);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return returnList;
    }

    public List<TimeModel> get2x2Times() {
        List<TimeModel> returnList = new ArrayList<>();
        String SQLString = "SELECT * FROM " + TIMES_TABLE + " WHERE " + TYPE_OF_CUBE + " = '2x2'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            do {
                String TimeSolved = cursor.getString(1);
                String DateTime = cursor.getString(2);
                String Shuffle = cursor.getString(3);
                String TypeOfCube = cursor.getString(4);
                TimeModel newTime = new TimeModel(TimeSolved, DateTime, Shuffle, TypeOfCube);
                returnList.add(newTime);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return returnList;
    }

    public List<TimeModel> get4x4Times() {
        List<TimeModel> returnList = new ArrayList<>();
        String SQLString = "SELECT * FROM " + TIMES_TABLE + " WHERE " + TYPE_OF_CUBE + " = '4x4'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            do {
                String TimeSolved = cursor.getString(1);
                String DateTime = cursor.getString(2);
                String Shuffle = cursor.getString(3);
                String TypeOfCube = cursor.getString(4);
                TimeModel newTime = new TimeModel(TimeSolved, DateTime, Shuffle, TypeOfCube);
                returnList.add(newTime);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return returnList;
    }

    public String getAllTimes3x3() throws ParseException {
        StringBuilder returnList = new StringBuilder();
        ArrayList<String> shortestTime = new ArrayList<>();
        boolean isEmpty = true;

        String SQLString = "SELECT * FROM " + TIMES_TABLE + " WHERE " + TYPE_OF_CUBE + " = '3x3'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            do {
                String TimeSolved = cursor.getString(1);
                shortestTime.add(TimeSolved);
                if (!isEmpty) {
                    returnList.append(" ").append(TimeSolved);
                } else {
                    returnList.append(TimeSolved);
                }
                isEmpty = false;

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if (!returnList.toString().isEmpty()) {
            String[] split = returnList.toString().split(" ");
            long sum = 0L;
            SimpleDateFormat sdf = new SimpleDateFormat("m:ss:SSS", Locale.ENGLISH);
            for (String s : split) {
                sum += Objects.requireNonNull(sdf.parse(s)).getTime();
            }
            Date avgDate = new Date((sum / split.length));
            String fastest = shortestTime.stream().min(Comparator.naturalOrder()).get();
            String longest = shortestTime.stream().max(Comparator.naturalOrder()).get();
            return sdf.format(avgDate) + "\nBest time: " + fastest + "\nWorst time: " + longest;
        }
        return "No Time set";
    }

    public String getAllTimes2x2() throws ParseException {
        StringBuilder returnList = new StringBuilder();
        ArrayList<String> shortestTime = new ArrayList<>();
        boolean isEmpty = true;

        String SQLString = "SELECT * FROM " + TIMES_TABLE + " WHERE " + TYPE_OF_CUBE + " = '2x2'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            do {
                String TimeSolved = cursor.getString(1);
                shortestTime.add(TimeSolved);
                if (!isEmpty) {
                    returnList.append(" ").append(TimeSolved);
                } else {
                    returnList.append(TimeSolved);
                }
                isEmpty = false;

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if (!returnList.toString().isEmpty()) {
            String[] split = returnList.toString().split(" ");
            long sum = 0L;
            SimpleDateFormat sdf = new SimpleDateFormat("m:ss:SSS", Locale.ENGLISH);
            for (String s : split) {
                sum += Objects.requireNonNull(sdf.parse(s)).getTime();
            }
            Date avgDate = new Date((sum / split.length));
            String fastest = shortestTime.stream().min(Comparator.naturalOrder()).get();
            String longest = shortestTime.stream().max(Comparator.naturalOrder()).get();
            return sdf.format(avgDate) + "\nBest time: " + fastest + "\nWorst time: " + longest;
        }
        return "No Time set";
    }

    public String getAllTimes4x4() throws ParseException {
        StringBuilder returnList = new StringBuilder();
        ArrayList<String> shortestTime = new ArrayList<>();
        boolean isEmpty = true;

        String SQLString = "SELECT * FROM " + TIMES_TABLE + " WHERE " + TYPE_OF_CUBE + " = '4x4'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            do {
                String TimeSolved = cursor.getString(1);
                shortestTime.add(TimeSolved);
                if (!isEmpty) {
                    returnList.append(" ").append(TimeSolved);
                } else {
                    returnList.append(TimeSolved);
                }
                isEmpty = false;

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if (!returnList.toString().isEmpty()) {
            String[] split = returnList.toString().split(" ");
            long sum = 0L;
            SimpleDateFormat sdf = new SimpleDateFormat("m:ss:SSS", Locale.ENGLISH);
            for (String s : split) {
                sum += Objects.requireNonNull(sdf.parse(s)).getTime();
            }
            Date avgDate = new Date((sum / split.length));
            String fastest = shortestTime.stream().min(Comparator.naturalOrder()).get();
            String longest = shortestTime.stream().max(Comparator.naturalOrder()).get();
            return sdf.format(avgDate) + "\nBest time: " + fastest + "\nWorst time: " + longest;
        }
        return "No Time set";
    }

    public long getTotalSolves() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TIMES_TABLE);
        db.close();
        return count;
    }

    public int getTotalSolves3x3() {
        int solves3x3 = 0;
        String SQLString = "SELECT * FROM " + TIMES_TABLE + " WHERE " + TYPE_OF_CUBE + " = '3x3'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLString, null);

        if (cursor.moveToFirst()) {
            do {
                solves3x3 += 1;
            } while (cursor.moveToNext());

        }

        return solves3x3;
    }

    public int getTotalSolves4x4() {
        int solves3x3 = 0;
        String SQLString = "SELECT * FROM " + TIMES_TABLE + " WHERE " + TYPE_OF_CUBE + " = '4x4'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLString, null);

        if (cursor.moveToFirst()) {
            do {
                solves3x3 += 1;
            } while (cursor.moveToNext());

        }
        return solves3x3;
    }

    public int getTotalSolves2x2() {
        int solves3x3 = 0;
        String SQLString = "SELECT * FROM " + TIMES_TABLE + " WHERE " + TYPE_OF_CUBE + " = '2x2'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            do {
                solves3x3 += 1;
            } while (cursor.moveToNext());

        }
        return solves3x3;
    }


}
