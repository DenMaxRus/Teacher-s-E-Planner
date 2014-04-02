package com.teacherse_planner;

import com.teacherse_planner.DBHelper.TABLES;
import com.teacherse_planner.DBHelper.TABLES.SPECIALTY;
import com.teacherse_planner.DBHelper.TABLES.SPECIALTY_CLASSES;
import com.teacherse_planner.DBHelper.TABLES.STUDENT;
import com.teacherse_planner.MainActivity.DialogBuilder.IdDialog;

import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
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
	private LinearLayout mGrouptableLayout;
	/** Список студентов */
	private ListView mStudentList;
	/** Список уроков */
	private GridView mSpecialtyLessonsGrid;
	/** Сетка оценок */
	private GridView mSpecialtytableGrid;
	
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
		mGrouptableLayout = (LinearLayout) inflater.inflate(R.layout.fragment_specialty_table, container, false);
		
		mStudentList = (ListView) mGrouptableLayout.findViewById(R.id.student_list);
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

		
		mSpecialtyLessonsGrid = (GridView) mGrouptableLayout.findViewById(R.id.specialty_lessons_grid);
		mSpecialtyLessonsGrid.setAdapter(
				new SimpleCursorAdapter(
						getActivity(),
						R.layout.student_grid_item,
						null,
						new String[]{SPECIALTY_CLASSES.DATE},
						new int[]{R.id.text1},
						0){
			
		});
		refillSpecialtyLessons();
		
		mSpecialtytableGrid = (GridView) mGrouptableLayout.findViewById(R.id.specialtytable_grid);
				
		return mGrouptableLayout;
	}
	public void refillSpecialtyLessons(){
		SQLiteDatabase db = mdbHelper.getReadableDatabase();
		SimpleCursorAdapter SpecialtyLessonsGridAdapter = (SimpleCursorAdapter)(mSpecialtyLessonsGrid.getAdapter());		
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
							null));
		db.close();
	}
	@Override
	public void onDialogDismiss(IdDialog dialogId) {
		// TODO Auto-generated method stub
		
	}
}
