package com.teacherse_planner;

import com.teacherse_planner.DBHelper.TABLES.SPECIALTY;
import com.teacherse_planner.DBHelper.TABLES.STUDENT;
import com.teacherse_planner.DBHelper.TABLES.TIMETABLE;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * ����� ��� ������ � ��.
 * ��� ������������� ���� ������ ����������� com.teacherse_planner.DBHelper.TABLES;
 * ��� ������������� ����� ����������� com.teacherse_planner.DBHelper.TABLES.%TABLENAME%; */
public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME="teachereplanner.db";
	
	/*public static final String TIMETABLE="timetable", SPECIALTY="specialty", STUDENT="student"
	private static final String TIMETABLE_ID="_id",TIMETABLE_SPECIALTY_ID="specialty_id",TIMETABLE_CLASSROOM="classroom",TIMETABLE_WEEK="week",TIMETABLE_COLOR="color";
	private static final String SPECIALTY_ID="_id",SPECIALTY_NAME="name";
	private static final String STUDENT_ID="_id",STUDENT_NAME="name";*/
	
	public static abstract class TABLES {
		public final static String TIMETABLE = "timetable", SPECIALTY = "specialty", STUDENT = "student";
		
		/** ������� ����������. ������ ���� ���� %TABLE%.%FIELD% ����� �������� ����� f%FIELD% ����������. */
		public static abstract class TIMETABLE {
			public final static String
				ID = "_id",
				SPECIALTY_ID = "specialty_id",
				CLASSROOM = "classroom",
				WEEK = "week",
				COLOR = "color",
				
				fID = TIMETABLE + "." + ID,
				fSPECIALTY_ID = TIMETABLE + "." + SPECIALTY_ID,
				fCLASSROOM = TIMETABLE + "." + CLASSROOM,
				fWEEK = TIMETABLE + "." + WEEK,
				fCOLOR = TIMETABLE + "." + COLOR;
			private TIMETABLE(){}
		};
		/** ������� �����. ������ ���� ���� %TABLE%.%FIELD% ����� �������� ����� f%FIELD% ����������. */
		public static abstract class SPECIALTY {
			public final static String
				ID = "_id",
				NAME = "name",
				
				fID = SPECIALTY + "." + ID,
				fNAME = SPECIALTY + "." + NAME;
			private SPECIALTY(){}
		};
		/** ������� ���������. ������ ���� ���� %TABLE%.%FIELD% ����� �������� ����� f%FIELD% ����������. */
		public static abstract class STUDENT {
			public final static String
				ID = "_id",
				NAME = "name",
				
				fID = STUDENT + "." + ID,
				fNAME = STUDENT + "." + NAME;
			private STUDENT(){}
		};
	};

	//;


	
	/*public enum TIMETABLE {
		ID(TIMETABLE + TIMETABLE_ID),
		SPECIALTY_ID(TIMETABLE + TIMETABLE_SPECIALTY_ID),
		CLASSROOM(TIMETABLE + TIMETABLE_CLASSROOM),
		WEEK(TIMETABLE + TIMETABLE_WEEK),
		COLOR(TIMETABLE + TIMETABLE_COLOR);
		private final String fieldName;
		private TIMETABLE(String fieldName){
			this.fieldName = fieldName;
		}
		@Override
		public String toString() {
			return fieldName;
		}
	}
	public enum SPECIALTY {
		ID(SPECIALTY + SPECIALTY_ID),
		CLASSROOM(TIMETABLE + TIMETABLE_CLASSROOM),
		WEEK(TIMETABLE + TIMETABLE_WEEK),
		COLOR(TIMETABLE + TIMETABLE_COLOR);
		private String fieldName; 
		private SPECIALTY(String fieldName){
			this.fieldName = fieldName;
		}
		@Override
		public String toString() {
			return fieldName;
		}
	}
	public enum STUDENT {
		ID(STUDENT + STUDENT_ID),
		NAME(STUDENT + STUDENT_NAME);
		private String fieldName; 
		private STUDENT(String fieldName){
			this.fieldName = fieldName;
		}
		@Override
		public String toString() {
			return fieldName;
		}
	}*/
	
	private static final String CREATE_SPECIALITY=
	"create table "+TABLES.SPECIALTY+" ("
		+SPECIALTY.ID+" integer primary key autoincrement, "
		+SPECIALTY.NAME+" text unique "
	+");";
	private static final String CREATE_STUDENT=
	"create table "+TABLES.STUDENT+" ("
		+STUDENT.ID+" integer primary key autoincrement, "
		+STUDENT.NAME+" text not null "
	+");";
	private static final String CREATE_TIMETABLE=
	"create table "+TABLES.TIMETABLE+" ("
		+TIMETABLE.ID+" integer primary key autoincrement, "
		+TIMETABLE.SPECIALTY_ID+" integer default 1, "
		+TIMETABLE.CLASSROOM+" text default null, "
		+TIMETABLE.WEEK+" integer not null, "
		+TIMETABLE.COLOR+" text default 'none', "
		+"foreign key("+TIMETABLE.SPECIALTY_ID+") references "+TABLES.SPECIALTY+" ("+SPECIALTY.ID+")"
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
		cv.put(SPECIALTY.NAME, "");
		db.insert(TABLES.SPECIALTY, null, cv);
		cv.clear();
		// ��������� 84 ������ ������ � ���������� (_id: 1-42 - ������ ������, 43-84 - ������ ������)
		cv.put(TIMETABLE.WEEK, 1);
		for(int i=0;i<42;++i)
			db.insert(TABLES.TIMETABLE, null, cv);
		cv.put(TIMETABLE.WEEK, 2);
		for(int i=0;i<42;++i)
			db.insert(TABLES.TIMETABLE, null, cv);
		cv.clear();
		
		// TODO ������ � ���������� - ���������� ����������� ����� � ��
		String[] Groups=new String[]{"����-21","����-11","�������-11","���-21","����-21","����-31","����-21","���-41","����-21","����-11"};
		for (String str : Groups) {
			cv.put(SPECIALTY.NAME, str);
			db.insert(TABLES.SPECIALTY, null, cv);
		}
		
		Log.d("MyLog", "Timetable DB Created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
