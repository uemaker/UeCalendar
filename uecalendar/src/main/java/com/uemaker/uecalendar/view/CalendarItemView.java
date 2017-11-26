package com.uemaker.uecalendar.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uemaker.uecalendar.R;
import com.uemaker.uecalendar.util.CustomDate;
import com.uemaker.uecalendar.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自定义日历卡
 * 
 * @author wuwenjie
 * 
 */
public class CalendarItemView extends RelativeLayout {

	private static final int TOTAL_COL = 7; // 7列
	private static final int TOTAL_ROW = 6; // 6行

	private static CustomDate mShowDate; // 当前显示日期，包括year,month,day
	private OnCellClickListener mCellClickListener; // 单元格点击回调事件

	private Context context;
	private CalendarGridView gridView;
	private MyGridAdapter myGridAdapter;
	private List<Cell> cells = new ArrayList<Cell>();
	private int cellWidth = 0;

	public final static String TODAYCOLOR = "todayColor";
	public final static String OTHERMONTHCOLOR = "otherMonthColor";
	public final static String REACHDAYCOLOR = "reachDayColor";
	public final static String UNREACHDAYCOLOR = "unreachDayColor";
	public final static String SELECTEDDAYCOLOR = "selectedDayColor";
	public final static String DAYSIZE = "daySize";
	public final static String SELECTEDDAYBACKGROUND = "selectedDayBackground";
	private Map<String,Integer> itemAttrs;

	/**
	 * 单元格点击的回调接口
	 *
	 * @author wuwenjie
	 *
	 */
	public interface OnCellClickListener {
		void clickDate(CustomDate date); // 回调点击的日期
		void changeDate(CustomDate date); // 回调滑动ViewPager改变的日期
	}

