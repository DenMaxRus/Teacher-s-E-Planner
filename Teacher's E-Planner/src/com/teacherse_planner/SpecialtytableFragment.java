package com.teacherse_planner;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.teacherse_planner.DBHelper.TABLES;
import com.teacherse_planner.DBHelper.TABLES.HOMEREADING;
import com.teacherse_planner.DBHelper.TABLES.SPECIALTY;
import com.teacherse_planner.DBHelper.TABLES.SPECIALTY_CLASSES;
import com.teacherse_planner.DBHelper.TABLES.SPECIALTY_CLASSES_DATE;
import com.teacherse_planner.DBHelper.TABLES.STUDENT;
import com.teacherse_planner.MainActivity.DialogBuilder;
import com.teacherse_planner.MainActivity.DialogBuilder.IdDialog;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
/** Окно списка группы */
public class SpecialtytableFragment extends Fragment implements MainActivity.DialogBuilder.DialogCallbacks {
	/** Заголовок */
	private String mTitle;
	public String getTitle() {
		return mTitle;
	}
	private LinearLayout mSpecialtytableLayout;
	/** Список студентов */
	private ListView mStudentList;
	/** Список уроков */
	private GridLayout mSpecialtyLessonsGrid;
	/** Сетка оценок */
	private GridLayout mSpecialtytableGrid;

	private DBHelper mdbHelper;
	private long mCurrentSpecialityId;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Добавить actionbar
		super.onCreate(savedInstanceState);
		// Вытаскиеваем текущую группу из сохраненного состояния или переданного
		mCurrentSpecialityId = getArguments() == null ? 2 : getArguments().getLong("mCurrentSpecialityId");
		// TODO Заменить - один и тот же объект во всех классах.
		mTitle = "Группа \"" + (getArguments() == null ? "NULL" : getArguments().getString("SpecialtyName"))+"\"";
		getActivity().setTitle(mTitle);
		mdbHelper = new DBHelper(getActivity());
		setHasOptionsMenu(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mSpecialtytableLayout = (LinearLayout) inflater.inflate(R.layout.fragment_specialty_table, container, false);
		
		mStudentList = (ListView) mSpecialtytableLayout.findViewById(R.id.student_list);
		TextView StudentListHeader = new TextView(getActivity());
		StudentListHeader.setText(R.string.student_fio);
		mStudentList.addHeaderView(StudentListHeader, null, false);
		mStudentList.setHeaderDividersEnabled(true);
		TextView StudentListFooter = new TextView(getActivity());
		StudentListFooter.setText(R.string.add_student);
		mStudentList.addFooterView(StudentListFooter, null, false);
		mStudentList.setFooterDividersEnabled(true);
		mStudentList.setAdapter(
				new SimpleCursorAdapter(
						getActivity(),
						R.layout.timetable_grid_item_2,
						mdbHelper.getAllStudentsFromSpecialty(String.valueOf(mCurrentSpecialityId)),
						new String[]{STUDENT.NAME},
						new int[]{R.id.text1},
						0));

		mSpecialtytableGrid = (GridLayout) mSpecialtytableLayout.findViewById(R.id.specialtytable_grid);
		
		mSpecialtyLessonsGrid = (GridLayout) mSpecialtytableLayout.findViewById(R.id.specialty_lessons_grid);
		
		/*mSpecialtyLessonsGrid.setAdapter(
				new SimpleCursorAdapter(
						getActivity(),
						R.layout.student_grid_item,
						null,
						new String[]{SPECIALTY_CLASSES.DATE},
						new int[]{R.id.text1},
						0){
					@Override
					public void bindView(View view, Context context,
							Cursor cursor) {
						// TODO Auto-generated method stub
						super.bindView(view, context, cursor);
						Calendar calendar = Calendar.getInstance();
						Cursor c = getCursor();
						calendar.setTimeInMillis(c.getLong(c.getColumnIndex(SPECIALTY_CLASSES.DATE)));
					}
			
		});*/
		
		refillSpecialtytableGrid();
		
		return mSpecialtytableLayout;
	}
	public void refillSpecialtytableGrid(){
		mSpecialtyLessonsGrid.removeAllViews();
		mSpecialtytableGrid.removeAllViews();
		LayoutInflater inflater = getActivity().getLayoutInflater();
		SQLiteDatabase db = mdbHelper.getReadableDatabase();
		Cursor lessons = db.query(
				TABLES.SPECIALTY_CLASSES_DATE,
				new String[]{SPECIALTY_CLASSES_DATE.ID, SPECIALTY_CLASSES_DATE.DATE},
				SPECIALTY_CLASSES_DATE.SPECIALTY_ID+"=?",
				new String[]{String.valueOf(mCurrentSpecialityId)},
				null, null, null);
		OnClickListener onLessonClkLst = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		};
		Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
		// Добавление дат занятий первой строкой
		for(int i=0; i <lessons.getCount(); ++i){
			lessons.moveToPosition(i);
			
			long dateInmSec = lessons.getLong(lessons.getColumnIndex(SPECIALTY_CLASSES.DATE));
			calendar.setTimeInMillis(dateInmSec);
			String date = DateFormat.format("dd.MM", calendar).toString();
			
			GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
			lp.columnSpec = GridLayout.spec(i);
			lp.rowSpec = GridLayout.spec(0);
			lp.setMargins(0, 0, 1, 1);
			
			TextView lessonView = (TextView) inflater.inflate(R.layout.pair_time_list_item_1, mSpecialtyLessonsGrid, false);
			lessonView.setText(date);
			lessonView.setLayoutParams(lp);
			
			mSpecialtyLessonsGrid.addView(lessonView);
		}
		// Установка кнопки "добавить занятие" последней
		GridLayout.LayoutParams blp = new GridLayout.LayoutParams();
		blp.columnSpec = GridLayout.spec(lessons.getCount());
		blp.rowSpec = GridLayout.spec(0);
		
