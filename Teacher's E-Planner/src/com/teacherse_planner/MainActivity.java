package com.teacherse_planner;

import java.security.InvalidKeyException;
import java.util.Calendar;

import com.teacherse_planner.DBHelper.TABLES;
import com.teacherse_planner.DBHelper.TABLES.SPECIALTY;
import com.teacherse_planner.DBHelper.TABLES.TIMETABLE;
import com.teacherse_planner.NavigationDrawerFragment.NavigationDrawerCallbacks;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements NavigationDrawerCallbacks {
	
	private Calendar mCalendar;
	private DBHelper mdbHelper;
	/** Управляющий класс Navigation Drawer'a */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	/** Фрагмент расписания */
	private TimetableFragment mTimetableFragment;
	/** Фрагмент оценок группы */
	private SpecialtytableFragment mSpecialtytableFragment;
	/** Заголовок текущего окна */
	private CharSequence mTitle;
	
	public Calendar getCalendar(){
		return mCalendar;
	}
	public DBHelper getDbHelper(){
		return mdbHelper;
	}
	public TimetableFragment getmTimetableFragment() {// TODO убрать!!! используется пока только в создании диалогов для получения недели
		return mTimetableFragment;
	}
	public SpecialtytableFragment getmSpecialtytableFragment(){
		return mSpecialtytableFragment;
	}
	@Override
	public void setTitle(CharSequence title) {
		// TODO Auto-generated method stub
		super.setTitle(title);
		mTitle = title;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Создаем вспомогательные объекты помошники, из всех фрагментов будем использовать отсюда
		mdbHelper = new DBHelper(this);
		mCalendar = Calendar.getInstance();
		mTitle = getTitle();
		
		setContentView(R.layout.activity_main_screen);
		// Найти фрагмент Navigation Drawer'a и вызвать его настройку
		mNavigationDrawerFragment = (NavigationDrawerFragment)getFragmentManager().findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp((DrawerLayout) findViewById(R.id.drawer_layout));
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment())
					.commit();
		}
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// TODO сохранить последний видимый фрагмент
		outState.putInt("lastFragment",(getFragmentManager().findFragmentById(R.id.container).getId()));
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
		// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main_screen, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			//return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onNavigationMenuItemSelected(int position) {
		// TODO Переключать окна в зависимости от позиции
		FragmentTransaction NewTransaction = getFragmentManager().beginTransaction();
		switch(position){
		case 0:// Расписание
			if(mTimetableFragment == null)
				mTimetableFragment = new TimetableFragment();
			NewTransaction
				.replace(R.id.container, mTimetableFragment);
			break;
		case 1:// Окно группы (обрабатывается самим NavigationDrawer'ом
			break;
		case 2:// Настройки
			break;
		case 3:// Выход
			finish();
			//System.exit(0);
			break;
		case 4:// Test
			NewTransaction
				.replace(R.id.container, new PlaceholderFragment());
			mTitle = getResources().getText(R.string.app_name);
			break;
		case 5:// Группа 2
			if(mSpecialtytableFragment == null)
				mSpecialtytableFragment = new SpecialtytableFragment();
			NewTransaction
				.replace(R.id.container, mSpecialtytableFragment);
			break;
		}
		NewTransaction.commit();
	}
	/** Перестроить ActionBar */
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
    /**
     * Класс, управляющий диалогами
     */
	public static class DialogBuilder extends DialogFragment {
		/** Текущий готовый диалог */
		private static Dialog mCurrentDialogInstance = null;
		/** ID текущего диалога */
		private static IdDialog mCurrentDialogId = null;
		/** Текущая активити */
		private static Activity mContext;
		/** Интерфейс для свзязи с фрагментами */
		private DialogCallbacks mDialogCallbacks;
		public static enum IdDialog { Timetable_ChangeDay, Add_Specialty }; // Сюда добавлять Id новых диалогов
		/** Возвращает текущий диалог 
		 * @return Текущий диалог или null
		 */
		public static Dialog getCurrentDialog(){
			return mCurrentDialogInstance;
		}
		/** Возвращает ID текущего диалога 
		 * @return ID текущего диалога или null
		 */
		public static IdDialog getCurrentDialogId(){
			return mCurrentDialogId;
		}
		/** Создает новый диалог
		 * 
		 * @param dialogInfo - куча передаваемых параметров
		 * @return Новый диалог DialogBuilder
		 */
		public static DialogBuilder newInstance(Context context,Bundle dialogInfo){
			mContext = (Activity) context;
			DialogBuilder instance = new DialogBuilder();
			instance.setArguments(dialogInfo);
			return instance;
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			try {
				mDialogCallbacks = (DialogCallbacks) getFragmentManager().findFragmentById(R.id.container);
        	} catch (ClassCastException e) {
        		throw new ClassCastException("Fragment must implement DialogCallbacks.");
        	}
			// TODO Пока затычка
			final DBHelper mdbHelper = new DBHelper(getActivity());
			// Создаем различные окна диалогов
			AlertDialog.Builder builder = new Builder(getActivity());
			try {
			mCurrentDialogId = IdDialog.valueOf(getArguments().getString("idDialog"));
			switch (mCurrentDialogId) {
			case Timetable_ChangeDay:{ // Диалог по долгому нажатию на клетку в расписании фрагмента TimetableFragment
				SQLiteDatabase db = mdbHelper.getReadableDatabase();
				final int idTimetable = getArguments().getInt("idTimetable");
				final int currentWeek = ((MainActivity) mContext).getmTimetableFragment().getWeek();
				// Получаем требуемые значения для заполнения текущими данными из БД
				Cursor dayInfo = db.query(
						TABLES.TIMETABLE+" INNER JOIN "+TABLES.SPECIALTY+" ON "+TIMETABLE.SPECIALTY_ID+"="+ SPECIALTY.aID,
						new String[]{TIMETABLE.fID+" AS "+TIMETABLE.aID, TIMETABLE.CLASSROOM, TIMETABLE.COLOR, TIMETABLE.WEEK, SPECIALTY.fID+" AS "+SPECIALTY.aID},
						TIMETABLE.aID+"=? AND "+TIMETABLE.WEEK+"=?",
						new String[]{String.valueOf(idTimetable), String.valueOf(currentWeek)},
						null, null, null);
				dayInfo.moveToFirst();
				final boolean dayInfoIsEmpty = dayInfo.getCount() == 0 ? true : false;
				// Текущие значение (на момент открытия диалога)
				final String currentClassroom = dayInfoIsEmpty ? "" : dayInfo.getString(dayInfo.getColumnIndex(TIMETABLE.CLASSROOM));
				final int currentSpecialtyPosition = dayInfoIsEmpty ? 0 : dayInfo.getInt(dayInfo.getColumnIndex(SPECIALTY.aID)) - 1;
				int currentColor = dayInfoIsEmpty ? 0 : dayInfo.getInt(dayInfo.getColumnIndex(TIMETABLE.COLOR));
				
				// Создание собственного вида для диалога, настрока спиннера и текстового поля
				View dialogView = View.inflate(getActivity(), R.layout.dialog_timetable_griditemlongclick, null);
				// Номер аудитории
				final EditText classroom = (EditText) dialogView.findViewById(R.id.classroom);
				classroom.setText(currentClassroom);
				// Спиннер списка групп
				final Spinner specialtiesSpinner = (Spinner) dialogView.findViewById(R.id.specialties_spinner);
				// Заполнение списка выбора групп группами
				SimpleCursorAdapter specialtiesSpinnerAdapter = new SimpleCursorAdapter(
						getActivity(),
						android.R.layout.simple_spinner_item,
						mdbHelper.getReadableDatabase().query(TABLES.SPECIALTY, null, null, null, null, null, null),
						new String[]{SPECIALTY.NAME},
						new int[]{android.R.id.text1},
						0);
				specialtiesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				specialtiesSpinner.setAdapter(specialtiesSpinnerAdapter);
				specialtiesSpinner.setSelection(currentSpecialtyPosition);
				db.close();
				// Список цвета
				final Spinner colorSpinner = (Spinner) dialogView.findViewById(R.id.color_spinner);
				int[] oldColors = getResources().getIntArray(R.array.colors);
				Integer[] colors = new Integer[oldColors.length];
				final String[] colorNames = getResources().getStringArray(R.array.color_names);
				int currentColorPos = 0;
				for(int i=0;i < oldColors.length; ++i) {
					colors[i] = Integer.valueOf(oldColors[i]);
					if(colors[i] == currentColor)
						currentColorPos = i;
				}
				ArrayAdapter<Integer> colorSpinnerAdapter = new ArrayAdapter<Integer>(
						getActivity(),
						android.R.layout.simple_spinner_item,
						colors){
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						View view = super.getView(position, convertView, parent);
						view.setBackgroundColor(getItem(position));
						((TextView)view.findViewById(android.R.id.text1)).setText(colorNames[position]);
						return view;
					}
					@Override
					public View getDropDownView(int position, View convertView, ViewGroup parent) {
						View view =  super.getDropDownView(position, convertView, parent);
						view.setBackgroundColor(getItem(position));
						((TextView)view.findViewById(android.R.id.text1)).setText(colorNames[position]);
						return view;
					}
				};
				colorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				colorSpinner.setAdapter(colorSpinnerAdapter);
				colorSpinner.setSelection(currentColorPos);
				builder
				.setMessage("Изменить день")
				.setView(dialogView)
				.setCancelable(true)
				.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SQLiteDatabase db = mdbHelper.getWritableDatabase();
						
						//Помещаемые значения
						long selectedSpecialityId = specialtiesSpinner.getSelectedItemId();
						String selectedClassroom = classroom.getText().toString();
						Integer selectedColor = (Integer) colorSpinner.getSelectedItem();
						
						//Пустые ли новые значения
						boolean newdayInfoisEmpty = (selectedSpecialityId == 1 && selectedClassroom.length() == 0 && selectedColor == getResources().getIntArray(R.array.colors)[0]) ? true : false;
						if(!newdayInfoisEmpty){
							ContentValues cv = new ContentValues();
							cv.put(TIMETABLE.SPECIALTY_ID, selectedSpecialityId);
							cv.put(TIMETABLE.CLASSROOM, selectedClassroom);
							cv.put(TIMETABLE.COLOR, selectedColor);
							if(dayInfoIsEmpty){
								cv.put(TIMETABLE.ID, idTimetable);
								cv.put(TIMETABLE.WEEK, currentWeek);
								db.insert(TABLES.TIMETABLE, null, cv);
							}else
								db.update(TABLES.TIMETABLE, cv, TIMETABLE.ID+"=? AND "+TIMETABLE.WEEK+"=?",new String[]{String.valueOf(idTimetable), String.valueOf(currentWeek)});
						}else
							db.delete(TABLES.TIMETABLE, TIMETABLE.ID+"=? AND "+TIMETABLE.WEEK+"=?",new String[]{String.valueOf(idTimetable), String.valueOf(currentWeek)});;
						db.close();
						dialog.dismiss();
					}
				})
				.setNegativeButton(R.string.cancel, null);
			}
				break;
			case Add_Specialty:{
				final EditText editSpecialty = new EditText(mContext);
				editSpecialty.setHint("Введите название группы");
				builder
					.setMessage("Добавить группу")
					.setView(editSpecialty)
					.setCancelable(true)
					.setPositiveButton(R.string.add, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(editSpecialty.length() != 0)	{
								ContentValues cv = new ContentValues();
								cv.put(SPECIALTY.NAME, editSpecialty.getText().toString());
								mdbHelper.getWritableDatabase().insert(TABLES.SPECIALTY, null, cv);
							}
							dialog.dismiss();
						}
					})
					.setNegativeButton(R.string.cancel, null);
				
			}
				break;
			default:
				throw new InvalidKeyException("No such ID in DialogBuilder.IdDialog");
			}
			} catch (InvalidKeyException e) {
				return null;
			}
			// Возвращаем созданное окно и записываем как текущее
			return mCurrentDialogInstance = builder.create();
		}
		@Override
		public void onDismiss(DialogInterface dialog) {
			// TODO Auto-generated method stub
			super.onDismiss(dialog);
			mDialogCallbacks.onDialogDismiss(mCurrentDialogId);
		}
		@Override
		public void onDetach() {
			// TODO Auto-generated method stub
			super.onDetach();
			mCurrentDialogInstance = null;
			mCurrentDialogId = null;
		}
		public static interface DialogCallbacks{
			/** Вызывается после метода OnDismiss() 
			 * @param dialogId - ID диалога */
			public void onDialogDismiss(IdDialog dialogId);
		}
	}
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_screen,
					container, false);
			return rootView;
		}
	}

}
