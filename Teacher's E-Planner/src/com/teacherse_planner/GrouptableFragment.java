package com.teacherse_planner;

import com.teacherse_planner.NavigationDrawerFragment.NavigationDrawerCallbacks;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/** Окно списка группы */
public class GrouptableFragment extends Fragment implements NavigationDrawerCallbacks {
	ViewGroup mGrouptableLayout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mGrouptableLayout = (ViewGroup) inflater.inflate(R.layout.fragment_group_table, container);
		return mGrouptableLayout;
	}
	@Override
	public void onNavigationMenuItemSelected(int position) {
		// TODO Auto-generated method stub
		
	}

}
