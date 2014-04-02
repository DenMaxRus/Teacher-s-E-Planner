package com.teacherse_planner;

import com.teacherse_planner.DBHelper.TABLES;
import com.teacherse_planner.DBHelper.TABLES.SPECIALTY;
import com.teacherse_planner.MainActivity.DialogBuilder.IdDialog;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class NavigationDrawerFragment extends Fragment implements MainActivity.DialogBuilder.DialogCallbacks {
	
	NavigationDrawerCallbacks mCallbacks;
	
	DrawerLayout mDrawerLayout;
	FrameLayout mDrawerPanel;// Вся панель NavigationDrawer
	ListView mDrawerMenuList;// Главное меню
	ListView mDrawerSpecialtiesList;// Появляющийся список групп
	ActionBarDrawerToggle mDrawerToggle;// Управление Navigation Drawer'a
	
	DBHelper mdbHelper;// Работа с БД

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Указатель на текущую callbacks instance (MainActivity)
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
        mdbHelper = new DBHelper(getActivity());
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Говорим, что хотим добавить изменения в actionbar
		setHasOptionsMenu(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Что то сделать
		// Вся панель NavigationDrawer
		mDrawerPanel = (FrameLayout) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
		// Главное меню
		mDrawerMenuList = (ListView) mDrawerPanel.findViewById(R.id.drawer_menu_list);
		mDrawerMenuList.setAdapter(new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_1,
				new String[]{"Расписание", "Группы", "Настройки", "Выход", "Test", "Группа 2"}));
		mDrawerMenuList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Добавить управление ко всем элементам меню
				selectNavigationMenuItem(position);
				switch(position){
				case 1:// Список групп
					maintainSpecialtiesList();
					break;
				default:
					selectNavigationMenuItem(position);
					mDrawerLayout.closeDrawer(Gravity.LEFT);
				}
			}
		});
		
		mDrawerSpecialtiesList = (ListView) mDrawerPanel.findViewById(R.id.drawer_specialties_list);// Появляющийся список групп
		// Создать и добавить футер для меню
		TextView SpecialtiesListFooter = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, null);
		SpecialtiesListFooter.setText("Добавить группу");
		SpecialtiesListFooter.setOnClickListener(
				new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle dialogInfo = new Bundle();
				dialogInfo.putString("idDialog", MainActivity.DialogBuilder.IdDialog.Add_Specialty.toString());
				MainActivity.DialogBuilder.newInstance(getActivity(), dialogInfo).show(getFragmentManager(), MainActivity.DialogBuilder.IdDialog.Add_Specialty.toString());
			}
		});
		SpecialtiesListFooter.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
		mDrawerSpecialtiesList.addFooterView(SpecialtiesListFooter);
		mDrawerSpecialtiesList.setAdapter(new SimpleCursorAdapter(
				getActivity(),
				android.R.layout.simple_list_item_1,
				null,
				new String[]{SPECIALTY.NAME},
				new int[]{android.R.id.text1},
				0));
		mDrawerSpecialtiesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Добавить вызов группы
				SpecialtytableFragment SF = ((MainActivity) getActivity()).getmSpecialtytableFragment();
				if(SF == null)
					SF = new SpecialtytableFragment();
				Bundle args = new Bundle();
				args.putLong("mCurrentSpecialityId", id);
				Cursor c = (Cursor) mDrawerSpecialtiesList.getItemAtPosition(position);
				args.putString("SpecialtyName", (c.getString(c.getColumnIndex(SPECIALTY.NAME))));
				SF.setArguments(args);
				getFragmentManager().beginTransaction().replace(R.id.container, SF).commit();
				mDrawerLayout.closeDrawer(Gravity.LEFT);
			}
		});
		registerForContextMenu(mDrawerSpecialtiesList);
		refillSpecialtiesList();
		
		return mDrawerPanel;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Если выбрали иконку - использовать управление NavigationDrawer'a
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // TODO Управление остальными элементами actionbar'a
		return super.onOptionsItemSelected(item);
	}
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.main_screen, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	// TODO Auto-generated method stub
    	super.onCreateContextMenu(menu, v, menuInfo);
    	menu.add(R.string.delete);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	// Удаление группы из списка
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	SQLiteDatabase db = mdbHelper.getWritableDatabase();
    	db.delete(TABLES.SPECIALTY, SPECIALTY.ID+"=?", new String[]{String.valueOf(info.id)});
    	db.close();
    	refillSpecialtiesList();
    	return true;
    }
    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.START);
    }
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.navigation_menu);
    }
	/** Убрать/показать список групп */
	private void maintainSpecialtiesList(){
		if(mDrawerSpecialtiesList == null || mDrawerSpecialtiesList.getVisibility() == ListView.VISIBLE)
			mDrawerSpecialtiesList.setVisibility(ListView.GONE);
		else
			mDrawerSpecialtiesList.setVisibility(ListView.VISIBLE);
		
	}
	/** Перерисовать текущее расписание */
	public void refillSpecialtiesList(){
		SQLiteDatabase db = mdbHelper.getReadableDatabase();
		SimpleCursorAdapter TimetableGridAdapter = (SimpleCursorAdapter)(((HeaderViewListAdapter)mDrawerSpecialtiesList.getAdapter()).getWrappedAdapter());		
		TimetableGridAdapter
				.changeCursor(
						db.query(
								TABLES.SPECIALTY,
								null,
								SPECIALTY.ID+">?",
								new String[]{"1"},
								null, null, null));
		db.close();
	}
	/** Настройка NavigationDrawer'a
	 * @param drawerLayout - Полотно NavigationDrawer'a */
	public void setUp(DrawerLayout drawerLayout){
		mDrawerLayout = drawerLayout;
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_hint, Gravity.START);
		// Управление Navigation Drawer
        mDrawerToggle = new ActionBarDrawerToggle(
				getActivity(),
				mDrawerLayout,
				R.drawable.ic_drawer,
				R.string.navigation_drawer_open,
				R.string.navigation_drawer_close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActivity().invalidateOptionsMenu();
			}
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				// Закрыть список групп при закрытии NavigationDrawer'a
				if(mDrawerSpecialtiesList != null && mDrawerSpecialtiesList.getVisibility() == ListView.VISIBLE)
					mDrawerSpecialtiesList.setVisibility(ListView.GONE);
				getActivity().invalidateOptionsMenu();
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
	@Override
	public void onDialogDismiss(IdDialog dialogId) {
		switch (dialogId) {
		case Add_Specialty:
			refillSpecialtiesList();
			break;
		default:
			break;
		}
		
	}
}
