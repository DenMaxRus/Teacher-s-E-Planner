package com.teacherse_planner;

import java.util.Calendar;
import java.util.Locale;

import com.teacherse_planner.MainActivity.DialogBuilder;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TimetableFragment extends Fragment implements MainActivity.DialogBuilder.DialogCallbacks {
	
	private String mTitle = "����������"; // ���������
	public String getTitle() {
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
				new String[]{DBHelper.SPECIALTY_NAME, DBHelper.TIMETABLE_CLASSROOM, DBHelper.TIMETABLE_COLOR},
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
		// ����� ������� "��������� ���"
		mTimetableGrid.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				// ������� � �������� ������ "��������� ���"
				Bundle dialogInfo = new Bundle();
				dialogInfo.putString("idDialog", DialogBuilder.IdDialog.Timetable_ChangeDay.toString());
				dialogInfo.putInt("idTimetable", (int) id);
				dialogInfo.putInt("currentWeek", mCurrentWeek);
				dialogInfo.putString("currentSpecialityName", ((TextView)view.findViewById(R.id.text1)).getText().toString());
				dialogInfo.putString("currentClassroom", ((TextView)view.findViewById(R.id.text2)).getText().toString());
				DialogBuilder df = MainActivity.DialogBuilder.newInstance(dialogInfo);
				df.show(getFragmentManager(), DialogBuilder.IdDialog.Timetable_ChangeDay.toString());
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
	public void onPause() {
		// TODO ���������, ���� �� ������, ���� ���� - �������� ������� �� ��������������
		super.onPause();
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
	public void refillTimetable(){
		((SimpleCursorAdapter)(mTimetableGrid.getAdapter()))
				.changeCursor(mdbHelper.getReadableDatabase()
						.query(
								DBHelper.TIMETABLE+" LEFT OUTER JOIN "+DBHelper.SPECIALTY+" ON "+DBHelper.TIMETABLE_SPECIALTY_ID+"="+DBHelper.SPECIALTY+"."+DBHelper.SPECIALTY_ID,
								new String[]{DBHelper.TIMETABLE+"."+DBHelper.TIMETABLE_ID, DBHelper.SPECIALTY_NAME, DBHelper.TIMETABLE_CLASSROOM, DBHelper.TIMETABLE_COLOR},
								DBHelper.TIMETABLE_WEEK+"=?",
								new String[]{String.valueOf(mCurrentWeek)},
								null, null, null));
		((SimpleCursorAdapter)mTimetableGrid.getAdapter()).notifyDataSetChanged();
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
