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
	
	private String mTitle = "����������"; // ���������
	public String getmTitle() {
		return mTitle;
	}
	LinearLayout mTimetableLayout; // ��� ������ ����������
	GridView mPairTimeGrid; // ����� ������� ���
	GridView mTimetableGrid; // ����� ����������
	ListView mDayList; // ���� ���� ������
	DBHelper mdbHelper; // ����� ������ � ��
	int mCurrentWeek; // ������� ������ (1/2)
	
	public TimetableFragment(){}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO �������� actionbar
		super.onCreate(savedInstanceState);
		// ���� ���� ����������� ��������� - �������� ������ ������, ����� ����� ������
		mCurrentWeek = savedInstanceState == null ? 1 : savedInstanceState.getInt("mCurrentWeek");
		
		// TODO �������� - ���� � ��� �� ������ �� ���� �������.
		mdbHelper = new DBHelper(getActivity());
		setHasOptionsMenu(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO ����� ����� ���������� ��������?
		// ���� ��� �� ���������� - �������, ����� ������� ��� ������������ �������
		mTimetableLayout = (LinearLayout) inflater.inflate(R.layout.fragment_timetable, container, false);// ��� ������ ����������
		// ���� ���� ������
		mDayList = (ListView) mTimetableLayout.findViewById(R.id.day_list);
		// Header ��� ���� ������ (����)
		TextView DayListHeader = (TextView) inflater.inflate(R.layout.pair_time_list_item_1, null);
		DayListHeader.setText("����");
		mDayList.addHeaderView(DayListHeader);
		// TODO ���� �������� ���������, ���� ��������� ������� �������� ��� � ���������� ������
		final Calendar calendar = Calendar.getInstance(); // �������� �� ����� ������� � ��������
		mDayList.setAdapter(new ArrayAdapter<String>(
				getActivity(),
				R.layout.timetable_grid_item_2,
				R.id.text1,
				new String[]{"��", "��", "��", "��", "��", "��"}){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null)
					convertView = getActivity().getLayoutInflater().inflate(R.layout.timetable_grid_item_2, parent, false);
				
				((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position)); // ���� ������
				((TextView) convertView.findViewById(R.id.text2)).setText((calendar.get(Calendar.DAY_OF_WEEK)==(position+2)?"�������":String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)+position+2-calendar.get(Calendar.DAY_OF_WEEK))+" "+calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK))); // ���� ������
				
				return convertView;
			}});
		// ����� ����������
		mTimetableGrid = (GridView) mTimetableLayout.findViewById(R.id.timetable_grid);
		// ���������� ������� �� ���� ������
		mTimetableGrid.setAdapter(new SimpleCursorAdapter(// ���� ��������� �������: 1lvl - �������� ������, 2lvl - ����� ���������
				getActivity(),
				R.layout.timetable_grid_item_2,
				null,
				new String[]{DBHelper.SPECIALTY_NAME, DBHelper.TIMETABLE_CLASSROOM},
				new int[]{android.R.id.text1, android.R.id.text2},
				0){
			@Override
			public void bindView(View view, Context context, Cursor cursor) {
				// TODO �������� ���������� ��������� ����� � ������� �� ������� (?) �������� � ��
				((TextView) view.findViewById(R.id.text1)).setText(cursor.getString(cursor.getColumnIndex(DBHelper.SPECIALTY_NAME)));// �������� ������
				((TextView) view.findViewById(R.id.text2)).setText(cursor.getString(cursor.getColumnIndex(DBHelper.TIMETABLE_CLASSROOM)));// ����� ���������
				// TODO �������� ����� ������� ����
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
		// TODO ��������� ������ ������� (����� ������� ��������� ���)
		mTimetableGrid.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO ������ �������� ������� �� ������, ������������ ����������� �����
				final long idTimetable=id;
				// �������� ������������ ���� ��� �������, �������� �������� � ���������� ����
				View dialogView = View.inflate(getActivity(), R.layout.dialog_timetable_griditemlongclick, null);
				// ����� ���������
				final EditText classroom = (EditText) dialogView.findViewById(R.id.classroom);
				classroom.setText(((TextView)view.findViewById(R.id.text2)).getText());
				// ������� ������ �����
				final Spinner specialtiesSpinner = (Spinner) dialogView.findViewById(R.id.specialties_spinner);
				// ���������� ������ ������ ����� ��������
				specialtiesSpinner.setAdapter(new SimpleCursorAdapter(
						getActivity(),
						android.R.layout.simple_spinner_item,
						mdbHelper.getReadableDatabase().query(DBHelper.SPECIALTY, null, null, null, null, null, null),
						new String[]{DBHelper.SPECIALTY_NAME},
						new int[]{android.R.id.text1},
						0));
				//specialtiesSpinner.setPrompt(((TextView)view.findViewById(R.id.text1)).getText());
				// ��������� ������� ������ TODO �� �� ����� ��������� ������ ��
				Cursor groupName = mdbHelper.getReadableDatabase().query(
						DBHelper.SPECIALTY,
						new String[]{DBHelper.SPECIALTY_ID},
						DBHelper.SPECIALTY_NAME+"=?",
						new String[]{((TextView)view.findViewById(R.id.text1)).getText().toString()}, null, null, null);
				groupName.moveToFirst();
				specialtiesSpinner.setSelection(groupName.getInt(0)-1);
				
				AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
				builder
					.setMessage("�������� ����")
					.setView(dialogView)
					.setCancelable(true)
					.setPositiveButton("���������", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//���������� ��������
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
							refillTimetable();// ��� ������ �������� �������� ����, � ������� ������
						}
					})
					.setNegativeButton("������", null)
					.create()
					.show();
				return true;
			}
		});
		
		// ����� ������� ���
		mPairTimeGrid = (GridView) mTimetableLayout.findViewById(R.id.pairtime_grid);
		// TODO �������� �����, �������� ����������� ���������
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
		// ��� �������� ��������� ����������
		refillTimetable();
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// ��� �������� ��������� ������� ������ TODO ��������� ��� ���� ������ �� ^^
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
		// �������� ������� �������� ������ � actionbar'e
		menu.findItem(R.id.current_week).setTitle("���� "+String.valueOf(mCurrentWeek)+" ������");
		super.onPrepareOptionsMenu(menu);
	}
	// ������������ ����������
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
