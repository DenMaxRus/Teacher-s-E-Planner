package com.teacherse_planner;

import com.teacherse_planner.NavigationDrawerFragment.NavigationDrawerCallbacks;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
/** Окно списка группы */
public class GrouptableFragment extends Fragment implements NavigationDrawerCallbacks {
	
	private LinearLayout mGrouptableLayout;
	private ListView mStudentList;
	private GridView mGroupLessonsGrid;
	private GridView mGrouptableGrid;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mGrouptableLayout = (LinearLayout) inflater.inflate(R.layout.fragment_group_table, container);
		
		mStudentList = (ListView) mGrouptableLayout.findViewById(R.id.student_list);
		mGroupLessonsGrid = (GridView) mGrouptableLayout.findViewById(R.id.group_lessons_grid);
		mGrouptableGrid = (GridView) mGrouptableLayout.findViewById(R.id.grouptable_grid);
		
		return mGrouptableLayout;
	}
	@Override
	public void onNavigationMenuItemSelected(int position) {
		// TODO Auto-generated method stub
		
	}

}
