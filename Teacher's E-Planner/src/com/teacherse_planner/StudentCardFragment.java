package com.teacherse_planner;

import com.teacherse_planner.DBHelper.TABLES;
import com.teacherse_planner.DBHelper.TABLES.STUDENT;
import com.teacherse_planner.MainActivity.DialogBuilder.IdDialog;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class StudentCardFragment extends Fragment implements MainActivity.DialogBuilder.DialogCallbacks, OnClickListener {
	
	private LinearLayout mStudentCardLayout;
	private EditText mFIO;
	private EditText mTelephone;
	private EditText mEmail;
	private EditText mNote;
	private long mCurrentStudentId;
	
	private DBHelper mdDbHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mdDbHelper = new DBHelper(getActivity());
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mStudentCardLayout = (LinearLayout) inflater.inflate(R.layout.fragment_student_card, container, false);
		
		mFIO = (EditText) mStudentCardLayout.findViewById(R.id.fio);
		
		mTelephone = (EditText) mStudentCardLayout.findViewById(R.id.telephone);
		
		mEmail= (EditText) mStudentCardLayout.findViewById(R.id.email);
		
		mNote = (EditText) mStudentCardLayout.findViewById(R.id.note);
		
		Button cancel = (Button) mStudentCardLayout.findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		
		Button back = (Button) mStudentCardLayout.findViewById(R.id.back);
		back.setOnClickListener(this);
		
		Button save = (Button) mStudentCardLayout.findViewById(R.id.save);
		save.setOnClickListener(this);
		
		restoreInformation();

		return mStudentCardLayout;
	}
	private void restoreInformation(){
		if(getArguments().getString("type").compareTo("edit") == 0){
			Bundle args = getArguments();
			mCurrentStudentId = args.getLong("mCurrentStudentId");
			
			SQLiteDatabase db = mdDbHelper.getReadableDatabase();
			
			Cursor currentStudentInfo = db.query(
					TABLES.STUDENT,
					null,
					STUDENT.ID+"=?",
					new String[]{String.valueOf(mCurrentStudentId)},
					null, null, null);
			currentStudentInfo.moveToFirst();
			
			mFIO.setText(currentStudentInfo.getString(currentStudentInfo.getColumnIndex(STUDENT.NAME)));
			mTelephone.setText(currentStudentInfo.getString(currentStudentInfo.getColumnIndex(STUDENT.TELEPHONE)));
			mEmail.setText(currentStudentInfo.getString(currentStudentInfo.getColumnIndex(STUDENT.EMAIL)));
			mNote.setText(currentStudentInfo.getString(currentStudentInfo.getColumnIndex(STUDENT.NOTE)));
			
			db.close();
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			ContentValues cv = new ContentValues();
			
			cv.put(STUDENT.NAME, mFIO.getText().toString());
			cv.put(STUDENT.TELEPHONE, mTelephone.getText().toString());
			cv.put(STUDENT.EMAIL, mEmail.getText().toString());
			cv.put(STUDENT.NOTE, mNote.getText().toString());
			
			SQLiteDatabase db = mdDbHelper.getWritableDatabase();
			
			if((getArguments().getString("type").compareTo("edit") == 0)) {
				db.update(TABLES.STUDENT, cv, STUDENT.ID+"=?", new String[]{String.valueOf(mCurrentStudentId)});
			} else {
				cv.put(STUDENT.SPECIALTY_ID, getArguments().getLong("mCurrentSpecialtyId"));
				db.insert(TABLES.STUDENT, null, cv);
			}
			
			db.close();
			
			getFragmentManager().popBackStack();
			break;
		case R.id.cancel:
			restoreInformation();
			break;
		case R.id.back:
			getFragmentManager().popBackStack();
			break;
		default:
			break;
		}
	}
	@Override
	public void onDialogDismiss(IdDialog dialogId) {
		
	}
}
