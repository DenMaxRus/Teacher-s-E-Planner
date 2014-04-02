package com.teacherse_planner;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class TableCursorAdapter extends CursorAdapter {
	
	private Context mContext;
	protected LayoutInflater mInflater;
	private int mLayout;
	private String[] mFrom;
	private int[] mTo;
	private int mSize;
	private long mCursorId;
	private int mCursorIdPosition;
	private int mViewPosition;
	private int mRowIDColumn;
	//private View mCleanView;
	/** “екуща€ позици€ в ListView + 1 */
	public int getViewPosition() {
		return mViewPosition;
	}
	/**
	 * 
	 * @param context The context where the ListView associated with this SimpleListItemFactory is running 
	 * @param layout resource identifier of a layout file that defines the views for this list item. The layout file should include at least those named views defined in "to" 
	 * @param c The database cursor. Can be null if the cursor is not available yet. 
	 * @param from A list of column names representing the data to bind to the UI. Can be null if the cursor is not available yet. 
	 * @param to The views that should display column in the "from" parameter. These should all be TextViews. The first N views in this list are given the values of the first N columns in the from parameter. Can be null if the cursor is not available yet. 
	 * @param size How many items should be in the data set represented by this Adapter.
	 */
	public TableCursorAdapter(Context context,int layout, Cursor c, String[] from, int[] to, int size) {
		super(context, c, 0);
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout = layout;
		mFrom = from;
		mTo = to;
		mSize = size;
		boolean cursorPresent = c != null;
		mRowIDColumn = cursorPresent  ? c.getColumnIndexOrThrow("_id") : -1;
		mViewPosition = 0;
		setStartIdPosition(cursorPresent);
	}
	/**
	 * ѕровер€ет, совпадает ли позици€(!)(позици€+1) в списке с id записи 
	 * @return true если совпадает, иначе - false
	 */
	protected boolean isAtCurrent() {
		return mViewPosition == mCursorId ? true : false;
	}
	protected void setStartIdPosition(boolean cursorPresent){
		if(cursorPresent){
			setNextIdPosition();
			getCursor().moveToPosition(-1);
		} else {
			mCursorId = mCursorIdPosition = -1;
		}
	}
	protected void setNextIdPosition(){
		if(getCursor().moveToNext()){
			mCursorId = getCursor().getLong(mRowIDColumn);
			mCursorIdPosition = getCursor().getPosition();
			getCursor().moveToPrevious();
		} else {
			mCursorId = mCursorIdPosition = -1;
		}
	}
	@Override
	public void changeCursor(Cursor cursor) {
		// TODO Auto-generated method stub
		super.changeCursor(cursor);
		boolean cursorPresent = cursor != null;
		mRowIDColumn = cursorPresent  ? cursor.getColumnIndexOrThrow("_id") : -1;
		setStartIdPosition(cursorPresent);
	}
	@Override
	public int getCount() {
		if(getCursor() != null)
			return mSize;
		else
			return 0;
	};
	@Override
	public Object getItem(int position) {
		mViewPosition = position+1;
		if(getCursor() != null){
			if(mViewPosition == mCursorId)
				return super.getItem(mCursorIdPosition);
			else
				return null;
		}else
			return null;
	}
	@Override
	public long getItemId(int position) {
		mViewPosition = position+1;
		if(mViewPosition == mCursorId)
			return super.getItemId(mCursorIdPosition);
		else
			return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		mViewPosition = position+1;
        View view;
        if (convertView == null) {
            view = newView(mContext, getCursor(), parent);
        } else {
            view = convertView;
        }
		if(isAtCurrent()) {
			getCursor().moveToPosition(mCursorIdPosition);
			bindView(view, mContext, getCursor());
			setNextIdPosition();
		} else {
			view = newView(mContext, getCursor(), parent);// Ћишн€€ работа, как иначе?
			bindClean(mContext, view);
		}
		bindAfter(view, mContext);
        return view;
	}
	/** 
	 * —оздает новый, чистый View дл€ пустых значений
	 * @param context - Interface to application's global information
	 * @param view - новый View, возвращенный методом getView()
	 */
	public void bindClean(Context context, View view) {}
	/** 
	 * ќбрабатывает View, созданные методами bindView и bindClean
	 * @param view - View , возвращенный методами bindView или bindClean
	 * @param context - Interface to application's global information
	 */
	public void bindAfter(View view, Context context){}
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		for(int i=0; i < (mFrom.length > mTo.length ? mTo.length : mFrom.length); ++i) {
			TextView textView = (TextView) view.findViewById(mTo[i]);
			String data = cursor.getString(cursor.getColumnIndex(mFrom[i]));
			textView.setText(data);
		}
	}
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(mLayout, parent, false);
		return view;
	}
}
