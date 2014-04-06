package com.teacherse_planner;

import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.Locale;

import com.teacherse_planner.DBHelper.TABLES;
import com.teacherse_planner.DBHelper.TABLES.SPECIALTY;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements NavigationDrawerCallbacks {
	
	private Calendar mCalendar;
	private DBHelper mdbHelper;
	/** ����������� ����� Navigation Drawer'a */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	/** �������� ���������� */
	private TimetableFragment mTimetableFragment;
	/** �������� ������ ������ */
	private SpecialtytableFragment mSpecialtytableFragment;
	/** �������� �������� �������� */
	private StudentCardFragment mStudentCardFragment;
	/** ��������� �������� ���� */
	private CharSequence mTitle;
	
	public Calendar getCalendar(){
		return mCalendar;
	}
	public DBHelper getDbHelper(){
		return mdbHelper;
	}
	// TODO ������? ������������ ���� ������ � �������� �������� ��� ��������� ������
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
		// ������� ��������������� ������� ���������, �� ���� ���������� ����� ������������ ������
		mdbHelper = new DBHelper(this);
		mCalendar = Calendar.getInstance();
		mTitle = getTitle();
		
		setContentView(R.layout.activity_main_screen);
		// ����� �������� Navigation Drawer'a � ������� ��� ���������
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
		// TODO ��������� ��������� ������� ��������
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
		// TODO ����������� ���� � ����������� �� �������
		FragmentTransaction NewTransaction = getFragmentManager().beginTransaction();
		switch(position){
		case 0:// ����������
			if(mTimetableFragment == null)
				mTimetableFragment = new TimetableFragment();
			NewTransaction
				.replace(R.id.container, mTimetableFragment);
			break;
		case 1:// ���� ������ (�������������� ����� NavigationDrawer'��
			break;
		case 2:// ���������
			break;
		case 3:// �����
			finish();
			break;
		}
		NewTransaction.commit();
	}
	/** ����������� ActionBar */
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
     * �����, ����������� ���������
     */
	public static class DialogBuilder extends DialogFragment {
		/** ������� ������� ������ */
		private static Dialog mCurrentDialogInstance = null;
		/** ID �������� ������� */
		private static IdDialog mCurrentDialogId = null;
		/** ������� �������� */
		private static Activity mContext;
		/** ��������� ��� ������ � ����������� */
		private DialogCallbacks mDialogCallbacks;
		public static enum IdDialog { CHANGE_DAY, ADD_SPECIALTY, ADD_STUDENT, ADD_LESSON_DATE, CHANGE_LESSON_DATE, ADD_CLASS, SHOW_CLASS, ADD_HOMEWORK, ADD_HOMEREADING }; // ���� ��������� Id ����� ��������
		/** ���������� ������� ������ 
		 * @return ������� ������ ��� null
		 */
		public static Dialog getCurrentDialog(){
			return mCurrentDialogInstance;
		}
		/** ���������� ID �������� ������� 
		 * @return ID �������� ������� ��� null
		 */
		public static IdDialog getCurrentDialogId(){
			return mCurrentDialogId;
		}
		/** ������� ����� ������
		 * 
		 * @param dialogInfo - ���� ������������ ����������
		 * @return ����� ������ DialogBuilder
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
			// TODO ���� �������
			final DBHelper mdbHelper = new DBHelper(getActivity());
			// ������� ��������� ���� ��������
			AlertDialog.Builder builder = new Builder(getActivity());
			builder.setCancelable(true);
			try {
				mCurrentDialogId = IdDialog.valueOf(getArguments().getString("idDialog"));
			switch (mCurrentDialogId) {
			case CHANGE_DAY:{ // ������ �� ������� ������� �� ������ � ���������� ��������� TimetableFragment
				final long idTimetable = getArguments().getLong("idTimetable");
				final int currentWeek = ((MainActivity) mContext).getTimetableFragment().getWeek();
				SQLiteDatabase db = mdbHelper.getReadableDatabase();
				// �������� ��������� �������� ��� ���������� �������� ������� �� ��
				Cursor dayInfo = db.query(
						TABLES.TIMETABLE+" INNER JOIN "+TABLES.SPECIALTY+" ON "+TIMETABLE.SPECIALTY_ID+"="+ SPECIALTY.aID,
						new String[]{TIMETABLE.fID+" AS "+TIMETABLE.aID, TIMETABLE.CLASSROOM, TIMETABLE.COLOR, TIMETABLE.WEEK, SPECIALTY.fID+" AS "+SPECIALTY.aID},
						TIMETABLE.aID+"=? AND "+TIMETABLE.WEEK+"=?",
						new String[]{String.valueOf(idTimetable), String.valueOf(currentWeek)},
						null, null, null);
				dayInfo.moveToFirst();
				// ���� �� ������� ����?
				final boolean dayInfoIsEmpty = dayInfo.getCount() == 0 ? true : false;
				
				// ������� ��� ������ � ������
				final int[] colorCodes = getResources().getIntArray(R.array.color_codes);
				final String[] colorNames = getResources().getStringArray(R.array.color_names);
				
				// ������� �������� (�� ������ �������� �������)
				final String currentClassroom = dayInfoIsEmpty ? "" : dayInfo.getString(dayInfo.getColumnIndex(TIMETABLE.CLASSROOM));
				final int currentSpecialtyPosition = dayInfoIsEmpty ? 0 : dayInfo.getInt(dayInfo.getColumnIndex(SPECIALTY.aID)) - 1;
				String currentColorName = dayInfoIsEmpty ? colorNames[0] : dayInfo.getString(dayInfo.getColumnIndex(TIMETABLE.COLOR));
				
				// �������� ������������ ���� ��� �������, �������� �������� � ���������� ����
				View dialogView = View.inflate(getActivity(), R.layout.dialog_timetable_griditemlongclick, null);
				// ����� ���������
				final EditText classroom = (EditText) dialogView.findViewById(R.id.classroom);
				classroom.setText(currentClassroom);
				// ������� ������ �����
				final Spinner specialtiesSpinner = (Spinner) dialogView.findViewById(R.id.specialties_spinner);
				// ���������� ������ ������ ����� ��������
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
				// ������ �����
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
				.setMessage("�������� ����")
				.setView(dialogView)
				.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SQLiteDatabase db = mdbHelper.getWritableDatabase();
						
						//���������� ��������
						long selectedSpecialityId = specialtiesSpinner.getSelectedItemId();
						String selectedClassroom = classroom.getText().toString();
						String selectedColor = (String) colorSpinner.getSelectedItem();
						
						int selectedColorPosition = colorSpinner.getSelectedItemPosition();
						//������ �� ����� ��������
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
			case ADD_SPECIALTY:{ // ������ ���������� ����� ������
				try {
					mDialogCallbacks = (DialogCallbacks) getFragmentManager().findFragmentById(R.id.navigation_drawer);
	        	} catch (ClassCastException e) {
	        		throw new ClassCastException("Fragment must implement DialogCallbacks.");
	        	}
				final EditText editSpecialty = new EditText(mContext);
				editSpecialty.setHint("������� �������� ������");
				builder
					.setMessage("�������� ������")
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
			case ADD_STUDENT:
				((MainActivity) getActivity()).getSpecialtytableFragment().refillStudentList();
				break;
			case ADD_LESSON_DATE:case CHANGE_LESSON_DATE:{ // ������ ���������� ������ ������� (����)
				final DatePicker datePicker = new DatePicker(mContext);
				builder
					.setView(datePicker)
					.setPositiveButton(R.string.add_lesson, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// �������� ����� �������, ������ ����
							SQLiteDatabase db = mdbHelper.getWritableDatabase();
							Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
							calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
							long specialtyId = getArguments().getLong("mCurrentSpecialityId");
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
			case SHOW_CLASS:{ // �������� ������ �������� �������

			}
				break;
			case ADD_HOMEREADING:{ // �������� ������ �� �������� ������
				
			}
				break;
			case ADD_HOMEWORK:{ // �������� ������ �� �������� ������
				
			}
				break;
			default:
				throw new InvalidKeyException("No such ID in DialogBuilder.IdDialog");
			}
			} catch (InvalidKeyException e) {
				return null;
			}
			// ���������� ��������� ���� � ���������� ��� �������
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
			/** ���������� ����� ������ OnDismiss() 
			 * @param dialogId - ID ������� */
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
