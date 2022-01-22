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

    //Public strings to use in the database to make it easier to use and reference
    public static final String TIME_SOLVE = "TIME_SOLVE";
    public static final String DATE_TIME = "DATE_TIME";
    public static final String TIMES_TABLE = "TIMES_TABLE";
    public static final String SHUFFLE = "SHUFFLE";
    public static final String TYPE_OF_CUBE = "TYPE_OF_CUBE";
    public static final String USER_NAME = "USER_NAME";

    //Creating the database called "users" and will automatically create if one isn't detected
    public DatabaseTimes(@Nullable Context context) {
        super(context, "solveTimes.db", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TIMES_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + TIME_SOLVE + " TEXT, " + SHUFFLE + " TEXT, " + DATE_TIME + " TIMESTAMP, " + TYPE_OF_CUBE + " TEXT, " + USER_NAME + " TEXT)";
        db.execSQL(createTable);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addTime(TimeModel timeModel) {
        //Method to add a time to the database via the time model class
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TIME_SOLVE, timeModel.getTimeSolved());
        cv.put(SHUFFLE, timeModel.getShuffle());
        cv.put(DATE_TIME, String.valueOf(new java.sql.Timestamp(new java.util.Date().getTime())));
        cv.put(TYPE_OF_CUBE, timeModel.getTypeOfCube());
        cv.put(USER_NAME, timeModel.getNameofUser());

        final long insert = db.insert(TIMES_TABLE, null, cv);
        db.close();
        return insert != -1; //Returns a boolean value corresponding to whether the user has been added to the database
    }


    public int getId(String time) { //Gets the primary key ID corresponding to the time in the parameter
        int ID = 1;
        SQLiteDatabase db = this.getReadableDatabase();
        String SQLString = "SELECT ID FROM " + TIMES_TABLE + " WHERE " + TIME_SOLVE + " = '" + time + "'";

        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) { //If there is a result
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

    public String getTimeID(int i) { //Gets the date stored from the table with corresponding ID
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

    public String getShuffle(int i) { //Gets the shuffle stored from the table with corresponding ID
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

    public String getTypeOfCube(int i) { //Gets the type of cube stored from the table with corresponding ID
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

    public String getSolvedTime(int i) { //Gets the solved time stored from the table with corresponding ID
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


    public boolean DeleteTime(String time) { //Deletes the element in the databse where the time is equal to the parameter
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TIMES_TABLE + " WHERE " + TIME_SOLVE + " = '" + time + "'");
        db.close();
        return true;
    }

    public List<TimeModel> get3x3Times(String AccountName) { //Gets the times of 3x3 returned as an array of time models.
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(DatabaseTimes);
        List<TimeModel> returnList = new ArrayList<>();
        //assert account != null;
        String SQLString = "SELECT * FROM " + TIMES_TABLE + " WHERE " + TYPE_OF_CUBE + " = '3x3' AND " + USER_NAME + " = '" + AccountName + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            do {
                String TimeSolved = cursor.getString(1);
                String DateTime = cursor.getString(2);
                String Shuffle = cursor.getString(3);
                String TypeOfCube = cursor.getString(4);
                String UserName = cursor.getString(5);
                TimeModel newTime = new TimeModel(TimeSolved, DateTime, Shuffle, TypeOfCube, UserName);
                returnList.add(newTime);

            } while (cursor.moveToNext()); //Keep repeating while the cursor can move to the next element from the SQL statement
        }
        cursor.close();
        db.close();
        return returnList;
    }

    public List<TimeModel> get2x2Times(String AccountName) { //Same but 2x2
        List<TimeModel> returnList = new ArrayList<>();
        String SQLString = "SELECT * FROM " + TIMES_TABLE + " WHERE " + TYPE_OF_CUBE + " = '2x2' AND " + USER_NAME + " = '" + AccountName + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            do {
                String TimeSolved = cursor.getString(1);
                String DateTime = cursor.getString(2);
                String Shuffle = cursor.getString(3);
                String TypeOfCube = cursor.getString(4);
                String UserName = cursor.getString(5);
                TimeModel newTime = new TimeModel(TimeSolved, DateTime, Shuffle, TypeOfCube, UserName);
                returnList.add(newTime);

            } while (cursor.moveToNext()); //Keep repeating while the cursor can move to the next element from the SQL statement
        }
        cursor.close();
        db.close();
        return returnList;
    }

    public List<TimeModel> get4x4Times(String AccountName) { //Same but 4x4
        List<TimeModel> returnList = new ArrayList<>();
        String SQLString = "SELECT * FROM " + TIMES_TABLE + " WHERE " + TYPE_OF_CUBE + " = '4x4' AND " + USER_NAME + " = '" + AccountName + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            do {
                String TimeSolved = cursor.getString(1);
                String DateTime = cursor.getString(2);
                String Shuffle = cursor.getString(3);
                String TypeOfCube = cursor.getString(4);
                String UserName = cursor.getString(5);
                TimeModel newTime = new TimeModel(TimeSolved, DateTime, Shuffle, TypeOfCube, UserName);
                returnList.add(newTime);

            } while (cursor.moveToNext()); //Keep repeating while the cursor can move to the next element from the SQL statement
        }
        cursor.close();
        db.close();
        return returnList;
    }

    public String getAllTimes3x3() throws ParseException { //Returns a string showing the cube average, best and worst times
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
                //Checks if the return list is empty
                if (!isEmpty) {
                    //If empty, then format it like this so that there is a space in between
                    returnList.append(" ").append(TimeSolved);
                } else {
                    returnList.append(TimeSolved);
                }
                isEmpty = false;

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if (!returnList.toString().isEmpty()) { //run if the list isnt empty
            String[] split = returnList.toString().split(" "); //Splits the string into a string array via treating each " " as a seperate element
            long sum = 0L;
            SimpleDateFormat sdf = new SimpleDateFormat("m:ss:SSS", Locale.ENGLISH); // Java method of Date formatting
            // m:ss:SSS referring to Minutes, Seconds, Milliseconds

            for (String s : split) {
                sum += Objects.requireNonNull(sdf.parse(s)).getTime(); //parsing into correct format
            }

            Date avgDate = new Date((sum / split.length)); //Averages the whole times so for exmaple
            //A time of 2s and 4s would return 3s as the average time

            //Required Android 7.0 Nougat to run this routine
            String fastest = shortestTime.stream().min(Comparator.naturalOrder()).get(); //Selects fastest time from the solves
            String longest = shortestTime.stream().max(Comparator.naturalOrder()).get(); //Selects longest time from the solves
            return sdf.format(avgDate) + "\nBest time: " + fastest + "\nWorst time: " + longest;
        }
        return "No Time set";
    }

    public String getAllTimes2x2() throws ParseException { //Same but 2x2
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

    public String getAllTimes4x4() throws ParseException { //Same but 4x4
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

    public long getTotalSolves() { //Returns a long type of total entries in the database
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TIMES_TABLE);
        db.close();
        return count;
    }

    public int getTotalSolves3x3() { //Returns only total solves where the cube type is 3x3
        int solves3x3 = 0;
        String SQLString = "SELECT * FROM " + TIMES_TABLE + " WHERE " + TYPE_OF_CUBE + " = '3x3'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLString, null);

        if (cursor.moveToFirst()) {
            do {
                solves3x3 += 1;
            } while (cursor.moveToNext());

        }
        db.close();
        cursor.close();
        return solves3x3;
    }

    public int getTotalSolves4x4() {//Returns only total solves where the cube type is 4x4
        int solves3x3 = 0;
        String SQLString = "SELECT * FROM " + TIMES_TABLE + " WHERE " + TYPE_OF_CUBE + " = '4x4'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLString, null);

        if (cursor.moveToFirst()) {
            do {
                solves3x3 += 1;
            } while (cursor.moveToNext());

        }
        db.close();
        cursor.close();
        return solves3x3;
    }

    public int getTotalSolves2x2() { //Returns only total solves where the cube type is 2x2
        int solves3x3 = 0;
        String SQLString = "SELECT * FROM " + TIMES_TABLE + " WHERE " + TYPE_OF_CUBE + " = '2x2'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            do {
                solves3x3 += 1;
            } while (cursor.moveToNext());

        }
        db.close();
        cursor.close();
        return solves3x3;
    }


}
