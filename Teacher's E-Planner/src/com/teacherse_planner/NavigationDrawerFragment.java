package com.teacherse_planner;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
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
	FrameLayout mDrawerPanel;// Вся панель NavigationDrawer
	ListView mDrawerMenuList;// Главное меню
	ListView mDrawerSpecialtiesList;// Появляющийся список групп
	DBHelper mdbHelper;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Добавить actonbar
		super.onCreate(savedInstanceState);
		mdbHelper = new DBHelper(getActivity());
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mDrawerPanel = (FrameLayout) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);// Вся панель NavigationDrawer

		mDrawerMenuList = (ListView) mDrawerPanel.findViewById(R.id.drawer_menu_list);// Главное меню
		mDrawerMenuList.setAdapter(new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_1,
				new String[]{"Расписание", "Группы"}));
		mDrawerMenuList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Добавить управление ко всем элементам меню
				switch(position){
				case 1:// Список групп
					maintainSpecialtiesList();
					break;
				}
			}
		});
		
		mDrawerSpecialtiesList = (ListView) mDrawerPanel.findViewById(R.id.drawer_specialties_list);// Появляющийся список групп
		// Создать и добавить футер для меню
		TextView DrawerSpecialtiesListFooter = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, null);
		DrawerSpecialtiesListFooter.setText("Добавить группу");
		mDrawerSpecialtiesList.addFooterView(DrawerSpecialtiesListFooter);
		mDrawerSpecialtiesList.setAdapter(new SimpleCursorAdapter(
				getActivity(),
				android.R.layout.simple_list_item_1,
				mdbHelper.getReadableDatabase().query(DBHelper.SPECIALTY, null, DBHelper.SPECIALTY_ID+">?", new String[]{"1"}, null, null, null),
				new String[]{DBHelper.SPECIALTY_NAME},
				new int[]{android.R.id.text1},
				0));
		
		return mDrawerPanel;
		//return super.onCreateView(inflater, container, savedInstanceState);
	}
	private void maintainSpecialtiesList(){ // Управление списком групп
		if(mDrawerSpecialtiesList == null || mDrawerSpecialtiesList.getVisibility() == ListView.VISIBLE)
			mDrawerSpecialtiesList.setVisibility(ListView.GONE);
		else
			mDrawerSpecialtiesList.setVisibility(ListView.VISIBLE);
		
	}
}
