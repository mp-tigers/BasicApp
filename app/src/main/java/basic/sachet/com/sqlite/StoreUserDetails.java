package basic.sachet.com.sqlite;

import basic.sachet.com.beans.Institute;
import basic.sachet.com.beans.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StoreUserDetails extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "userDetails";

    public StoreUserDetails(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_USER_TAB = "CREATE TABLE USER_DTLS(USER_ID TEXT, USER_NAME TEXT NOT NULL,"+
                                    "USER_DOB DATE, USER_EMAIL TEXT, USER_PIC_URL TEXT, USER_ACCOUNT_ID TEXT NOT NULL,"+
                                    "USER_ACCOUNT_TYPE CHARACTER NOT NULL, INSTITUTE_NAME TEXT, INSTITUTE_CITY TEXT)";

        //String CREATE_INSTITUTE_TAB = "CREATE TABLE INSTITUTE_DTLS(INSTITUTE_NAME TEXT, CITY TEXT NOT NULL";

        db.execSQL(CREATE_USER_TAB);
     //   db.execSQL(CREATE_INSTITUTE_TAB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS USER_DTLS");
        db.execSQL("DROP TABLE IF EXISTS INSTITUTE_DTLS");

        // Create tables again
        onCreate(db);
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList <Institute> institutes = user.getInstituteList();
        //int i = 0;
        //while(i<user.getInstituteList().lastIndexOf())
        for(int i =0; i< institutes.size(); i++)
        {
            ContentValues values = new ContentValues();
            values.put("USER_NAME", user.getUserName());
            //values.put("USER_ID", user.getUserId());
            /* if(user.getDob() != null)
            values.put("USER_DOB",user.getDob().toString());*/
            values.put("USER_EMAIL",user.getEmail());
            values.put("USER_PIC_URL",user.getPicUrl());
            values.put("USER_ACCOUNT_ID",user.getAccountId());
            values.put("USER_ACCOUNT_TYPE", user.getAccountType());

            Institute institute = institutes.get(i);
            values.put("INSTITUTE_NAME", institute.getName());
            values.put("INSTITUTE_CITY", institute.getCity());

            // Inserting Row
            db.insert("USER_DTLS", null, values);

        }
        db.close(); // Closing database connection
    }
    // Getting single user details
  public  User getUserByAccountId(String id) {
      Log.d("Debug", "inside getUserByAccountId id :" +id);
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("USER_DTLS", new String[]{"USER_ID", "USER_NAME",
                        "USER_EMAIL", "USER_PIC_URL", "USER_ACCOUNT_ID", "USER_ACCOUNT_TYPE", "INSTITUTE_NAME", "INSTITUTE_CITY"}, "USER_ACCOUNT_ID = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);
      User user = null;
        if (cursor != null) {
            cursor.moveToFirst();

             user = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
            Institute institute = new Institute(cursor.getString(6), cursor.getString(7));
            Log.d("Debug", institute.toString());
            ArrayList<Institute> institutes = user.getInstituteList();
            institutes.add(institute);
            cursor.moveToNext();
            while (cursor != null) {
                institute = new Institute(cursor.getString(6), cursor.getString(7));
                Log.d("Debug", institute.toString());
                institutes.add(institute);
                Log.d("Debug", "moving cursor to next");
                if (cursor.isLast())
                      break;
                cursor.moveToNext();
            }
            Log.d("Debug", "Setting institutes list");
            user.setInstituteList(institutes);
        }
        // return contact
      Log.d("Debug", "Leaving getUserByAccountId");
        return user;
    }

}
