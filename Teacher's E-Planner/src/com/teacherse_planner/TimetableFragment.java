package com.teacherse_planner;

import java.util.Calendar;
import java.util.Locale;

import com.teacherse_planner.DBHelper.TABLES;
import com.teacherse_planner.DBHelper.TABLES.SPECIALTY;
import com.teacherse_planner.DBHelper.TABLES.TIMETABLE;
import com.teacherse_planner.MainActivity.DialogBuilder;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TimetableFragment extends Fragment implements MainActivity.DialogBuilder.DialogCallbacks {
	/** Заголовок */
	private String mTitle = "Расписание";
	public String getTitle() {
		return mTitle;
	}
	/** Вся панель расписания */
	LinearLayout mTimetableLayout;
	/** Сетка времени пар */
	GridView mPairTimeGrid;
	/** Сетка расписания */
	GridView mTimetableGrid;
	/** Лист дней недели */
	ListView mDayList;
	/** Объект для работы с БД */
	DBHelper mdbHelper;
	/** Текущая неделя (1/2) */
	int mCurrentWeek;
	public int getWeek(){
		return mCurrentWeek;
	}
	public TimetableFragment(){}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
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
		final Calendar calendar = Calendar.getInstance(); // TODO Засунуть бы такие объекты в активити
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
		/*mTimetableGrid.setAdapter(new SimpleCursorAdapter(// Двух уровневый адаптер: 1lvl - Название группы, 2lvl - номер аудитории
				getActivity(),
				R.layout.timetable_grid_item_2,
				null,
				new String[]{SPECIALTY.NAME, TIMETABLE.CLASSROOM, TIMETABLE.COLOR},
				new int[]{android.R.id.text1, android.R.id.text2},
				0){
			@Override
					public int getCount() {
						// TODO Auto-generated method stub
						return 6*7;
					}
			@Override
			public void bindView(View view, Context context, Cursor cursor) {
				super.bindView(view, context, cursor);
				int id = cursor.getInt(0);
				// TODO Изменить заполнение текстовых полей и выборку из курсора (?) проблема с БД
				((TextView) view.findViewById(R.id.text1)).setText(cursor.getString(cursor.getColumnIndex(SPECIALTY.NAME)));// Название группы
				((TextView) view.findViewById(R.id.text2)).setText(cursor.getString(cursor.getColumnIndex(TIMETABLE.CLASSROOM)));// Номер аудитории
				// TODO Педелать выбор текущей пары
				if(id>42)
					id-=42;
				Calendar calendar=Calendar.getInstance();
				//int currentHour=calendar.get(Calendar.HOUR_OF_DAY);
				//int currentMinute=calendar.get(Calendar.MINUTE);
				int currentTime=calendar.get(Calendar.HOUR_OF_DAY)*60+calendar.get(Calendar.MINUTE);
				//int pair=id-(id/8)*7-1;
				int pairTime=8*60+(id-(id/8)*7-1)*(90+10);
				if(calendar.get(Calendar.DAY_OF_WEEK)==(id/8+2) && currentTime>=pairTime && currentTime<pairTime+90){
					view.setBackgroundColor(Color.RED);
				}
			}
		});*/
		mTimetableGrid.setAdapter(new TableCursorAdapter(
				getActivity(),
				R.layout.timetable_grid_item_2,
				null,
				new String[]{SPECIALTY.NAME, TIMETABLE.CLASSROOM},//, TIMETABLE.COLOR},
				new int[]{R.id.text1, R.id.text2},
				42));
		// Вызов диалога "Изменение дня"
		mTimetableGrid.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				// Создать и показать диалог "Изменение дня"
				Bundle dialogInfo = new Bundle();
				dialogInfo.putString("idDialog", DialogBuilder.IdDialog.Timetable_ChangeDay.toString());
				dialogInfo.putInt("idTimetable", position+1);
				MainActivity.DialogBuilder.newInstance(getActivity(), dialogInfo).show(getFragmentManager(), DialogBuilder.IdDialog.Timetable_ChangeDay.toString());
				return true;
			}
		});
		
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
	public void onResume() {
		super.onResume();
		// Проверить, есть ли диалог, если есть - добавить команду на восстановление
		if(DialogBuilder.getCurrentDialogId() == DialogBuilder.IdDialog.Timetable_ChangeDay){
			DialogBuilder.getCurrentDialog().show();
		}
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
	/** Перерисовать текущее расписание */
	public void refillTimetable(){
		SQLiteDatabase db = mdbHelper.getReadableDatabase();
		((TableCursorAdapter)(mTimetableGrid.getAdapter()))
				.changeCursor(
						db.query(
								TABLES.TIMETABLE+" JOIN "+TABLES.SPECIALTY+" ON "+TIMETABLE.fSPECIALTY_ID+"="+SPECIALTY.fID,
								new String[]{TIMETABLE.fID, SPECIALTY.NAME, TIMETABLE.CLASSROOM, TIMETABLE.COLOR},
								TIMETABLE.WEEK+"=?",
								new String[]{String.valueOf(mCurrentWeek)},
								null, null, null));
		((TableCursorAdapter)mTimetableGrid.getAdapter()).notifyDataSetChanged();
		db.close();
	}
	@Override
	public void onDialogDismiss(DialogBuilder.IdDialog dialogId) {
		// Что делать по закрытию диалоговых окон
		switch (dialogId) {
		case Timetable_ChangeDay:
			refillTimetable();
			break;
		default:
			break;
		}
		
	}
	public class TableCursorAdapter extends CursorAdapter {
		
		private Context mContext;
		private LayoutInflater mInflater;
		private int mLayout;
		private String[] mFrom;
		private int[] mTo;
		private int mSize;
		private long mCursorId;
		private int mCursorIdPosition;
		private int mViewPosition;
		private int mRowIDColumn;
		/**
		 * 
		 * @param context The context where the ListView associated with this SimpleListItemFactory is running 
		 * @param layout resource identifier of a layout file that defines the views for this list item. The layout file should include at least those named views defined in "to" 
		 * @param c The database cursor. Can be null if the cursor is not available yet. 
		 * @param from A list of column names representing the data to bind to the UI. Can be null if the cursor is not available yet. 
		 * @param to The views that should display column in the "from" parameter. These should all be TextViews. The first N views in this list are given the values of the first N columns in the from parameter. Can be null if the cursor is not available yet. 
		 * @param size How many items should be in the data set represented by this Adapter.
		 */
		public TableCursorAdapter(Context context,int layout, Cursor c, String[] from, int[] to, int size) {
			super(context, c, 0);
			mContext = context;
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mLayout = layout;
			mFrom = from;
			mTo = to;
			mSize = size;
			boolean cursorPresent = c != null;
			mRowIDColumn = cursorPresent  ? c.getColumnIndexOrThrow("_id") : -1;
			mViewPosition = 0;
			setStartIdPosition(cursorPresent);
		}
		/**
		 * Проверяет, совпадает ли позиция(!)(позиция+1) в списке с id записи 
		 * @return true если совпадает, иначе - false
		 */
		protected boolean isAtCurrent() {
			return mViewPosition == mCursorId ? true : false;
		}
		protected void setStartIdPosition(boolean cursorPresent){
			if(cursorPresent){
				setNextIdPosition();
				getCursor().moveToPosition(-1);
			}else {
				mCursorId = mCursorIdPosition = -1;
			}
		}
		protected void setNextIdPosition(){
			if(getCursor().moveToNext()){
				mCursorId = getCursor().getLong(mRowIDColumn);
				mCursorIdPosition = getCursor().getPosition();
			} else {
				mCursorId = mCursorIdPosition = -1;
			}
		}
		@Override
		public void changeCursor(Cursor cursor) {
			super.changeCursor(cursor);
			boolean cursorPresent = cursor != null;
			mRowIDColumn = cursorPresent  ? cursor.getColumnIndexOrThrow("_id") : -1;
			setStartIdPosition(cursorPresent);
		}
		@Override
		public int getCount() {
			if(getCursor() != null)
				return mSize;
			else
				return 0;
		};
		@Override
		public Object getItem(int position) {
			mViewPosition = position+1;
			if(getCursor() != null){
				if(isAtCurrent())
					return super.getItem(mCursorIdPosition);
				else
					return null;
			}else
				return null;
		}
		@Override
		public long getItemId(int position) {
			mViewPosition = position+1;
			if(isAtCurrent())
				return super.getItemId(mCursorIdPosition);
			else
				return 0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if((mViewPosition = position+1) == mCursorId) {
				getCursor().moveToPosition(mCursorIdPosition);
			}
	        View view;
	        if (convertView == null) {
	            view = newView(mContext, getCursor(), parent);
	        } else {
	            view = convertView;
	        }
	        bindView(view, mContext, getCursor());
	        return view;
		}
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			if(isAtCurrent()) {
				for(int i=0; i < (mFrom.length > mTo.length ? mTo.length : mFrom.length); ++i){
					TextView tv = (TextView) view.findViewById(mTo[i]);
					String data = cursor.getString(cursor.getColumnIndex(mFrom[i]));
					tv.setText(data);
				}
				setNextIdPosition();
			}
		}
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(mLayout, parent, false);
			return view;
		}
	}
}
