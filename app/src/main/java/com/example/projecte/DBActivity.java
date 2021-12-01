package com.example.projecte;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;


public abstract class DBActivity extends RESTactivity {

    protected  interface OnQuerySuccess{
        public  void OnSuccess();
    }

    protected  interface OnSelectSuccess{
        public  void OnElementSelected(
                String ID, Double A, Double B, Double C,String Result
        );
    }

   /* protected boolean matchString(String string_, String regexp){
        final String regex = regexp;//"[A-Z]+\\w{0,}\\s[A-Z]+\\w{0,}";
        final String string = string_;

        final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.UNICODE_CASE);
        final Matcher matcher = pattern.matcher(string);

        while (matcher.find()){
            return true;
        }
        return false;
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    protected  void SelectSQL(String SelectQ,
                              String[] args,
                              OnSelectSuccess success)
            throws  Exception
    {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getFilesDir().getPath() + "/CALDATABASE.db", null);
        Cursor cursor = db.rawQuery(SelectQ, args);
        while (cursor.moveToNext()){
            @SuppressLint("Range") String ID = cursor.getString(cursor.getColumnIndex("ID"));
            @SuppressLint("Range") Double A = cursor.getDouble(cursor.getColumnIndex("A"));
            @SuppressLint("Range") Double B = cursor.getDouble(cursor.getColumnIndex("B"));
            @SuppressLint("Range") Double C = cursor.getDouble(cursor.getColumnIndex("C"));
            @SuppressLint("Range") String Result = cursor.getString(cursor.getColumnIndex("Result"));
            success.OnElementSelected(ID,A,B,C,Result);
        }
        db.close();
    }
    protected  void  ExecSQL(String SQL, Object[] args, OnQuerySuccess success)
            throws  Exception
    {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getFilesDir().getPath() + "/CALDATABASE.db", null);
        if(args!=null)
            db.execSQL(SQL, args);
        else
            db.execSQL(SQL);
        db.close();
        success.OnSuccess();
    }

    protected  void  initDB() throws Exception{
        ExecSQL(
                "CREATE TABLE if not exists CALDATABASE( " +
                        "ID integer PRIMARY KEY AUTOINCREMENT, " +
                        "A decimal not null, " +
                        "B decimal not null, " +
                        "C decimal not null, " +
                        "Result text " +  ")",
                null,
                ()-> Toast.makeText(getApplicationContext(),"DB Init Successful. ",Toast.LENGTH_LONG).show()
        );
    }


}
