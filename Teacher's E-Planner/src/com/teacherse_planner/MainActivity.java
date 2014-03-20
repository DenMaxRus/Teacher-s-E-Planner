package com.teacherse_planner;

import java.util.Calendar;

import com.teacherse_planner.NavigationDrawerFragment.NavigationDrawerCallbacks;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Build;

public class MainActivity extends Activity implements NavigationDrawerCallbacks {
	
	private Calendar mCalendar; //
	public Calendar getCalendar(){
		return mCalendar;
	}
	public DBHelper mdbHelper;
	
	private NavigationDrawerFragment mNavigationDrawerFragment; // Управляющий класс Navigation Drawer'a
	private TimetableFragment mTimetableFragment;
	private CharSequence mTitle; // Название текущего окна
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		// Найти фрагмент Navigation Drawer'a и вызвать его настройку
		mNavigationDrawerFragment = (NavigationDrawerFragment)getFragmentManager().findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp((DrawerLayout) findViewById(R.id.drawer_layout));
		// Создаем вспомогательные объекты помошники, из всех фгарментов будем использовать отсюда
		mdbHelper = new DBHelper(this);
		mCalendar = Calendar.getInstance();
		
		mTitle = getTitle();
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment())
					.commit();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
		// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main_screen, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			//return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onNavigationMenuItemSelected(int position) {
		// TODO Переключать окна в зависимости от позиции
		FragmentTransaction NewTransaction = getFragmentManager().beginTransaction();
		switch(position){
		case 0:// Расписание
			if(mTimetableFragment == null)
				mTimetableFragment = new TimetableFragment();
			NewTransaction
				.replace(R.id.container, mTimetableFragment);
			mTitle = mTimetableFragment.getmTitle();
			break;
		case 1:// Окно группы (обрабатывается самим NavigationDrawer'ом
			break;
		case 2:// Настройки
			break;
		case 3:// Выход
			finish();
			System.exit(0);
			break;
		case 4:// Test
			NewTransaction
				.replace(R.id.container, new PlaceholderFragment());
			mTitle = getResources().getText(R.string.app_name);
			break;
		}
		NewTransaction.commit();
	}
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
	// TODO Подумать над класом диалогов
	public static class DialogBilder extends DialogFragment {
		public static DialogBilder newInstance(Bundle args){
			DialogBilder instance = new DialogBilder();
			instance.setArguments(args);
			return instance;
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			return super.onCreateDialog(savedInstanceState);
		}
	}
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_screen,
					container, false);
			return rootView;
		}
	}

}
