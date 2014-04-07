package com.teacherse_planner;

import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.Locale;


import com.teacherse_planner.DBHelper.TABLES;
import com.teacherse_planner.DBHelper.TABLES.HOMEREADING;
import com.teacherse_planner.DBHelper.TABLES.HOMEWORK;
import com.teacherse_planner.DBHelper.TABLES.SPECIALTY;
import com.teacherse_planner.DBHelper.TABLES.SPECIALTY_CLASSES;
import com.teacherse_planner.DBHelper.TABLES.SPECIALTY_CLASSES_DATE;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
	/** Фрагмент карточка студента */
	private StudentCardFragment mStudentCardFragment;
	/** Заголовок текущего окна */
	private CharSequence mTitle;
	
	public Calendar getCalendar(){
		return mCalendar;
	}
	public DBHelper getDbHelper(){
		return mdbHelper;
	}
	// TODO убрать? используется пока только в создании диалогов для получения недели
	public TimetableFragment getTimetableFragment() {
		return mTimetableFragment;
	}
	public SpecialtytableFragment getSpecialtytableFragment(){
		return mSpecialtytableFragment;
	}
	public StudentCardFragment getStudentCardFragment(){
		return mStudentCardFragment;
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
		case 3:// Действия с базой
			break;
		case 4:// Выбор дисциплины
			break;
		case 5:// Выход
			finish();
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
    public int getColor(String colorName){
		String[] colorNames = getResources().getStringArray(R.array.color_names);
		int colorIndex = 0;
		for(int i=0;i<colorNames.length;++i){
			if(colorNames[i].compareTo(colorName) == 0){
				colorIndex = i;
				break;
			}
		}
		int[] colorCodes = getResources().getIntArray(R.array.color_codes);
    	return colorCodes[colorIndex];
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
		public static enum IdDialog { CHANGE_DAY, ADD_SPECIALTY, ADD_STUDENT, ADD_LESSON_DATE, CHANGE_LESSON_DATE, CHOOSE_CLASS, SHOW_CLASS, ADD_HOMEWORK, ADD_HOMEWORK_RESULT, ADD_HOMEREADING }; // Сюда добавлять Id новых диалогов
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
			builder.setCancelable(true);
			try {
				mCurrentDialogId = IdDialog.valueOf(getArguments().getString("idDialog"));
			switch (mCurrentDialogId) {
			case CHANGE_DAY:{ // Диалог по долгому нажатию на клетку в расписании фрагмента TimetableFragment
				final long idTimetable = getArguments().getLong("itemId");
				final int currentWeek = ((MainActivity) mContext).getTimetableFragment().getWeek();
				SQLiteDatabase db = mdbHelper.getReadableDatabase();
				// Получаем требуемые значения для заполнения текущими данными из БД
				Cursor dayInfo = db.query(
						TABLES.TIMETABLE+" INNER JOIN "+TABLES.SPECIALTY+" ON "+TIMETABLE.SPECIALTY_ID+"="+ SPECIALTY.aID,
						new String[]{TIMETABLE.fID+" AS "+TIMETABLE.aID, TIMETABLE.CLASSROOM, TIMETABLE.COLOR, TIMETABLE.WEEK, SPECIALTY.fID+" AS "+SPECIALTY.aID},
						TIMETABLE.aID+"=? AND "+TIMETABLE.WEEK+"=?",
						new String[]{String.valueOf(idTimetable), String.valueOf(currentWeek)},
						null, null, null);
				dayInfo.moveToFirst();
				// Пуст ли текущий день?
				final boolean dayInfoIsEmpty = dayInfo.getCount() == 0 ? true : false;
				
				// Массивы для работы с цветом
				final int[] colorCodes = getResources().getIntArray(R.array.color_codes);
				final String[] colorNames = getResources().getStringArray(R.array.color_names);
				
				// Текущие значение (на момент открытия диалога)
				final String currentClassroom = dayInfoIsEmpty ? "" : dayInfo.getString(dayInfo.getColumnIndex(TIMETABLE.CLASSROOM));
				final int currentSpecialtyPosition = dayInfoIsEmpty ? 0 : dayInfo.getInt(dayInfo.getColumnIndex(SPECIALTY.aID)) - 1;
				String currentColorName = dayInfoIsEmpty ? colorNames[0] : dayInfo.getString(dayInfo.getColumnIndex(TIMETABLE.COLOR));
				
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
				int currentColorPos = 0;
				for(int i=0;i < colorNames.length; ++i) {
					if(colorNames[i].compareTo(currentColorName) == 0){
						currentColorPos = i;
						break;
					}
				}
				ArrayAdapter<String> colorSpinnerAdapter = new ArrayAdapter<String>(
						getActivity(),
						android.R.layout.simple_spinner_item,
						colorNames){
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						View view = super.getView(position, convertView, parent);
						view.setBackgroundColor(colorCodes[position]);
						((TextView)view.findViewById(android.R.id.text1)).setText(colorNames[position]);
						return view;
					}
					@Override
					public View getDropDownView(int position, View convertView, ViewGroup parent) {
						View view =  super.getDropDownView(position, convertView, parent);
						view.setBackgroundColor(colorCodes[position]);
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
				.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SQLiteDatabase db = mdbHelper.getWritableDatabase();
						
						// Помещаемые значения
						long selectedSpecialityId = specialtiesSpinner.getSelectedItemId();
						String selectedClassroom = classroom.getText().toString();
						String selectedColor = (String) colorSpinner.getSelectedItem();
						
						int selectedColorPosition = colorSpinner.getSelectedItemPosition();
						//Пустые ли новые значения
						boolean newdayInfoisEmpty = (selectedSpecialityId == 1 && selectedClassroom.length() == 0 && selectedColorPosition == 0) ? true : false;
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
			case ADD_SPECIALTY:{ // Диалог добавления новой группы
				try {
					mDialogCallbacks = (DialogCallbacks) getFragmentManager().findFragmentById(R.id.navigation_drawer);
	        	} catch (ClassCastException e) {
	        		throw new ClassCastException("Fragment must implement DialogCallbacks.");
	        	}
				final EditText editSpecialty = new EditText(mContext);
				editSpecialty.setHint("Введите название группы");
				builder
					.setMessage("Добавить группу")
					.setView(editSpecialty)
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
			case ADD_STUDENT: // Добавить студента
				((MainActivity) getActivity()).getSpecialtytableFragment().refillStudentList();
				break;
			case ADD_LESSON_DATE:case CHANGE_LESSON_DATE:{ // Диалог добавления нового занятия (даты)
				final DatePicker datePicker = new DatePicker(mContext);
				builder
					.setView(datePicker)
					.setPositiveButton(R.string.add_lesson, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Добавить новое занятие, выбрав дату
							long specialtyId = getArguments().getLong("mCurrentSpecialityId");
							
							SQLiteDatabase db = mdbHelper.getWritableDatabase();
							
							Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
							calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
							
							ContentValues cv = new ContentValues();
							cv.put(SPECIALTY_CLASSES_DATE.SPECIALTY_ID, specialtyId);
							cv.put(SPECIALTY_CLASSES_DATE.DATE, calendar.getTimeInMillis());
							
							if(mCurrentDialogId == IdDialog.CHANGE_LESSON_DATE) {
								String lessonId = String.valueOf(getArguments().getInt("lessonId"));
								db.update(TABLES.SPECIALTY_CLASSES_DATE, cv, SPECIALTY_CLASSES_DATE.ID+"=?", new String[]{lessonId});
							}else
								db.insert(TABLES.SPECIALTY_CLASSES_DATE, null, cv);
							
							db.close();
							dialog.dismiss();
						}
					})
					.setNegativeButton(R.string.cancel, null);
			}
				break;
			case CHOOSE_CLASS:{ // Выбор типа занятия
				
				View dialogView = View.inflate(mContext, R.layout.dialog_student_choose_class, null);
				
				View.OnClickListener onChoiceClkLst = new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						getCurrentDialog().dismiss();
						DialogBuilder.IdDialog currentIdDialog;
						switch (v.getId()) {
						case R.id.homereading:
							currentIdDialog = DialogBuilder.IdDialog.ADD_HOMEREADING;
							break;
						case R.id.mark:
							currentIdDialog = DialogBuilder.IdDialog.ADD_HOMEWORK_RESULT;
							break;
						default:
							currentIdDialog = null;
							break;
						}
						Bundle dialogInfo = new Bundle(getArguments());
						dialogInfo.putString("idDialog", currentIdDialog.toString());
						dialogInfo.putInt("classTypeId", v.getId());
						newInstance(mContext, dialogInfo).show(getFragmentManager(), currentIdDialog.toString());
					}
				};
				
				Button homereadingButton = (Button) dialogView.findViewById(R.id.homereading);
				Button homeworkButton = (Button) dialogView.findViewById(R.id.mark);
				homereadingButton.setOnClickListener(onChoiceClkLst);
				homeworkButton.setOnClickListener(onChoiceClkLst);

				builder.setView(dialogView);
			}
				break;
			case SHOW_CLASS:{ // Просмотр оценок текущего ученика
				
				final int lessonId = getArguments().getInt("lessonId");
				final int studentId = getArguments().getInt("studentId");
				
				SQLiteDatabase db = mdbHelper.getReadableDatabase();
				
				View dialogView = View.inflate(mContext, R.layout.dialog_student_class_item, null);
				
				LinearLayout classesBlock = (LinearLayout) dialogView.findViewById(R.id.classes_block);
				final Button addClass = (Button) dialogView.findViewById(R.id.add);
				addClass.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Bundle dialogInfo = new Bundle(getArguments());
						dialogInfo.putString("idDialog", DialogBuilder.IdDialog.CHOOSE_CLASS.toString());
						newInstance(mContext, dialogInfo).show(getFragmentManager(), DialogBuilder.IdDialog.CHOOSE_CLASS.toString());
					}
				});
				
				final CheckBox naStatus = (CheckBox) dialogView.findViewById(R.id.na_status);
				naStatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						addClass.setEnabled(!isChecked);
					}
				});
				
				Cursor classesData = db.query(
						TABLES.SPECIALTY_CLASSES+" JOIN "+TABLES.SPECIALTY_CLASSES_DATE+" ON "+SPECIALTY_CLASSES.fSPECIALTY_CLASSES_DATE_ID+"="+SPECIALTY_CLASSES_DATE.fID,
						new String[]{SPECIALTY_CLASSES_DATE.DATE, SPECIALTY_CLASSES_DATE.SPECIALTY_ID, SPECIALTY_CLASSES.CLASS_ID, SPECIALTY_CLASSES.CLASS_TYPE},
						SPECIALTY_CLASSES.STUDENT_ID+"=? AND "+SPECIALTY_CLASSES_DATE.fID+"=?",
						new String[]{String.valueOf(String.valueOf(studentId)), String.valueOf(lessonId)},
						null, null, null);
				
				View.OnClickListener onClassClkLst = new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						DialogBuilder.IdDialog currentIdDialog;
						switch (((Button) v).getText().toString()) {
						case TABLES.HOMEREADING:
							currentIdDialog = DialogBuilder.IdDialog.ADD_HOMEREADING;
							break;
						case TABLES.HOMEWORK_RESULT:
							currentIdDialog = DialogBuilder.IdDialog.ADD_HOMEWORK_RESULT;
							break;
						default:
							currentIdDialog = null;
							break;
						}
						Bundle dialogInfo = new Bundle(getArguments());
						dialogInfo.putString("idDialog", currentIdDialog.toString());
						dialogInfo.putInt("classTypeId", v.getId());
						newInstance(mContext, dialogInfo).show(getFragmentManager(), currentIdDialog.toString());
					}
				};
				
				for(int i = 0; i < classesData.getCount(); ++i) {
					classesData.moveToPosition(i);
										
					Button classView  = new Button(getActivity());
					
					String classType = classesData.getString(classesData.getColumnIndex(SPECIALTY_CLASSES.CLASS_TYPE));
					int classTypeId = classesData.getInt(classesData.getColumnIndex(SPECIALTY_CLASSES.CLASS_ID));
					
					if(classType.compareTo(TABLES.NA) == 0) {
						naStatus.setChecked(true);
						break;
					} else {
						classView.setText(classType);
						classView.setId(classTypeId);
						classView.setOnClickListener(onClassClkLst);
						
						classesBlock.addView(classView);
					}
				}
				
				builder
					.setView(dialogView)
					.setPositiveButton(R.string.save, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(naStatus.isChecked()) {
								SQLiteDatabase db = mdbHelper.getWritableDatabase();
								ContentValues cv = new ContentValues();
															
								cv.put(SPECIALTY_CLASSES.SPECIALTY_CLASSES_DATE_ID, lessonId);
								cv.put(SPECIALTY_CLASSES.STUDENT_ID, studentId);
								cv.put(SPECIALTY_CLASSES.CLASS_TYPE, TABLES.NA);
								cv.putNull(SPECIALTY_CLASSES.CLASS_ID);
								
								db.insert(TABLES.SPECIALTY_CLASSES, null, cv);
								db.close();
							}
							dialog.dismiss();
						}
					})
					.setNegativeButton(R.string.cancel, null);
				db.close();
			}
				break;
			case ADD_HOMEREADING:{ // Добавить оценку за домашнее чтение
				
				final int lessonId = getArguments().getInt("lessonId");
				final int studentId = getArguments().getInt("studentId");
				final int classTypeId = getArguments().getInt("classTypeId");

				View dialogView = View.inflate(mContext, R.layout.dialog_student_homereading, null);
				
				final EditText symbols = (EditText) dialogView.findViewById(R.id.symbols);
				final EditText words = (EditText) dialogView.findViewById(R.id.words);
				final CheckBox translating = (CheckBox) dialogView.findViewById(R.id.translating);
				final CheckBox retteling = (CheckBox) dialogView.findViewById(R.id.retteling);
				
				final Cursor homereadingData = mdbHelper.getHomereading(String.valueOf(classTypeId));
				
				if(homereadingData.getCount() != 0) {
					homereadingData.moveToFirst();
					
					symbols.setText(homereadingData.getString(homereadingData.getColumnIndex(HOMEREADING.SYMBOLS)));
					words.setText(homereadingData.getString(homereadingData.getColumnIndex(HOMEREADING.WORDS)));
					translating.setEnabled(homereadingData.getInt(homereadingData.getColumnIndex(HOMEREADING.TRANSLATING)) == 0 ? false : true);
					retteling.setEnabled(homereadingData.getInt(homereadingData.getColumnIndex(HOMEREADING.RETELLING)) == 0 ? false : true);
				}
				
				builder
					.setView(dialogView)
					.setPositiveButton(R.string.save, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							SQLiteDatabase db = mdbHelper.getWritableDatabase();
							
							ContentValues cv = new ContentValues();
							cv.put(HOMEREADING.SYMBOLS, symbols.getText().toString());
							cv.put(HOMEREADING.WORDS, words.getText().toString());
							cv.put(HOMEREADING.TRANSLATING, translating.isChecked() ? 1 : 0);
							cv.put(HOMEREADING.RETELLING, retteling.isChecked() ? 1 : 0);
							
							if(homereadingData.getCount() == 0) {
								long hrId = db.insert(TABLES.HOMEREADING, null, cv);
								cv.clear();
								cv.put(SPECIALTY_CLASSES.SPECIALTY_CLASSES_DATE_ID, lessonId);
								cv.put(SPECIALTY_CLASSES.STUDENT_ID, studentId);
								cv.put(SPECIALTY_CLASSES.CLASS_TYPE, TABLES.HOMEREADING);
								cv.put(SPECIALTY_CLASSES.CLASS_ID, hrId);
								db.insert(TABLES.SPECIALTY_CLASSES, null, cv);
							} else {
								db.update(TABLES.HOMEREADING, cv, HOMEREADING.ID+"=?", new String[]{String.valueOf(classTypeId)});
							}
							
							db.close();
							dialog.dismiss();
						}
					})
					.setNegativeButton(R.string.cancel, null);
			}
				break;
			case ADD_HOMEWORK:{ // Добавить домашнее задание
				
				final int homeworkId = getArguments().getInt("homeworkId");
								
				SQLiteDatabase db = mdbHelper.getReadableDatabase();
				
				View dialogView = View.inflate(mContext, R.layout.dialog_student_homework, null);
				
				final Cursor homeworkCursor = db.query(
						TABLES.HOMEWORK,
						null,
						HOMEWORK.ID+"=?",
						new String[]{String.valueOf(homeworkId)},
						null, null, null);
				
				final EditText homework = (EditText) dialogView.findViewById(R.id.homework);
				if(homeworkCursor.getCount() != 0){
					homeworkCursor.moveToFirst();
					homework.setText(homeworkCursor.getString(homeworkCursor.getColumnIndex(HOMEWORK.HOMEWORK_TEXT)));
				}
				
				builder
					.setView(dialogView)
					.setPositiveButton(R.string.save, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							SQLiteDatabase db = mdbHelper.getWritableDatabase();
							if(homework.getText().length() != 0) {
								
								ContentValues cv = new ContentValues();
								cv.put(HOMEWORK.HOMEWORK_TEXT, homework.getText().toString());
								
								if(homeworkCursor.getCount() != 0){
									db.update(TABLES.HOMEWORK, cv, HOMEWORK.ID+"=?", new String[]{String.valueOf(homeworkId)});
								} else {
									cv.put(HOMEWORK.ID, homeworkId);
									db.insert(TABLES.HOMEWORK, null, cv);
								}
							} else {
								db.delete(TABLES.HOMEWORK, HOMEWORK.ID+"=?", new String[]{String.valueOf(homeworkId)});
							}
							db.close();
							dialog.dismiss();
						}
					})
					.setNegativeButton(R.string.cancel, null);
				db.close();
			}
				break;
			case ADD_HOMEWORK_RESULT:{ // Добавить оценку за домащнюю работу
				final int lessonId = getArguments().getInt("lessonId");
				
				SQLiteDatabase db = mdbHelper.getReadableDatabase();
				
				final Cursor homeworkData = db.query(
						TABLES.HOMEWORK,
						null,
						HOMEWORK.ID+"=?",
						new String[]{String.valueOf(lessonId)},
						null, null, null);
				
				ViewGroup dialogView = (ViewGroup) View.inflate(mContext, R.layout.dialog_student_homework, null);
				final EditText mark = new EditText(mContext);
				if(homeworkData.getCount() != 0) {
					mark.setText(homeworkData.getString(homeworkData.getColumnIndex(HOMEWORK.HOMEWORK_TEXT)));
				}
				dialogView.addView(mark);
				
				builder
					.setView(dialogView)
					.setPositiveButton(R.string.add, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//if(homeworkData.getCount())
							dialog.dismiss();
						}
					})
					.setNegativeButton(R.string.cancel, null);
				db.close();
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
