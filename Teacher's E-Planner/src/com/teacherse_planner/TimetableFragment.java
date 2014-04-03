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
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/** ���� ���������� �� 2 ������ */
public class TimetableFragment extends Fragment implements MainActivity.DialogBuilder.DialogCallbacks {
	/** ��������� */
	private String mTitle = "����������";
	public String getTitle() {
		return mTitle;
	}
	/** ��� ������ ���������� */
	LinearLayout mTimetableLayout;
	/** ����� ������� ��� */
	GridView mPairTimeGrid;
	/** ����� ���������� */
	GridView mTimetableGrid;
	/** ���� ���� ������ */
	ListView mDayList;
	/** ������ ��� ������ � �� */
	DBHelper mdbHelper;
	/** ������� ������ (1/2) */
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
		// TODO �������� actionbar
		super.onCreate(savedInstanceState);
		// ���� ���� ����������� ��������� - �������� ������ ������, ����� ����� ������
		mCurrentWeek = savedInstanceState == null ? 1 : savedInstanceState.getInt("mCurrentWeek");
		getActivity().setTitle(mTitle);
		// TODO �������� - ���� � ��� �� ������ �� ���� �������.
		mdbHelper = new DBHelper(getActivity());
		setHasOptionsMenu(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mTimetableLayout = (LinearLayout) inflater.inflate(R.layout.fragment_timetable, container, false);// ��� ������ ����������
		// ���� ���� ������
		mDayList = (ListView) mTimetableLayout.findViewById(R.id.day_list);
		// Header ��� ���� ������ (����)
		TextView DayListHeader = (TextView) inflater.inflate(R.layout.pair_time_list_item_1, null);
		DayListHeader.setText("����");
		mDayList.addHeaderView(DayListHeader);
		// TODO ���� �������� ���������, ���� ��������� ������� �������� ��� � ���������� ������
		final Calendar calendar = Calendar.getInstance(); // TODO �������� �� ����� ������� � ��������
		mDayList.setAdapter(
				new ArrayAdapter<String>(
					getActivity(),
					R.layout.timetable_grid_item_2,
					R.id.text1,
					new String[]{"��", "��", "��", "��", "��", "��"}){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null)
					convertView = getActivity().getLayoutInflater().inflate(R.layout.timetable_grid_item_2, parent, false);
				
				((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position)); // ���� ������
				((TextView) convertView.findViewById(R.id.text2)).setText((calendar.get(Calendar.DAY_OF_WEEK) == (position+2) ? "�������":String.valueOf(calendar.get(Calendar.DAY_OF_MONTH) + position + 2 - calendar.get(Calendar.DAY_OF_WEEK)) + " " + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK))); // ���� ������
				
				return convertView;
			}});
		// ����� ����������
		mTimetableGrid = (GridView) mTimetableLayout.findViewById(R.id.timetable_grid);
		// ���������� ������� �� ���� ������
		mTimetableGrid.setAdapter(
				new TableCursorAdapter(
					getActivity(),
					R.layout.timetable_grid_item_2,
					null,
					new String[]{SPECIALTY.NAME, TIMETABLE.CLASSROOM, TIMETABLE.COLOR},
					new int[]{R.id.text1, R.id.text2},
					42){
			@Override
			public void bindView(View view, Context context, Cursor cursor) {
				((TextView)view.findViewById(R.id.text1)).setText(cursor.getString(cursor.getColumnIndex(SPECIALTY.NAME)));
				((TextView)view.findViewById(R.id.text2)).setText(cursor.getString(cursor.getColumnIndex(TIMETABLE.CLASSROOM)));
				(view.findViewById(R.id.text_block)).setBackgroundColor(((MainActivity) getActivity()).getColor(cursor.getString((cursor.getColumnIndex(TIMETABLE.COLOR)))));
				(view.findViewById(R.id.text_block)).getBackground().setAlpha(80);
			}
			@Override
			public void bindAfter(View view, Context context) {
				super.bindAfter(view, context);
				// ��������� ������� ���� ������ TODO ������� ���������� � ������ ���������
				Calendar calendar = Calendar.getInstance();
				int currentTime = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
				int pairTime = 8 * 60 + (getViewPosition() - (getViewPosition() / 8) * 7 - 1) * (90 + 10);
				if(calendar.get(Calendar.DAY_OF_WEEK) == (getViewPosition() / 8 + 2) && currentTime >= pairTime && currentTime < pairTime + 90) {
					(view.findViewById(R.id.text_block)).setBackgroundColor(Color.RED);
				}
			}
		});
		// ����� ������� "��������� ���"
		mTimetableGrid.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				// ������� � �������� ������ "��������� ���"
				Bundle dialogInfo = new Bundle();
				dialogInfo.putString("idDialog", DialogBuilder.IdDialog.Timetable_ChangeDay.toString());
				dialogInfo.putInt("idTimetable", position+1);
				MainActivity.DialogBuilder.newInstance(getActivity(), dialogInfo).show(getFragmentManager(), DialogBuilder.IdDialog.Timetable_ChangeDay.toString());
				return true;
			}
		});
		
		mPairTimeGrid = (GridView) mTimetableLayout.findViewById(R.id.pairtime_grid);
		// TODO �������� �����, �������� ����������� ���������
		mPairTimeGrid.setAdapter(new ArrayAdapter<String>(
				getActivity(),
				R.layout.pair_time_list_item_1,
				new String[]{"8.00 - 9.30", "9.40 - 11.10", "11.30 - 13.00", "13.10 - 14.40", "14.50 - 16.20", "16.30 - 18.00", "18.00 - 19.30"}));
		
		return mTimetableLayout;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// ��� �������� ��������� ����������
		refillTimetable();
	}
	@Override
	public void onResume() {
		super.onResume();
		// ���������, ���� �� ������, ���� ���� - �������� ������� �� ��������������
		if(DialogBuilder.getCurrentDialogId() == DialogBuilder.IdDialog.Timetable_ChangeDay){
			DialogBuilder.getCurrentDialog().show();
		}
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
	/** ������������ ������� ���������� */
	public void refillTimetable(){
		SQLiteDatabase db = mdbHelper.getReadableDatabase();
		TableCursorAdapter TimetableGridAdapter = (TableCursorAdapter)(mTimetableGrid.getAdapter());		
		TimetableGridAdapter
				.changeCursor(
						db.query(
								TABLES.TIMETABLE+" JOIN "+TABLES.SPECIALTY+" ON "+TIMETABLE.fSPECIALTY_ID+"="+SPECIALTY.fID,
								new String[]{TIMETABLE.fID, SPECIALTY.NAME, TIMETABLE.CLASSROOM, TIMETABLE.COLOR},
								TIMETABLE.WEEK+"=?",
								new String[]{String.valueOf(mCurrentWeek)},
								null, null,
								TIMETABLE.fID));
		db.close();
	}
	@Override
	public void onDialogDismiss(DialogBuilder.IdDialog dialogId) {
		// ��� ������ �� �������� ���������� ����
		switch (dialogId) {
		case Timetable_ChangeDay:
			refillTimetable();
			break;
		default:
			break;
		}
	}
}
