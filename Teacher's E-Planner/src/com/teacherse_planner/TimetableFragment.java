package com.teacherse_planner;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class TimetableFragment extends Fragment {
	
	private String mTitle = "Расписание"; // Заголовок
	public String getmTitle() {
		return mTitle;
	}
	LinearLayout mTimetableLayout; // Вся панель расписания
	GridView mPairTimeGrid; // Сетка времени пар
	GridView mTimetableGrid; // Сетка расписания
	ListView mDayList; // Лист дней недели
	DBHelper mdbHelper; // Класс работы с БД
	int mCurrentWeek; // Текущая неделя (1/2)
	
	public TimetableFragment(){}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Добавить actionbar
		super.onCreate(savedInstanceState);
		// Если есть сохраненное состояние - вынимаем оттуда неделю, иначе будет первая
		mCurrentWeek = savedInstanceState == null ? 1 : savedInstanceState.getInt("mCurrentWeek");
		
		// TODO Заменить - один и тот же объект во всех классах.
		mdbHelper = new DBHelper(getActivity());
		setHasOptionsMenu(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Разве здесь подключать адаптеры?
		// Если еще не создавался - создать, иначе вернуть уже существующее полотно
		mTimetableLayout = (LinearLayout) inflater.inflate(R.layout.fragment_timetable, container, false);// Вся панель расписания
		// Лист дней недели
		mDayList = (ListView) mTimetableLayout.findViewById(R.id.day_list);
		// Header для дней недели (Пары)
		TextView DayListHeader = (TextView) inflater.inflate(R.layout.pair_time_list_item_1, null);
		DayListHeader.setText("Пары");
		mDayList.addHeaderView(DayListHeader);
		// TODO Либо изменить полностью, либо дополнить выбором текущего дня и выделением ячейки
		final Calendar calendar = Calendar.getInstance(); // Засунуть бы такие объекты в активити
		mDayList.setAdapter(new ArrayAdapter<String>(
				getActivity(),
				R.layout.timetable_grid_item_2,
				R.id.text1,
				new String[]{"Пн", "Вт", "Ср", "Чт", "Пт", "Сб"}){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null)
					convertView = getActivity().getLayoutInflater().inflate(R.layout.timetable_grid_item_2, parent, false);
				
				((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position)); // День недели
				((TextView) convertView.findViewById(R.id.text2)).setText((calendar.get(Calendar.DAY_OF_WEEK)==(position+2)?"Сегодня":String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)+position+2-calendar.get(Calendar.DAY_OF_WEEK))+" "+calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK))); // День месяца
				
				return convertView;
			}});
		// Сетка расписания
		mTimetableGrid = (GridView) mTimetableLayout.findViewById(R.id.timetable_grid);
		// Заполнение таблицы из базы данных
		mTimetableGrid.setAdapter(new SimpleCursorAdapter(// Двух уровневый адаптер: 1lvl - Название группы, 2lvl - номер аудитории
				getActivity(),
				R.layout.timetable_grid_item_2,
				null,
				new String[]{DBHelper.SPECIALTY_NAME, DBHelper.TIMETABLE_CLASSROOM},
				new int[]{android.R.id.text1, android.R.id.text2},
				0){
			@Override
			public void bindView(View view, Context context, Cursor cursor) {
				// TODO Изменить заполнение текстовых полей и выборку из курсора (?) проблема с БД
				((TextView) view.findViewById(R.id.text1)).setText(cursor.getString(cursor.getColumnIndex(DBHelper.SPECIALTY_NAME)));// Название группы
				((TextView) view.findViewById(R.id.text2)).setText(cursor.getString(cursor.getColumnIndex(DBHelper.TIMETABLE_CLASSROOM)));// Номер аудитории
				// TODO Педелать выбор текущей пары
				int id = cursor.getInt(0);
				if(id>42)
					id-=42;
				Calendar c2=Calendar.getInstance();
				int currentHour=c2.get(Calendar.HOUR_OF_DAY);
				int currentMinute=c2.get(Calendar.MINUTE);
				int currentTime=currentHour*60+currentMinute;
				int pair=id-(id/8)*7-1;
				int pairtime=8*60+pair*(90+10);
				if(c2.get(Calendar.DAY_OF_WEEK)==(id/8+2) && currentTime>=pairtime && currentTime<pairtime+90){
					view.setBackgroundColor(Color.YELLOW);
				}
				super.bindView(view, context, cursor);
			}
		});
		// TODO Исправить работу кликера (вызов диалога изменения дня)
		mTimetableGrid.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Убрать создание диалога из метода, использовать специальный класс
				final long idTimetable=id;
				// Создание собственного вида для диалога, настрока спиннера и текстового поля
				View dialogView = View.inflate(getActivity(), R.layout.dialog_timetable_griditemlongclick, null);
				// Номер аудитории
				final EditText classroom = (EditText) dialogView.findViewById(R.id.classroom);
				classroom.setText(((TextView)view.findViewById(R.id.text2)).getText());
				// Спиннер списка групп
				final Spinner specialtiesSpinner = (Spinner) dialogView.findViewById(R.id.specialties_spinner);
				// Заполнение списка выбора групп группами
				specialtiesSpinner.setAdapter(new SimpleCursorAdapter(
						getActivity(),
						android.R.layout.simple_spinner_item,
						mdbHelper.getReadableDatabase().query(DBHelper.SPECIALTY, null, null, null, null, null, null),
						new String[]{DBHelper.SPECIALTY_NAME},
						new int[]{android.R.id.text1},
						0));
				//specialtiesSpinner.setPrompt(((TextView)view.findViewById(R.id.text1)).getText());
				// Выделение текущей группы TODO ну не через отдельный курсор же
				Cursor groupName = mdbHelper.getReadableDatabase().query(
						DBHelper.SPECIALTY,
						new String[]{DBHelper.SPECIALTY_ID},
						DBHelper.SPECIALTY_NAME+"=?",
						new String[]{((TextView)view.findViewById(R.id.text1)).getText().toString()}, null, null, null);
				groupName.moveToFirst();
				specialtiesSpinner.setSelection(groupName.getInt(0)-1);
				
				AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
				builder
					.setMessage("Изменить день")
					.setView(dialogView)
					.setCancelable(true)
					.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//Помещаемые значения
							ContentValues cv = new ContentValues();
							cv.put(DBHelper.TIMETABLE_SPECIALTY_ID, specialtiesSpinner.getSelectedItemId());
							cv.put(DBHelper.TIMETABLE_CLASSROOM, classroom.getText().toString());
							mdbHelper.getWritableDatabase()
								.update(
										DBHelper.TIMETABLE,
										cv,
										DBHelper.TIMETABLE_ID+"=? AND "+DBHelper.TIMETABLE_WEEK+"=?",
										new String[]{String.valueOf(idTimetable), String.valueOf(mCurrentWeek)});
							dialog.dismiss();
							refillTimetable();// или просто изменить значение поля, в котором меняли
						}
					})
					.setNegativeButton("Отмена", null)
					.create()
					.show();
				return true;
			}
		});
		
		// Сетка времени пар
		mPairTimeGrid = (GridView) mTimetableLayout.findViewById(R.id.pairtime_grid);
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
		super.onActivityCreated(savedInstanceState);
		// При загрузке обновляем расписание
		refillTimetable();
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// При закрытии сохраняем текущую неделю TODO сохранить еще чего нибудь бы ^^
		outState.putInt("mCurrentWeek", mCurrentWeek);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.timetable, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int itemId = item.getItemId();
		switch(itemId){
		case R.id.current_week:
			mCurrentWeek = mCurrentWeek == 1 ? 2 : 1;
			refillTimetable();
			getActivity().invalidateOptionsMenu();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// Показать текущее значение недели в actionbar'e
		menu.findItem(R.id.current_week).setTitle("Идет "+String.valueOf(mCurrentWeek)+" неделя");
		super.onPrepareOptionsMenu(menu);
	}
	// Перерисовать расписание
	private void refillTimetable(){
		((SimpleCursorAdapter)(mTimetableGrid.getAdapter()))
				.changeCursor(mdbHelper.getReadableDatabase()
						.query(
								DBHelper.TIMETABLE+" LEFT OUTER JOIN "+DBHelper.SPECIALTY+" ON "+DBHelper.TIMETABLE_SPECIALTY_ID+"="+DBHelper.SPECIALTY+"."+DBHelper.SPECIALTY_ID,
								new String[]{DBHelper.TIMETABLE+"."+DBHelper.TIMETABLE_ID, DBHelper.SPECIALTY_NAME, DBHelper.TIMETABLE_CLASSROOM},
								DBHelper.TIMETABLE_WEEK+"=?",
								new String[]{String.valueOf(mCurrentWeek)},
								null, null, null));
		((SimpleCursorAdapter)mTimetableGrid.getAdapter()).notifyDataSetChanged();
	}
}
