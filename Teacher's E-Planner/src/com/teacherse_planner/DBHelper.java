package com.teacherse_planner;

import com.teacherse_planner.DBHelper.TABLES.HOMEREADING;
import com.teacherse_planner.DBHelper.TABLES.HOMEWORK;
import com.teacherse_planner.DBHelper.TABLES.HOMEWORK_RESULT;
import com.teacherse_planner.DBHelper.TABLES.NA;
import com.teacherse_planner.DBHelper.TABLES.SPECIALTY;
import com.teacherse_planner.DBHelper.TABLES.SPECIALTY_CLASSES;
import com.teacherse_planner.DBHelper.TABLES.SPECIALTY_CLASSES_DATE;
import com.teacherse_planner.DBHelper.TABLES.STUDENT;
import com.teacherse_planner.DBHelper.TABLES.TIMETABLE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Класс для работы с БД.
 * Для использования имен таблиц подключайте com.teacherse_planner.DBHelper.TABLES;
 * Для использования полей подключайте com.teacherse_planner.DBHelper.TABLES.%TABLENAME%; */
public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME="teachereplanner.db";
	
	/*public static final String TIMETABLE="timetable", SPECIALTY="specialty", STUDENT="student"
	private static final String TIMETABLE_ID="_id",TIMETABLE_SPECIALTY_ID="specialty_id",TIMETABLE_CLASSROOM="classroom",TIMETABLE_WEEK="week",TIMETABLE_COLOR="color";
	private static final String SPECIALTY_ID="_id",SPECIALTY_NAME="name";
	private static final String STUDENT_ID="_id",STUDENT_NAME="name";*/
	
	public static abstract class TABLES {
		
		public final static String TIMETABLE = "timetable", SPECIALTY = "specialty", STUDENT = "student",SPECIALTY_CLASSES_DATE = "specialty_classes_date", SPECIALTY_CLASSES = "specialty_classes", HOMEREADING = "homereading", HOMEWORK_RESULT = "homework_result", HOMEWORK = "homework", NA = "na";
		
		/** Таблица расписания. Полные пути вида %TABLE%.%FIELD% можно получить через f%FIELD% переменные. */
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
				fCOLOR = TIMETABLE + "." + COLOR,
			
				aID = "timetable_table_id";
			private TIMETABLE(){}
		};
		/** Таблица групп. Полные пути вида %TABLE%.%FIELD% можно получить через f%FIELD% переменные. */
		public static abstract class SPECIALTY {
			public final static String
				ID = "_id",
				NAME = "name",
				
				fID = SPECIALTY + "." + ID,
				fNAME = SPECIALTY + "." + NAME,

				aID = "specialty_table_id";
			
			private SPECIALTY(){}
		};
		/** Таблица студентов. Полные пути вида %TABLE%.%FIELD% можно получить через f%FIELD% переменные. */
		public static abstract class STUDENT {
			public final static String
				ID = "_id",
				NAME = "name",
				SPECIALTY_ID = "specialty_id",
				TELEPHONE = "telephone",
				EMAIL = "email",
				NOTE = "note",
				
				fID = STUDENT + "." + ID,
				fNAME = STUDENT + "." + NAME,
				fSPECIALTY_ID = STUDENT + "." + SPECIALTY_ID,
				fTELEPHONE = STUDENT + "." + TELEPHONE,
				fEMAIL = STUDENT + "." + EMAIL,
				fNOTE = STUDENT + "." + NOTE,
				
				aID = "student_table_id";
			private STUDENT(){}
		};
		/** Таблица домашних заданий. Полные пути вида %TABLE%.%FIELD% можно получить через f%FIELD% переменные. */
		public static abstract class HOMEWORK {
			public final static String
				ID = "_id",
				HOMEWORK_TEXT = "homework_text",
				
				fID = HOMEWORK + "." + ID,
				fHOMEWORK_TEXT = HOMEWORK + "." + HOMEWORK_TEXT;
			private HOMEWORK(){}
		};
		/** Таблица оценок за домашнюю работу. Полные пути вида %TABLE%.%FIELD% можно получить через f%FIELD% переменные. */
		public static abstract class HOMEWORK_RESULT {
			public final static String
				ID = "_id",
				MARK = "mark",
				NOTE = "note",
				
				fID = HOMEWORK_RESULT + "." + ID,
				fMARK = HOMEWORK_RESULT + "." + MARK,
				fNOTE = HOMEWORK_RESULT + "." + NOTE;
			private HOMEWORK_RESULT(){}
		};
		/** Таблица резульатов домашнего чтения. Полные пути вида %TABLE%.%FIELD% можно получить через f%FIELD% переменные. */
		public static abstract class HOMEREADING {
			public final static String
				ID = "_id",
				SYMBOLS = "symbols",
				WORDS = "words",
				RETELLING = "retelling",
				TRANSLATING = "translating",
				
				fID = HOMEREADING + "." + ID,
				fSYMBOLS = HOMEREADING + "." + SYMBOLS,
				fWORDS = HOMEREADING + "." + WORDS,
				fRETELLING = HOMEREADING + "." + RETELLING,
				fTRANSLATING = HOMEREADING + "." + TRANSLATING;
			private HOMEREADING(){}
		};
		/** Таблица дат занятий. Полные пути вида %TABLE%.%FIELD% можно получить через f%FIELD% переменные. */
		public static abstract class SPECIALTY_CLASSES_DATE {
			public final static String
				ID = "_id",
				DATE ="date",
				SPECIALTY_ID = "specialty_id",
				
				fID = SPECIALTY_CLASSES_DATE + "." + ID,
				fDATE = SPECIALTY_CLASSES_DATE + "." + DATE,
				fSPECIALTY_ID = SPECIALTY_CLASSES_DATE + "." + SPECIALTY_ID;
			private SPECIALTY_CLASSES_DATE(){}
		};
		/** Таблица оценок, посещаемости и т.д. Полные пути вида %TABLE%.%FIELD% можно получить через f%FIELD% переменные. */
		public static abstract class SPECIALTY_CLASSES {
			public final static String
				ID = "_id",
				DATE ="date",
				STUDENT_ID = "student_id",
				CLASS_TYPE = "class_type",
				CLASS_ID = "сlass_id",
				
				fID = SPECIALTY_CLASSES + "." + ID,
				fDATE = SPECIALTY_CLASSES + "." + DATE,
				fSTUDENT_ID = SPECIALTY_CLASSES + "." + STUDENT_ID,
				fCLASS_TYPE = SPECIALTY_CLASSES + "." + CLASS_TYPE,
				fCLASS_ID = SPECIALTY_CLASSES + "." + CLASS_ID;
			private SPECIALTY_CLASSES(){}
		};
		private TABLES(){}
		/** Таблица отсутствия. Полные пути вида %TABLE%.%FIELD% можно получить через f%FIELD% переменные. */
		public static abstract class NA {
			public final static String
				ID = "_id",
				DATE ="date",
				
				fID = NA + "." + ID,
				fDATE = NA + "." + DATE;
			private NA(){}
		};
	};
	
	private static final String CREATE_SPECIALITY =
	"create table "+TABLES.SPECIALTY+" ("
		+SPECIALTY.ID+" integer primary key autoincrement, "
		+SPECIALTY.NAME+" text unique "
	+");";
	private static final String CREATE_STUDENT =
	"create table "+TABLES.STUDENT+" ("
		+STUDENT.ID+" integer primary key autoincrement, "
		+STUDENT.NAME+" text not null, "
		+STUDENT.SPECIALTY_ID+" integer default 1, "
		+STUDENT.TELEPHONE+" integer default null, "
		+STUDENT.EMAIL+" text default null, "
		+STUDENT.NOTE+" text default null, "
		+"foreign key("+TIMETABLE.SPECIALTY_ID+") references "+TABLES.SPECIALTY+" ("+SPECIALTY.ID+")"
	+");";
	private static final String CREATE_TIMETABLE =
	"create table "+TABLES.TIMETABLE+" ("
		+TIMETABLE.ID+" integer, "
		+TIMETABLE.SPECIALTY_ID+" integer default 1, "
		+TIMETABLE.CLASSROOM+" text default null, "
		+TIMETABLE.WEEK+" integer, "
		+TIMETABLE.COLOR+" text default 'none', "
		+"primary key("+TIMETABLE.ID+", "+TIMETABLE.WEEK+"), "
		+"foreign key("+TIMETABLE.SPECIALTY_ID+") references "+TABLES.SPECIALTY+" ("+SPECIALTY.ID+")"
	+");";
	private static final String CREATE_HOMEWORK =
	"create table "+TABLES.HOMEWORK+" ("
		+HOMEWORK.ID+" integer primary key, "
		+HOMEWORK.HOMEWORK_TEXT+" text default null"
	+");";
	private static final String CREATE_HOMEWORK_RESULT =
	"create table "+TABLES.HOMEWORK_RESULT+" ("
		+HOMEWORK_RESULT.ID+" integer primary key autoincrement, "
		+HOMEWORK_RESULT.MARK+" integer check("+HOMEWORK_RESULT.MARK+">0), "
		+HOMEWORK_RESULT.NOTE+" text default null"
	+");";
	private static final String CREATE_HOMEREADING =
	"create table "+TABLES.HOMEREADING+" ("
		+HOMEREADING.ID+" integer primary key autoincrement, "
		+HOMEREADING.SYMBOLS+" integer check("+HOMEREADING.SYMBOLS+">=0), "
		+HOMEREADING.WORDS+" integer check("+HOMEREADING.WORDS+">=0), "
		+HOMEREADING.RETELLING+" integer check("+HOMEREADING.RETELLING+"=0 or "+HOMEREADING.RETELLING+"=1), "
		+HOMEREADING.TRANSLATING+" integer check("+HOMEREADING.TRANSLATING+"=0 or "+HOMEREADING.TRANSLATING+"=1)"
	+");";
	private static final String CREATE_SPECIALTY_CLASSES_DATE =
	"create table "+TABLES.SPECIALTY_CLASSES_DATE+" ("
		+SPECIALTY_CLASSES_DATE.ID+" integer primary key autoincrement, "
		+SPECIALTY_CLASSES_DATE.DATE+" numeric, "
		+SPECIALTY_CLASSES_DATE.SPECIALTY_ID+" integer, "
		+"foreign key("+SPECIALTY_CLASSES_DATE.SPECIALTY_ID+") references "+TABLES.STUDENT+" ("+STUDENT.ID+"), "
		+"unique ("+SPECIALTY_CLASSES_DATE.ID+", "+SPECIALTY_CLASSES_DATE.DATE+") on conflict replace"
	+");";
	private static final String CREATE_SPECIALTY_CLASSES =
	"create table "+TABLES.SPECIALTY_CLASSES+" ("
		+SPECIALTY_CLASSES.ID+" integer primary key autoincrement, "
		+SPECIALTY_CLASSES.DATE+" numeric, "
		+SPECIALTY_CLASSES.STUDENT_ID+" integer, "
		+SPECIALTY_CLASSES.CLASS_TYPE+" text not null, "
		+SPECIALTY_CLASSES.CLASS_ID+" integer not null, "
		+"foreign key("+SPECIALTY_CLASSES.DATE+") references "+TABLES.SPECIALTY_CLASSES_DATE+" ("+SPECIALTY_CLASSES_DATE.DATE+"), "
		+"foreign key("+SPECIALTY_CLASSES.STUDENT_ID+") references "+TABLES.STUDENT+" ("+STUDENT.ID+")"
	+");";
	private static final String CREATE_NA =
	"create table "+TABLES.NA+" ("
		+NA.ID+" integer primary key autoincrement, "
		+NA.DATE+" numeric, "
		+"foreign key("+SPECIALTY_CLASSES.DATE+") references "+TABLES.SPECIALTY_CLASSES_DATE+" ("+SPECIALTY_CLASSES_DATE.DATE+")"
	+");";
	/** Получить всех студентов */
	public Cursor getAllStudentsFromSpecialty(String specialty_id){
		return getReadableDatabase().query(
				TABLES.STUDENT+" JOIN "+TABLES.SPECIALTY+" ON "+STUDENT.fSPECIALTY_ID+"="+SPECIALTY.fID,
				new String[]{STUDENT.fID, STUDENT.fSPECIALTY_ID, STUDENT.fNAME, STUDENT.TELEPHONE, STUDENT.EMAIL, STUDENT.NOTE},
				SPECIALTY.fID+"=?",
				new String[]{specialty_id}, null, null, null);
	}
	public DBHelper(Context context) {
		super(context, DB_NAME, null, 1);
		// TODO Добавить версию для сравнения
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Создаем таблицы
		db.execSQL(CREATE_SPECIALITY);
		db.execSQL(CREATE_STUDENT);
		db.execSQL(CREATE_TIMETABLE);
		db.execSQL(CREATE_HOMEWORK);
		db.execSQL(CREATE_HOMEWORK_RESULT);
		db.execSQL(CREATE_HOMEREADING);
		db.execSQL(CREATE_SPECIALTY_CLASSES_DATE);
		db.execSQL(CREATE_SPECIALTY_CLASSES);
		db.execSQL(CREATE_NA);
		
		ContentValues cv=new ContentValues();
		// Добавляем в группы пустое значение
		cv.put(SPECIALTY.NAME, "");
		db.insert(TABLES.SPECIALTY, null, cv);
		
		// TODO Убрать в дальнейшем - добавление стандартных групп в бд
		String[] Groups=new String[]{"ПИбд-21", "ПСбд-11", "ИВТАПбд-11", "Нбд-21", "БАбд-21", "СОбд-31", "МКбд-21", "СОд-41", "УПбд-21", "ПИбд-11"};
		for (String str : Groups) {
			cv.put(SPECIALTY.NAME, str);
			db.insert(TABLES.SPECIALTY, null, cv);
		}
		
		cv.clear();
		cv.put(STUDENT.SPECIALTY_ID, 2);
		cv.put(STUDENT.NAME, "Daniil Maximov");
		db.insert(TABLES.STUDENT, null, cv);

		Log.d("MyLog", "Timetable DB Created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