		Button AddClassButton = new Button(getActivity());
		AddClassButton.setLayoutParams(blp);
		AddClassButton.setText(R.string.add_lesson);
		AddClassButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// Добавляем новое занятие
				// TODO добавить передаваемые параметры
				Bundle dialogInfo = new Bundle();
				dialogInfo.putString("idDialog", DialogBuilder.IdDialog.ADD_LESSON_DATE.toString());
				dialogInfo.putLong("mCurrentSpecialityId", mCurrentSpecialityId);
				MainActivity.DialogBuilder.newInstance(getActivity(), dialogInfo).show(getFragmentManager(), DialogBuilder.IdDialog.ADD_LESSON_DATE.toString());
			}
		});
		mSpecialtyLessonsGrid.addView(AddClassButton);
		
		Cursor studentListCursor = ((SimpleCursorAdapter) ((HeaderViewListAdapter) mStudentList.getAdapter()).getWrappedAdapter()).getCursor();
		for(int i=0; i<studentListCursor.getCount(); ++i){			
			studentListCursor.moveToPosition(i);
			String studentId = studentListCursor.getString(studentListCursor.getColumnIndex(STUDENT.ID));
			lessons.moveToFirst();
			for(int l=0; l<lessons.getCount(); ++l){
				LinearLayout view = new LinearLayout(getActivity());
				lessons.moveToPosition(l);
				String date = lessons.getString(studentListCursor.getColumnIndex(SPECIALTY_CLASSES.DATE));
				Cursor allClasses = db.query(
					TABLES.SPECIALTY_CLASSES,
					new String[]{SPECIALTY_CLASSES.CLASS_ID,SPECIALTY_CLASSES.CLASS_TYPE},
					SPECIALTY_CLASSES.STUDENT_ID+"=? AND "+SPECIALTY_CLASSES.DATE+"=?",
					new String[]{studentId,date},
					null,
					null,
					SPECIALTY_CLASSES.DATE);
				for(int c=0; c<allClasses.getCount(); ++c){
					allClasses.moveToPosition(c);
					String classType = allClasses.getString(allClasses.getColumnIndex(SPECIALTY_CLASSES.CLASS_TYPE));
					String classId = allClasses.getString(allClasses.getColumnIndex(SPECIALTY_CLASSES.CLASS_ID));
					Cursor currentClass = db.query(
							classType,
							null,
							"_id=?",
							new String[]{classId},
							null, null, null);
					currentClass.moveToFirst();
					switch(classType){
					case TABLES.HOMEWORK_RESULT:
						break;
					case TABLES.HOMEREADING:
						
						int words = currentClass.getInt(currentClass.getColumnIndex(HOMEREADING.WORDS));
						int symbols = currentClass.getInt(currentClass.getColumnIndex(HOMEREADING.SYMBOLS));
						int retelling = currentClass.getInt(currentClass.getColumnIndex(HOMEREADING.RETELLING));
						int translating = currentClass.getInt(currentClass.getColumnIndex(HOMEREADING.TRANSLATING));
						
						View homereadingView = inflater.inflate(R.layout.specialty_class_homereading_item, view, false);
						LinearLayout borders = (LinearLayout) homereadingView.findViewById(R.id.borders);
						borders.setPadding(symbols > 0 ? 1 : 0, translating, retelling, words > 0 ? 1 : 0);
						view.addView(homereadingView);
						break;
					}
					mSpecialtyLessonsGrid.addView(view);
				}
			}
		}
		/*SimpleCursorAdapter SpecialtyLessonsGridAdapter = (SimpleCursorAdapter)(mSpecialtyLessonsGrid.getAdapter());		
		SpecialtyLessonsGridAdapter
				.changeCursor(
						db.query(
							true,
							TABLES.SPECIALTY_CLASSES,
							new String[]{SPECIALTY_CLASSES.ID, SPECIALTY_CLASSES.DATE},
							SPECIALTY.ID+"=?",
							new String[]{String.valueOf(mCurrentSpecialityId)},
							null,
							null,
							SPECIALTY_CLASSES.DATE,
							null));*/
		db.close();
	}
	@Override
	public void onDialogDismiss(IdDialog dialogId) {
		switch (dialogId) {
		case ADD_LESSON_DATE:
			refillSpecialtytableGrid();
			break;

		default:
			break;
		}
		
	}
}
