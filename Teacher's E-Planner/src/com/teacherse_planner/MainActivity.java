package com.teacherse_planner;

import java.security.InvalidKeyException;
import java.util.Calendar;

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
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity implements NavigationDrawerCallbacks {
	
	private Calendar mCalendar; //
	public Calendar getCalendar(){
		return mCalendar;
	}
	private DBHelper mdbHelper;
	public DBHelper getDbHelper(){
		return mdbHelper;
	}
	
	private NavigationDrawerFragment mNavigationDrawerFragment; // Управляющий класс Navigation Drawer'a
	private TimetableFragment mTimetableFragment;
	private CharSequence mTitle; // Название текущего окна
	
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
			mTitle = mTimetableFragment.getTitle();
			break;
		case 1:// Окно группы (обрабатывается самим NavigationDrawer'ом
			break;
		case 2:// Настройки
			break;
		case 3:// Выход
			finish();
			System.exit(0);
			break;
		case 4:// Test
			NewTransaction
				.replace(R.id.container, new PlaceholderFragment());
			mTitle = getResources().getText(R.string.app_name);
			break;
		}
		NewTransaction.commit();
	}
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
	// TODO Подумать над класом диалогов
	public static class DialogBuilder extends DialogFragment {
		
		public static Dialog mCurrentDialogInstance; // Текущий готовый диалог
		public static IdDialog mCurrentDialogId; // Id текущего диалога
		private DialogCallbacks mDialogCallbacks; // Интерфейс для свзязи с фрагментами
		public static enum IdDialog { Timetable_ChangeDay }; // Сюда добавлять Id новых диалогов
		
		public static DialogBuilder newInstance(Bundle dialogInfo){
			DialogBuilder instance = new DialogBuilder();
			instance.setArguments(dialogInfo);
			return instance;
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			try {
				mDialogCallbacks = (DialogCallbacks) getFragmentManager().findFragmentById(R.id.container);
        	} catch (ClassCastException e) {
        		throw new ClassCastException("Fragent must implement DialogCallbacks.");
        	}
			// Пока затычка
			final DBHelper mdbHelper = new DBHelper(getActivity());
			// Создаем различные окна диалогов
			try {
			AlertDialog.Builder builder = new Builder(getActivity());
			mCurrentDialogId = IdDialog.valueOf(getArguments().getString("idDialog"));
			switch (mCurrentDialogId) {
			case Timetable_ChangeDay:{ // Диалог по долгому нажатию на клетку в расписании фрагмента TimetableFragment
				final int currentWeek = getArguments().getInt("currentWeek", 1);
				final int idTimetable = getArguments().getInt("idTimetable");
				final String currentSpecialityName = getArguments().getString("currentSpecialityName");
				final String currentClassroom = getArguments().getString("currentClassroom");
				// Создание собственного вида для диалога, настрока спиннера и текстового поля
				View dialogView = View.inflate(getActivity(), R.layout.dialog_timetable_griditemlongclick, null);
				// Номер аудитории
				final EditText classroom = (EditText) dialogView.findViewById(R.id.classroom);
				classroom.setText(currentClassroom);
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
				// Выделение текущей группы TODO ну не через отдельный курсор же , да?
				Cursor CurrentSpecialityNameCursor = mdbHelper.getReadableDatabase().query(
						DBHelper.SPECIALTY,
						new String[]{DBHelper.SPECIALTY_ID},
						DBHelper.SPECIALTY_NAME+"=?",
						new String[]{currentSpecialityName}, null, null, null);
				CurrentSpecialityNameCursor.moveToFirst();
				specialtiesSpinner.setSelection(CurrentSpecialityNameCursor.getInt(0)-1);
				// Список цвета
				final Spinner colorSpinner = (Spinner) dialogView.findViewById(R.id.color_spinner);
				
				builder
				.setMessage("Изменить день")
				.setView(dialogView)
				.setCancelable(true)
				.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Помещаемые значения
						ContentValues cv = new ContentValues();
						cv.put(DBHelper.TIMETABLE_SPECIALTY_ID, specialtiesSpinner.getSelectedItemId());
						cv.put(DBHelper.TIMETABLE_CLASSROOM, classroom.getText().toString());
						cv.put(DBHelper.TIMETABLE_COLOR, colorSpinner.getSelectedItem().toString());
						mdbHelper.getWritableDatabase()
							.update(
									DBHelper.TIMETABLE,
									cv,
									DBHelper.TIMETABLE_ID+"=? AND "+DBHelper.TIMETABLE_WEEK+"=?",
									new String[]{String.valueOf(idTimetable), String.valueOf(currentWeek)});
						dialog.dismiss();
					}
				})
				.setNegativeButton("Отмена", null);
			}
				break;

			default:
				throw new InvalidKeyException("No such ID in DialogBuilder.DialogID");
			}
			// Возвращаем созданное окно и записываем как текущее
			return mCurrentDialogInstance = builder.create();
			} catch (InvalidKeyException e) {
				return null;
			}
		}
		@Override
		public void onDismiss(DialogInterface dialog) {
			// TODO Auto-generated method stub
			super.onDismiss(dialog);
			mDialogCallbacks.onDialogDismiss(mCurrentDialogId);
		}
		public static interface DialogCallbacks{
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
