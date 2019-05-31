package com.example.pjt_student;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//dbms 관리적인 코드 추상화
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        //파일명은 studentdb로 고정. 하나의 파일에 여러 테이블 추가.
        //상위클래스에 db version 정보가 들어간다.
        super(context, "studentdb", null, 5);
    }

    //app install 후에 helper 이용되는 최초에 한번
    @Override
    public void onCreate(SQLiteDatabase db) {
        String studentSql = "create table tb_student(" +
                "_id integer primary key autoincrement, " +
                "name not null," +
                "email," +
                "phone," +
                "photo," +
                "memo)";

        String scoreSql = "create table tb_score (" +
                "_id integer primary key autoincrement, " +
                "student_id not null," +
                "date," +
                "score)";

        db.execSQL(studentSql);
        db.execSQL(scoreSql);
    }

    //상위 클래스에 전달하는 db version 정보가 변경될때 마다
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table tb_student");
        db.execSQL("drop table tb_score");

        onCreate(db);
    }

}
