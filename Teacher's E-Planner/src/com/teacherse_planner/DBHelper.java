package com.teacherse_planner;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {
	
	public static final String DB_NAME="teachereplanner.db";
	public static final String TIMETABLE="timetable",TIMETABLE_ID="_id",TIMETABLE_SPECIALTY_ID="specialty_id",TIMETABLE_CLASSROOM="classroom",TIMETABLE_WEEK="week",TIMETABLE_COLOR="color";
	public static final String SPECIALTY="specialty",SPECIALTY_ID="_id",SPECIALTY_NAME="name";
	public static final String STUDENT="student",STUDENT_ID="_id",STUDENT_NAME="name";
	private static final String CREATE_SPECIALITY=
	"create table "+SPECIALTY+" ("
		+SPECIALTY_ID+" integer primary key autoincrement, "
		+SPECIALTY_NAME+" text unique "
	+");";
	private static final String CREATE_STUDENT=
	"create table "+STUDENT+" ("
		+STUDENT_ID+" integer primary key autoincrement, "
		+STUDENT_NAME+" text not null "
	+");";
	private static final String CREATE_TIMETABLE=
	"create table "+TIMETABLE+" ("
		+TIMETABLE_ID+" integer primary key autoincrement, "
		+TIMETABLE_SPECIALTY_ID+" integer default 1, "
		+TIMETABLE_CLASSROOM+" text default null, "
		+TIMETABLE_WEEK+" integer not null, "
		+TIMETABLE_COLOR+" text default 'none', "
		+"foreign key("+TIMETABLE_SPECIALTY_ID+") references "+SPECIALTY+" ("+SPECIALTY_ID+")"
	+");";
	public DBHelper(Context context) {
		super(context, DB_NAME, null, 1);
		// TODO �������� ������ ��� ���������
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO �������� ����� �������
		// ������� �������
		db.execSQL(CREATE_SPECIALITY);
		db.execSQL(CREATE_STUDENT);
		db.execSQL(CREATE_TIMETABLE);
		
		ContentValues cv=new ContentValues();
		// ��������� � ������ ������ ��������
		cv.put(SPECIALTY_NAME, "");
		db.insert(SPECIALTY, null, cv);
		cv.clear();
		// ��������� 84 ������ ������ � ���������� (_id: 1-42 - ������ ������, 43-84 - ������ ������)
		cv.put(TIMETABLE_WEEK, 1);
		for(int i=0;i<42;++i)
			db.insert(TIMETABLE, null, cv);
		cv.put(TIMETABLE_WEEK, 2);
		for(int i=0;i<42;++i)
			db.insert(TIMETABLE, null, cv);
		cv.clear();
		
		// TODO ������ � ���������� - ���������� ����������� ����� � ��
		String[] Groups=new String[]{"����-21","����-11","�������-11","���-21","����-21","����-31","����-21","���-41","����-21","����-11"};
		for (String str : Groups) {
			cv.put(SPECIALTY_NAME, str);
			db.insert(SPECIALTY, null, cv);
		}
		
		Log.d("MyLog", "Timetable DB Created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
