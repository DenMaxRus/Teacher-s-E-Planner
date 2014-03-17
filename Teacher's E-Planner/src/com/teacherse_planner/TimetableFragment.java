package com.teacherse_planner;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TimetableFragment extends Fragment {
	
	LinearLayout mTimetableLayout; // Вся панель расписания
	GridView mPairTimeGrid; // Сетка времени пар
	GridView mTimetableGrid; // Сетка расписания
	ListView mDayList; // Лист дней недели
	DBHelper mdbHelper; // Класс работы с БД
	int CurrentWeek;	
	
	public TimetableFragment(){}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Добавить actionbar
		super.onCreate(savedInstanceState);
		CurrentWeek = 1;
		
		// TODO Заменить - один и тот же объект во всех классах.
		mdbHelper = new DBHelper(getActivity());
		setHasOptionsMenu(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Разве здесь подключать адаптеры?
		
		mTimetableLayout = (LinearLayout) inflater.inflate(R.layout.fragment_timetable, container, false);// Вся панель расписания

		mDayList = (ListView) mTimetableLayout.findViewById(R.id.day_list);// Лист дней недели
		// TODO Либо изменить полностью, либо дополнить выбором текущего дня и выделением ячейки
		mDayList.setAdapter(new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_1,
				new String[]{"Пн", "Вт", "Ср", "Чт", "Пт", "Сб"}));
		
		mTimetableGrid = (GridView) mTimetableLayout.findViewById(R.id.timetable_grid);// Сетка расписания
		// TODO Выборка текущей пары
		mTimetableGrid.setAdapter(new SimpleCursorAdapter(// Двух уровневый адаптер: 1lvl - Название группы, 2lvl - номер аудитории
				getActivity(),
				R.layout.timetable_grid_item_2,
				null,
				new String[]{DBHelper.SPECIALTY_NAME, DBHelper.TIMETABLE_CLASSROOM},
				new int[]{android.R.id.text1, android.R.id.text2},
				0){
			@Override
			public void bindView(View view, Context context, Cursor cursor) {
				// TODO Изменить заполнение текстовых полей и выборку из курсора (?)
				((TextView) view.findViewById(R.id.text1)).setText(cursor.getString(cursor.getColumnIndex(DBHelper.SPECIALTY_NAME)));// Название группы
				((TextView) view.findViewById(R.id.text2)).setText(cursor.getString(cursor.getColumnIndex(DBHelper.TIMETABLE_CLASSROOM)));// Номер аудитории
				super.bindView(view, context, cursor);
			}
		});
		
		mPairTimeGrid = (GridView) mTimetableLayout.findViewById(R.id.pairtime_grid);// Сетка времени пар
		// TODO Изменить время, добавить возможность изменения
		mPairTimeGrid.setAdapter(new ArrayAdapter<String>(
				getActivity(),
				R.layout.pair_time_list_item_1,
				new String[]{"8.00 - 9.30", "9.40 - 11.10", "11.30 - 13.00", "13.10 - 14.40", "14.50 - 16.20", "16.30 - 18.00", "18.00 - 19.30"}));
		
		return mTimetableLayout;
		//return super.onCreateView(inflater, container, savedInstanceState);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		refillTimetable();
	}
	private void refillTimetable(){
		((SimpleCursorAdapter)(mTimetableGrid.getAdapter()))
				.changeCursor(mdbHelper.getReadableDatabase()
						.query(
								DBHelper.TIMETABLE+" LEFT OUTER JOIN "+DBHelper.SPECIALTY+" ON "+DBHelper.TIMETABLE_SPECIALTY_ID+"="+DBHelper.SPECIALTY+"."+DBHelper.SPECIALTY_ID,
								new String[]{DBHelper.TIMETABLE+"."+DBHelper.TIMETABLE_ID, DBHelper.SPECIALTY_NAME, DBHelper.TIMETABLE_CLASSROOM},
								DBHelper.TIMETABLE_WEEK+"=?",
								new String[]{String.valueOf(CurrentWeek)},
								null, null, null));
	}
}
