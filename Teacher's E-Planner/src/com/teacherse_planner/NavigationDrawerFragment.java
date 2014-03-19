package com.teacherse_planner;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class NavigationDrawerFragment extends Fragment {
	
	NavigationDrawerCallbacks mCallbacks;
	
	DrawerLayout mDrawerLayout;
	FrameLayout mDrawerPanel;// ��� ������ NavigationDrawer
	ListView mDrawerMenuList;// ������� ����
	ListView mDrawerSpecialtiesList;// ������������ ������ �����
	ActionBarDrawerToggle mDrawerToggle;// ���������� Navigation Drawer'a
	
	DBHelper mdbHelper;// ����� � ��
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// ��������� �� ������� callbacks instance (MainActivity)
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO ������� ����� �������� ��
		mdbHelper = new DBHelper(getActivity());
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// �������, ��� ����� �������� ��������� � actionbar
		setHasOptionsMenu(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO ��� �� �������
		// ��� ������ NavigationDrawer
		mDrawerPanel = (FrameLayout) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
		// ������� ����
		mDrawerMenuList = (ListView) mDrawerPanel.findViewById(R.id.drawer_menu_list);
		mDrawerMenuList.setAdapter(new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_1,
				new String[]{"����������", "������", "���������", "�����", "Test"}));
		mDrawerMenuList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO �������� ���������� �� ���� ��������� ����
				selectNavigationMenuItem(position);
				switch(position){
				case 1:// ������ �����
					maintainSpecialtiesList();
					break;
				default:
					selectNavigationMenuItem(position);
					mDrawerLayout.closeDrawer(Gravity.LEFT);
				}
			}
		});
		
		mDrawerSpecialtiesList = (ListView) mDrawerPanel.findViewById(R.id.drawer_specialties_list);// ������������ ������ �����
		// ������� � �������� ����� ��� ����
		TextView DrawerSpecialtiesListFooter = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, null);
		DrawerSpecialtiesListFooter.setText("�������� ������");
		mDrawerSpecialtiesList.addFooterView(DrawerSpecialtiesListFooter);
		mDrawerSpecialtiesList.setAdapter(new SimpleCursorAdapter(
				getActivity(),
				android.R.layout.simple_list_item_1,
				mdbHelper.getReadableDatabase().query(DBHelper.SPECIALTY, null, DBHelper.SPECIALTY_ID+">?", new String[]{"1"}, null, null, null),
				new String[]{DBHelper.SPECIALTY_NAME},
				new int[]{android.R.id.text1},
				0));
		
		return mDrawerPanel;
	}
	// ���������� ������� �����
	private void maintainSpecialtiesList(){
		if(mDrawerSpecialtiesList == null || mDrawerSpecialtiesList.getVisibility() == ListView.VISIBLE)
			mDrawerSpecialtiesList.setVisibility(ListView.GONE);
		else
			mDrawerSpecialtiesList.setVisibility(ListView.VISIBLE);
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// ���� ������� ������ - ������������ ���������� NavigationDrawer'a
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // TODO ���������� ���������� ���������� actionbar'a
		return super.onOptionsItemSelected(item);
	}
	//��������� NavigationDrawer'a
	public void setUp(DrawerLayout drawerLayout){
		mDrawerLayout = drawerLayout;
        // ���������� Navigation Drawer
        mDrawerToggle = new ActionBarDrawerToggle(
				getActivity(),
				mDrawerLayout,
				R.drawable.ic_drawer,
				R.string.navigation_drawer_open,
				R.string.navigation_drawer_close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				// TODO Auto-generated method stub
				super.onDrawerOpened(drawerView);
			}
			public void onDrawerClosed(View drawerView) {
				// TODO Auto-generated method stub
				super.onDrawerClosed(drawerView);
				// ������� ������ ����� ��� �������� NavigationDrawer'a
				if(mDrawerSpecialtiesList != null && mDrawerSpecialtiesList.getVisibility() == ListView.VISIBLE)
					mDrawerSpecialtiesList.setVisibility(ListView.GONE);
			}
		};
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});
		
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
	}
	void selectNavigationMenuItem(int position){
		if(mCallbacks != null)
			mCallbacks.onNavigationMenuItemSelected(position);
	}
	public static interface NavigationDrawerCallbacks{
		void onNavigationMenuItemSelected(int position);
	}
}
