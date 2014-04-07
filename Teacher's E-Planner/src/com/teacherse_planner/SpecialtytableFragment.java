package com.teacherse_planner;

import java.util.Calendar;
import java.util.Locale;

import com.teacherse_planner.DBHelper.TABLES;
import com.teacherse_planner.DBHelper.TABLES.HOMEREADING;
import com.teacherse_planner.DBHelper.TABLES.HOMEWORK_RESULT;
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
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
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
	private long mCurrentSpecialtyId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Добавить actionbar
		super.onCreate(savedInstanceState);
		// Вытаскиеваем текущую группу из сохраненного состояния или переданного
		mCurrentSpecialtyId = getArguments() == null ? 2 : getArguments().getLong("mCurrentSpecialityId");
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
		
		FrameLayout StudentListHeader = (FrameLayout) inflater.inflate(R.layout.student_list_item_1, mStudentList, false);
		((TextView) StudentListHeader.findViewById(R.id.text1)).setText(R.string.student_fio);
		mStudentList.addHeaderView(StudentListHeader, null, false);
		mStudentList.setHeaderDividersEnabled(true);
		
		FrameLayout StudentListFooter = (FrameLayout) inflater.inflate(R.layout.student_list_item_1, mStudentList, false);
		StudentListFooter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Вызвать пустую карточку студента
				Bundle args = new Bundle();
				args.putLong("mCurrentSpecialtyId", mCurrentSpecialtyId);
				args.putString("type","add");
				StudentCardFragment SC = ((MainActivity) getActivity()).getStudentCardFragment();
				if(SC == null)
					SC = new StudentCardFragment();
				SC.setArguments(args);
				getFragmentManager().beginTransaction().replace(R.id.container, SC).addToBackStack(mTitle).commit();
			}
		});
		((TextView) StudentListFooter.findViewById(R.id.text1)).setText(R.string.add_student);
		mStudentList.addFooterView(StudentListFooter, null, false);
		mStudentList.setFooterDividersEnabled(true);
		mStudentList.setAdapter(
				new SimpleCursorAdapter(
						getActivity(),
						R.layout.student_list_item_1,
						null,
						new String[]{STUDENT.NAME},
						new int[]{R.id.text1},
						0));
		mStudentList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// Вызвать заполненную карточку студента
				
				
				Bundle args = new Bundle();
				args.putString("type", "edit");
				args.putLong("mCurrentStudentId", id);
				args.putLong("mCurrentSpecialtyId", mCurrentSpecialtyId);
				
				StudentCardFragment SC = ((MainActivity) getActivity()).getStudentCardFragment();
				if(SC == null)
					SC = new StudentCardFragment();
				SC.setArguments(args);
				getFragmentManager().beginTransaction().replace(R.id.container, SC).addToBackStack(mTitle).commit();
			}
		});
		refillStudentList();
		
		mSpecialtytableGrid = (GridLayout) mSpecialtytableLayout.findViewById(R.id.specialtytable_grid);

		mSpecialtyLessonsGrid = (GridLayout) mSpecialtytableLayout.findViewById(R.id.specialty_lessons_grid);
		
		refillSpecialtytableGrid();
		
		return mSpecialtytableLayout;
	}
	public void refillSpecialtytableGrid(){
		mSpecialtyLessonsGrid.removeAllViews();
		mSpecialtytableGrid.removeAllViews();
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		SQLiteDatabase db = mdbHelper.getReadableDatabase();
		// Все занесенные занятия текущей группы
		Cursor allLessons = mdbHelper.getAllLessonsOfSpecialty(String.valueOf(mCurrentSpecialtyId));
		
		OnClickListener onLessonClkLst = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Изменить домашнее задание
				Bundle dialogInfo = new Bundle();
				dialogInfo.putString("idDialog", DialogBuilder.IdDialog.ADD_HOMEWORK.toString());
				dialogInfo.putInt("homeworkId", v.getId());
				MainActivity.DialogBuilder.newInstance(getActivity(), dialogInfo).show(getFragmentManager(), DialogBuilder.IdDialog.ADD_HOMEWORK.toString());
			}
		};
		OnLongClickListener onLongLessonClkLst = new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// Изменить дату занятия
				Bundle dialogInfo = new Bundle();
				dialogInfo.putString("idDialog", DialogBuilder.IdDialog.CHANGE_LESSON_DATE.toString());
				dialogInfo.putLong("mCurrentSpecialtyId", mCurrentSpecialtyId);
				dialogInfo.putInt("lessonId", v.getId());
				
				MainActivity.DialogBuilder.newInstance(getActivity(), dialogInfo).show(getFragmentManager(), DialogBuilder.IdDialog.CHANGE_LESSON_DATE.toString());
				return true;
			}
		};
		Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
		
		int cellWidth = 60;
		// Добавление дат занятий первой строкой
		for(int i=0; i < allLessons.getCount(); ++i){
			allLessons.moveToPosition(i);
			
			long dateInmSec = allLessons.getLong(allLessons.getColumnIndex(SPECIALTY_CLASSES_DATE.DATE));
			calendar.setTimeInMillis(dateInmSec);
			String date = DateFormat.format("dd.MM", calendar).toString();
			
			GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
			lp.width = cellWidth;
			lp.columnSpec = GridLayout.spec(i);
			lp.rowSpec = GridLayout.spec(0);
			lp.rightMargin = 1;
			
			FrameLayout lessonView = (FrameLayout) inflater.inflate(R.layout.student_list_item_1, mSpecialtyLessonsGrid, false);
			lessonView.setLayoutParams(lp);
			lessonView.setId(allLessons.getInt(allLessons.getColumnIndex(SPECIALTY_CLASSES_DATE.ID)));
			lessonView.setOnClickListener(onLessonClkLst);
			lessonView.setOnLongClickListener(onLongLessonClkLst);
			
			TextView text1 = (TextView) lessonView.findViewById(R.id.text1);
			text1.setText(date);
			// Id View = Id записи в бд для удобного доступа
			mSpecialtyLessonsGrid.addView(lessonView);
		}
		
		// Установка кнопки "добавить занятие" последней
		GridLayout.LayoutParams buttonLP = new GridLayout.LayoutParams();
		buttonLP.columnSpec = GridLayout.spec(allLessons.getCount() > 0 ? allLessons.getCount() : 0);
		buttonLP.rowSpec = GridLayout.spec(0);
		buttonLP.height = 48;
		
		Button addLessonButton = new Button(getActivity());
		addLessonButton.setBackgroundResource(R.drawable.quad_list);
		addLessonButton.setLayoutParams(buttonLP);
		addLessonButton.setText(R.string.add_lesson);
		addLessonButton.setTextColor(getResources().getColor(R.color.pen));
		addLessonButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// Добавляем новое занятие
				Bundle dialogInfo = new Bundle();
				dialogInfo.putString("idDialog", DialogBuilder.IdDialog.ADD_LESSON_DATE.toString());
				dialogInfo.putLong("mCurrentSpecialityId", mCurrentSpecialtyId);
				MainActivity.DialogBuilder.newInstance(getActivity(), dialogInfo).show(getFragmentManager(), DialogBuilder.IdDialog.ADD_LESSON_DATE.toString());
			}
		});
		mSpecialtyLessonsGrid.addView(addLessonButton);
		
		OnClickListener onClassClkLst = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Изменить/добавить урок
				Bundle dialogInfo = new Bundle();
				dialogInfo.putString("idDialog", MainActivity.DialogBuilder.IdDialog.SHOW_CLASS.toString());
				dialogInfo.putInt("lessonId", v.getId());
				dialogInfo.putInt("studentId", (int) v.getTag(R.string.student_id));
				MainActivity.DialogBuilder.newInstance(getActivity(), dialogInfo).show(getFragmentManager(), MainActivity.DialogBuilder.IdDialog.SHOW_CLASS.toString());
			}
		};
		
		Cursor studentListCursor = ((SimpleCursorAdapter) ((HeaderViewListAdapter) mStudentList.getAdapter()).getWrappedAdapter()).getCursor();
		// Заполнение основной части таблицы данными об оценках и т.д.
		for(int i=0; i < studentListCursor.getCount(); ++i){			
			studentListCursor.moveToPosition(i);
					
			int studentId = studentListCursor.getInt(studentListCursor.getColumnIndex(STUDENT.ID));

			for(int l=0; l < allLessons.getCount(); ++l){
				allLessons.moveToPosition(l);
				
				int lessonId = allLessons.getInt(studentListCursor.getColumnIndex(SPECIALTY_CLASSES_DATE.ID));
				
				Cursor allClasses = mdbHelper.getAllClassesOfStudentAtLesson(String.valueOf(studentId), String.valueOf(lessonId));
				allClasses.moveToFirst();
				
				GridLayout.LayoutParams emptyViewLP = new GridLayout.LayoutParams();
				emptyViewLP.columnSpec = GridLayout.spec(l);
				emptyViewLP.rowSpec = GridLayout.spec(i+1);
				emptyViewLP.width = cellWidth;
				emptyViewLP.setMargins(0, 0, 1, 1);
				// Пустая ячейка без оценок
				FrameLayout emptyView = (FrameLayout) inflater.inflate(R.layout.specialty_class_empty_item, mSpecialtytableGrid, false);
				emptyView.setId(lessonId);
				emptyView.setTag(R.string.student_id, studentId);
				emptyView.setLayoutParams(emptyViewLP);
				emptyView.setOnClickListener(onClassClkLst);
				
				for(int c = 0; c < allClasses.getCount(); ++c) {
					allClasses.moveToPosition(c);
					
					String classType = allClasses.getString(allClasses.getColumnIndex(SPECIALTY_CLASSES.CLASS_TYPE));
					String classTypeId = allClasses.getString(allClasses.getColumnIndex(SPECIALTY_CLASSES.CLASS_ID));
					
					Cursor currentClass = null;
					if(classType.compareTo(TABLES.NA) != 0) {
						currentClass = db.query(
								classType,
								null,
								"_id=?",
								new String[]{classTypeId},
								null, null, null);
						currentClass.moveToFirst();
					}
					View classView;
					
					switch(classType){
					case TABLES.HOMEWORK_RESULT:{ // Обычная оценка за доамашнюю работу
						
						int homeworkId = currentClass.getInt(currentClass.getColumnIndex(HOMEWORK_RESULT.ID));
						String mark = currentClass.getString(currentClass.getColumnIndex(HOMEWORK_RESULT.MARK));
						
						TextView homeworkView = new TextView(getActivity());
						homeworkView.setId(homeworkId);
						homeworkView.setText(mark);
						
						classView = homeworkView;
					}
						break;
					case TABLES.HOMEREADING:{
						
						int homereadingId = currentClass.getInt(currentClass.getColumnIndex(HOMEREADING.ID));
						int words = currentClass.getInt(currentClass.getColumnIndex(HOMEREADING.WORDS));
						int symbols = currentClass.getInt(currentClass.getColumnIndex(HOMEREADING.SYMBOLS));
						int retelling = currentClass.getInt(currentClass.getColumnIndex(HOMEREADING.RETELLING));
						int translating = currentClass.getInt(currentClass.getColumnIndex(HOMEREADING.TRANSLATING));
						
						View homereadingView = inflater.inflate(R.layout.specialty_class_homereading_item, emptyView, false);
						homereadingView.setId(homereadingId);
						LinearLayout borders = (LinearLayout) homereadingView.findViewById(R.id.borders);
						borders.setPadding((symbols > 0 ? 1 : 0), translating, retelling, (words > 0 ? 1 : 0));
						
						TextView sumbolsText = (TextView) homereadingView.findViewById(R.id.symbols);
						sumbolsText.setText(String.valueOf(symbols/1000));
						
						TextView wordsText = (TextView) homereadingView.findViewById(R.id.words);
						wordsText.setText(String.valueOf(words));
						
						classView = homereadingView;
					}
						break;
					case TABLES.NA:{
						
						TextView naView = new TextView(getActivity());
						naView.setText("Н");
						naView.setTextColor(getResources().getColor(R.color.pen));
						
						classView = naView;
					}
						break;
					default:
						classView = new View(getActivity());
					}
					emptyView.addView(classView);
				}
				mSpecialtytableGrid.addView(emptyView);
			}
		}
		db.close();
	}
	public void refillStudentList(){
		SimpleCursorAdapter StudentListAdater = (SimpleCursorAdapter) ((HeaderViewListAdapter) mStudentList.getAdapter()).getWrappedAdapter();
		StudentListAdater.changeCursor(mdbHelper.getAllStudentsFromSpecialty(String.valueOf(mCurrentSpecialtyId)));
	}
	@Override
	public void onDialogDismiss(IdDialog dialogId) {
		switch (dialogId) {
		case ADD_LESSON_DATE:case CHANGE_LESSON_DATE:case SHOW_CLASS:case ADD_HOMEREADING:case ADD_HOMEWORK_RESULT:
			refillSpecialtytableGrid();
			break;
		case ADD_STUDENT:
			refillStudentList();
			break;
		default:
			break;
		}
	}
}
