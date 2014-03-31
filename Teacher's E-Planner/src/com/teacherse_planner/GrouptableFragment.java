package com.teacherse_planner;

import com.teacherse_planner.DBHelper.TABLES.STUDENT;
import com.teacherse_planner.MainActivity.DialogBuilder.IdDialog;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
/** ���� ������ ������ */
public class GrouptableFragment extends Fragment implements MainActivity.DialogBuilder.DialogCallbacks {
	
	private LinearLayout mGrouptableLayout;
	/** ������ ��������� */
	private ListView mStudentList;
	/** ������ ������ */
	private GridView mGroupLessonsGrid;
	/** ����� ������ */
	private GridView mGrouptableGrid;
	
	private DBHelper mdbHelper;
	private int mCurrentSpecialityId;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO �������� actionbar
		super.onCreate(savedInstanceState);
		// ������������ ������� ������ �� ������������ ��������� ��� �����������
		mCurrentSpecialityId = savedInstanceState == null ? 1 : savedInstanceState.getInt("mCurrentSpecialityId");
		
		// TODO �������� - ���� � ��� �� ������ �� ���� �������.
		mdbHelper = new DBHelper(getActivity());
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mGrouptableLayout = (LinearLayout) inflater.inflate(R.layout.fragment_group_table, container, false);
		
		mStudentList = (ListView) mGrouptableLayout.findViewById(R.id.student_list);
		mStudentList.setAdapter(
				new SimpleCursorAdapter(
						getActivity(),
						R.layout.pair_time_list_item_1,
						mdbHelper.getAllStudentsFromSpecialty(String.valueOf(mCurrentSpecialityId)),
						new String[]{STUDENT.NAME},
						new int[]{R.id.text1},
						0));
		
		mGroupLessonsGrid = (GridView) mGrouptableLayout.findViewById(R.id.group_lessons_grid);
		mGrouptableGrid = (GridView) mGrouptableLayout.findViewById(R.id.grouptable_grid);
		
		return mGrouptableLayout;
	}

	@Override
	public void onDialogDismiss(IdDialog dialogId) {
		// TODO Auto-generated method stub
		
	}
}
