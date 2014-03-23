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
	
	private NavigationDrawerFragment mNavigationDrawerFragment; // ����������� ����� Navigation Drawer'a
	private TimetableFragment mTimetableFragment;
	private CharSequence mTitle; // �������� �������� ����
	
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
			mTitle = mTimetableFragment.getTitle();
			break;
		case 1:// ���� ������ (�������������� ����� NavigationDrawer'��
			break;
		case 2:// ���������
			break;
		case 3:// �����
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
	/**
	 * ����������� ActionBar
	 */
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
	
    /**
     * �����, ����������� ���������
     */
    // TODO �������� ��� ������ ��������
	public static class DialogBuilder extends DialogFragment {
		/** ������� ������� ������ */
		private static Dialog mCurrentDialogInstance = null;
		/** ID �������� ������� */
		private static IdDialog mCurrentDialogId = null;
		/** ��������� ��� ������ � ����������� */
		private DialogCallbacks mDialogCallbacks;
		public static enum IdDialog { Timetable_ChangeDay }; // ���� ��������� Id ����� ��������
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
			// TODO ���� �������
			final DBHelper mdbHelper = new DBHelper(getActivity());
			// ������� ��������� ���� ��������
			try {
			AlertDialog.Builder builder = new Builder(getActivity());
			mCurrentDialogId = IdDialog.valueOf(getArguments().getString("idDialog"));
			switch (mCurrentDialogId) {
			case Timetable_ChangeDay:{ // ������ �� ������� ������� �� ������ � ���������� ��������� TimetableFragment
				
				final int idTimetable = getArguments().getInt("idTimetable");
				// �������� ��������� �������� ��� ���������� �������� ������� �� ��
				Cursor dayInfo = ((MainActivity)getActivity()).getDbHelper().getReadableDatabase().query(
						TABLES.TIMETABLE+" LEFT OUTER JOIN "+TABLES.SPECIALTY+" ON "+SPECIALTY.fID+"="+ SPECIALTY.fID,
						new String[]{TIMETABLE.fID, TIMETABLE.CLASSROOM, TIMETABLE.COLOR, TIMETABLE.WEEK, SPECIALTY.fID},
						TIMETABLE.fID+"=?",
						new String[]{String.valueOf(idTimetable)},
						null, null, null);
				dayInfo.moveToFirst();
				
				final String currentClassroom = dayInfo.getString(dayInfo.getColumnIndex(TIMETABLE.CLASSROOM));
				final int currentSpecialtyPosition = dayInfo.getInt(dayInfo.getColumnIndex(SPECIALTY.ID)) - 1;
				final int currentWeek = dayInfo.getInt(dayInfo.getColumnIndex(TIMETABLE.WEEK));
				
				// �������� ������������ ���� ��� �������, �������� �������� � ���������� ����
				View dialogView = View.inflate(getActivity(), R.layout.dialog_timetable_griditemlongclick, null);
				// ����� ���������
				final EditText classroom = (EditText) dialogView.findViewById(R.id.classroom);
				classroom.setText(currentClassroom);
				// ������� ������ �����
				final Spinner specialtiesSpinner = (Spinner) dialogView.findViewById(R.id.specialties_spinner);
				// ���������� ������ ������ ����� ��������
				specialtiesSpinner.setAdapter(new SimpleCursorAdapter(
						getActivity(),
						android.R.layout.simple_spinner_item,
						mdbHelper.getReadableDatabase().query(TABLES.SPECIALTY, null, null, null, null, null, null),
						new String[]{SPECIALTY.NAME},
						new int[]{android.R.id.text1},
						0));
				specialtiesSpinner.setSelection(currentSpecialtyPosition);
				// ������ �����
				final Spinner colorSpinner = (Spinner) dialogView.findViewById(R.id.color_spinner);
				
				builder
				.setMessage("�������� ����")
				.setView(dialogView)
				.setCancelable(true)
				.setPositiveButton("���������", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//���������� ��������
						ContentValues cv = new ContentValues();
						cv.put(TIMETABLE.SPECIALTY_ID, specialtiesSpinner.getSelectedItemId());
						cv.put(TIMETABLE.CLASSROOM, classroom.getText().toString());
						cv.put(TIMETABLE.COLOR, colorSpinner.getSelectedItem().toString());
						mdbHelper.getWritableDatabase()
							.update(
									TABLES.TIMETABLE,
									cv,
									TIMETABLE.ID+"=? AND "+TIMETABLE.WEEK+"=?",
									new String[]{String.valueOf(idTimetable), String.valueOf(currentWeek)});
						dialog.dismiss();
					}
				})
				.setNegativeButton("������", null);
			}
				break;

			default:
				throw new InvalidKeyException("No such ID in DialogBuilder.DialogID");
			}
			// ���������� ��������� ���� � ���������� ��� �������
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