	public CalendarItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		init();
	}

	public CalendarItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public CalendarItemView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public CalendarItemView(Context context, OnCellClickListener listener, Map<String,Integer> itemAttrs) {
		super(context);
		this.context = context;
		this.mCellClickListener = listener;
		this.itemAttrs = itemAttrs;
		init();
	}

	private void init() {

		gridView = new CalendarGridView(context);
		gridView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		gridView.setNumColumns(TOTAL_COL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		addView(gridView);

		mShowDate = new CustomDate();

//		LogUtil.e("cells="+cells.toString());
		myGridAdapter = new MyGridAdapter(context);
		gridView.setAdapter(myGridAdapter);
		initDate();
		myGridAdapter.setCellData(cells);

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cell cell = (Cell) parent.getAdapter().getItem(position);
				if(cell!=null && mCellClickListener!=null){
					if(cell.state == State.UNREACH_DAY){
						myGridAdapter.setCurDate(cell.date);
						mCellClickListener.clickDate(cell.date);
					}
				}
			}
		});
	}

	public void setCurDate(CustomDate curDate)
	{
		myGridAdapter.setCurDate(curDate);
	}

	private void initDate(){
		CustomDate today = new CustomDate();
		int monthDay = DateUtil.getCurrentMonthDay(); // 今天
		int lastMonthDays = DateUtil.getMonthDays(mShowDate.year, mShowDate.month - 1);
		int currentMonthDays = DateUtil.getMonthDays(mShowDate.year, mShowDate.month);
		int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.year, mShowDate.month);
		boolean isCurrentMonth = false;
		if (DateUtil.isCurrentMonth(mShowDate)) {
			isCurrentMonth = true;
		}

		int day = 0;
		Cell cell;
		if(cells.size()>0){
			cells.clear();
		}
		for (int j = 0; j < TOTAL_ROW; j++) {
			for (int i = 0; i < TOTAL_COL; i++) {
				int position = i + j * TOTAL_COL;
				if (position >= firstDayWeek && position < firstDayWeek + currentMonthDays) { //这个月的
					day++;
					CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
					int diff = compareDate(date, today);
					if(diff>0){//未过
						cell = new Cell(date, State.UNREACH_DAY, i, j);
					}else if(diff<0){//已过
						cell = new Cell(date, State.REACH_DAY, i, j);
					}else{//今天
						cell = new Cell(date, State.TODAY, i, j);
					}

					cells.add(cell);
				} else if (position < firstDayWeek) { //过去一个月
					cell = new Cell(new CustomDate(mShowDate.year, mShowDate.month-1,lastMonthDays-(firstDayWeek - position - 1)), State.PAST_MONTH_DAY, j, i);
					cells.add(cell);
				} else if (position >= firstDayWeek + currentMonthDays) {//下个月
					cell = new Cell((new CustomDate(mShowDate.year,mShowDate.month + 1, position - firstDayWeek - currentMonthDays + 1)), State.NEXT_MONTH_DAY, i, j);
					cells.add(cell);
				}
			}
		}
		myGridAdapter.notifyDataSetChanged();
		if(mCellClickListener!=null){
			mCellClickListener.changeDate(mShowDate);
		}
	}

	class Cell {
		public CustomDate date;
		public State state;
		public int rowNum;
		public int colNum;

		public Cell(CustomDate date, State state, int rowNum, int colNum){
			this.date = date;
			this.state = state;
			this.rowNum = rowNum;
			this.colNum = colNum;
		}
	}

	//比较两个日期，-2为无效，1大于，0相等，-1小于
	public int compareDate(CustomDate date1, CustomDate date2)
	{
		int result = -2;
		int diffYear;
		int diffMonth;
		int diffDay;
		if(date1==null || date2==null){
			return -2;
		}
		diffYear = date1.getYear()-date2.getYear();
		diffMonth = date1.getMonth()-date2.getMonth();
		diffDay = date1.getDay()-date2.getDay();

		if(date1!=null && date2!=null){
			diffYear = date1.getYear()-date2.getYear();
			diffMonth = date1.getMonth()-date2.getMonth();
			diffDay = date1.getDay()-date2.getDay();
		}
		result = diffYear>0 ? 1 : (diffYear<0 ? -1 : 0);
		if(result != 0){
			return result;
		}
		result = diffMonth>0 ? 1 : (diffMonth<0 ? -1 : 0);
		if(result != 0){
			return result;
		}
		result = diffDay>0 ? 1 : (diffDay<0 ? -1 : 0);
		return result;
	}

	/**
	 * 
	 * @author wuwenjie 单元格的状态 当前月日期，过去的月的日期，下个月的日期
	 */
	enum State {

		TODAY,PAST_MONTH_DAY, NEXT_MONTH_DAY, REACH_DAY,UNREACH_DAY;
	}

	// 从左往右划，上一个月
	public void leftSlide() {
		if (mShowDate.month == 1) {
			mShowDate.month = 12;
			mShowDate.year -= 1;
		} else {
			mShowDate.month -= 1;
		}
		initDate();
	}

	// 从右往左划，下一个月
	public void rightSlide() {
		if (mShowDate.month == 12) {
			mShowDate.month = 1;
			mShowDate.year += 1;
		} else {
			mShowDate.month += 1;
		}
		initDate();
	}

	class MyGridAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater mInflater;
		private List<Cell> cellData = new ArrayList<Cell>();
		private CustomDate curDate = null;

		public MyGridAdapter(Context context, List<Cell> cells){
			this.context = context;
			this.mInflater = LayoutInflater.from(context);
		}

		public MyGridAdapter(Context context){
			this.context = context;
			this.cellData = cells;
			this.mInflater = LayoutInflater.from(context);
		}

		public void setCellData(List<Cell> cells){
			this.cellData = cells;
		}

		@Override
		public int getCount() {
			return cellData.size();
		}

		@Override
		public Object getItem(int position) {
			return cellData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder viewHolder = null;
			if (convertView == null)
			{
				convertView = mInflater.inflate(R.layout.ue_calendar_item, parent,false);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_circle);
				viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_text);

				convertView.setTag(viewHolder);
			} else
			{
				viewHolder = (ViewHolder) convertView.getTag();
			}


			Cell cell = (Cell) getItem(position);

			viewHolder.textView.setTextSize(itemAttrs.get(CalendarItemView.DAYSIZE));
			viewHolder.textView.setText(cell.date.day+"");
			switch (cell.state){
				case TODAY:
					viewHolder.textView.setText("今天");
					viewHolder.textView.setTextColor(itemAttrs.get(CalendarItemView.TODAYCOLOR));
					break;
				case REACH_DAY:
					viewHolder.textView.setTextColor(itemAttrs.get(CalendarItemView.REACHDAYCOLOR));
					break;
				case UNREACH_DAY:
					viewHolder.textView.setTextColor(itemAttrs.get(CalendarItemView.UNREACHDAYCOLOR));
					break;
				case NEXT_MONTH_DAY:
					viewHolder.textView.setTextColor(itemAttrs.get(CalendarItemView.OTHERMONTHCOLOR));
					break;
				case PAST_MONTH_DAY:
					viewHolder.textView.setTextColor(itemAttrs.get(CalendarItemView.OTHERMONTHCOLOR));
					break;
				default:
					break;
			}

			int diff = compareDate(cell.date, curDate);
			if(curDate!=null && diff==0 && cell.state!=State.PAST_MONTH_DAY && cell.state!=State.NEXT_MONTH_DAY){
				viewHolder.textView.setTextColor(itemAttrs.get(CalendarItemView.SELECTEDDAYCOLOR));
				viewHolder.imageView.setVisibility(VISIBLE);
			}else{
				viewHolder.imageView.setVisibility(GONE);
			}

			return convertView;
		}

		public void setCurDate(CustomDate curDate)
		{
			this.curDate = curDate;
			notifyDataSetChanged();
		}

		private final class ViewHolder
		{
			ImageView imageView;
			TextView textView;
		}
	}
}
