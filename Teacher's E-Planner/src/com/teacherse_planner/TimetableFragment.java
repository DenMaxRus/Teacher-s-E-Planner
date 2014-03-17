package com.teacherse_planner;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TimetableFragment extends Fragment {
	
	LinearLayout mTimetableLayout; // ��� ������ ����������
	GridView mPairTimeGrid; // ����� ������� ���
	GridView mTimetableGrid; // ����� ����������
	ListView mDayList; // ���� ���� ������
	DBHelper mdbHelper; // ����� ������ � ��
	int CurrentWeek;	
	
	public TimetableFragment(){}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO �������� actionbar
		super.onCreate(savedInstanceState);
		CurrentWeek = 1;
		
		// TODO �������� - ���� � ��� �� ������ �� ���� �������.
		mdbHelper = new DBHelper(getActivity());
		setHasOptionsMenu(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mTimetableLayout = (LinearLayout) inflater.inflate(R.layout.fragment_timetable, container, false);
		
		mDayList = (ListView) mTimetableLayout.findViewById(R.id.day_list);
		
		mTimetableGrid = (GridView) mTimetableLayout.findViewById(R.id.timetable_grid);
		// TODO ����� ����� ���������� ��������?
		mTimetableGrid.setAdapter(new SimpleCursorAdapter(// ���� ��������� �������: 1lvl - �������� ������, 2lvl - ����� ���������
				getActivity(),
				android.R.layout.simple_list_item_2,
				null,
				new String[]{DBHelper.SPECIALTY_NAME, DBHelper.TIMETABLE_CLASSROOM},
				new int[]{android.R.id.text1, android.R.id.text2},
				0){
			@Override
			public void bindView(View view, Context context, Cursor cursor) {
				// TODO �������� ���������� ��������� ����� � ������� �� ������� (?)
				((TextView) view.findViewById(android.R.id.text1)).setText(cursor.getString(cursor.getColumnIndex(DBHelper.SPECIALTY_NAME)));// �������� ������
				((TextView) view.findViewById(android.R.id.text2)).setText(cursor.getString(cursor.getColumnIndex(DBHelper.TIMETABLE_CLASSROOM)));// ����� ���������
				super.bindView(view, context, cursor);
			}
		});
		
		mPairTimeGrid = (GridView) mTimetableLayout.findViewById(R.id.pairtime_grid);
		
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
