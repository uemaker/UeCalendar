package com.uemaker.uecalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.uemaker.uecalendar.adapter.CalendarViewAdapter;
import com.uemaker.uecalendar.util.CommonUtil;
import com.uemaker.uecalendar.util.CustomDate;
import com.uemaker.uecalendar.util.DateUtil;
import com.uemaker.uecalendar.view.CalendarItemView;
import com.uemaker.uecalendar.view.ContentViewPager;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: document your custom view class.
 */
public class UeCalendar extends LinearLayout implements View.OnClickListener,CalendarItemView.OnCellClickListener {

    private final Context context;

    private View rlTitle;
    private TextView btnPreMonth;
    private TextView btnNextMonth;
    private TextView tvCurrentMonth;
    private TableLayout calendarHeader;
    private ContentViewPager viewPager;

    private int mCurrentIndex = 498;
    private SildeDirection mDirection = SildeDirection.NO_SILDE;
    enum SildeDirection {
        RIGHT, LEFT, NO_SILDE;
    }
    private CalendarItemView[] mShowViews;
    private CalendarViewAdapter<CalendarItemView> adapter;

    private Map<String,Integer> itemAttrs;

    private CustomDate curDate;


    private CalendarListener calendarListener;


    //attrs

    private boolean showTitle;
    private float titleHeight;
    private float titleSize;
    private int titleColor;
    private int titleBackground;
    private boolean showWeek;
    private float weekHeight;
    private float weekSize;
    private int weekColor;
    private int weekBackground;
    private int arrowColor;

    private String todayText;
    private int todayColor;
    private int otherMonthColor;
    private int reachDayColor;
    private int unreachDayColor;
    private int selectedDayColor;
    private float daySize;
    private int selectedDayBackground;

    public UeCalendar(Context context) {
        super(context);
        this.context = context;
        init(null, 0);
    }

    public UeCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs, 0);
    }

    public UeCalendar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        initStyle(attrs);
        View view = View.inflate(context, R.layout.ue_calendar, null);
        calendarHeader = (TableLayout) view.findViewById(R.id.calendar_header);
        btnPreMonth = (TextView) view.findViewById(R.id.btnPreMonth);
        btnNextMonth = (TextView) view.findViewById(R.id.btnNextMonth);
        tvCurrentMonth = (TextView) view.findViewById(R.id.tvCurrentMonth);
        viewPager = (ContentViewPager) view.findViewById(R.id.vp_calendar);
        rlTitle = view.findViewById(R.id.rl_title);

        addHeader();
        addCalendar();

        btnPreMonth.setOnClickListener(this);
        btnNextMonth.setOnClickListener(this);
        addView(view);

        if(!showTitle){
            rlTitle.setVisibility(GONE);
        }
        if(!showWeek){
            calendarHeader.setVisibility(GONE);
        }
        tvCurrentMonth.setTextColor(titleColor);
        tvCurrentMonth.setTextSize(titleSize);
        rlTitle.setBackgroundColor(titleBackground);
        btnPreMonth.setTextSize(titleSize);
        btnNextMonth.setTextSize(titleSize);
        btnPreMonth.setTextColor(arrowColor);
        btnNextMonth.setTextColor(arrowColor);
        calendarHeader.setBackgroundColor(weekBackground);

//        calendarItemViewAttrs = new A
    }

    private void initStyle(AttributeSet attrs)
    {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UeCalendar);
        todayText = ta.getString(R.styleable.UeCalendar_todayText);
        todayColor = ta.getColor(R.styleable.UeCalendar_todayColor, getResources().getColor(R.color.cl_text_gold));
        showTitle = ta.getBoolean(R.styleable.UeCalendar_showTitle, true);
        titleHeight = ta.getDimension(R.styleable.UeCalendar_titleHeight, 40);
        titleSize = ta.getDimension(R.styleable.UeCalendar_titleSize, 16);
        titleColor = ta.getColor(R.styleable.UeCalendar_titleColor, getResources().getColor(R.color.cl_text_default));
        titleBackground = ta.getColor(R.styleable.UeCalendar_titleBackground, Color.TRANSPARENT);

        showWeek = ta.getBoolean(R.styleable.UeCalendar_showWeek, true);
        weekHeight = ta.getDimension(R.styleable.UeCalendar_weekHeight, 40);
        weekSize = ta.getDimension(R.styleable.UeCalendar_weekSize, 16);
        weekColor = ta.getColor(R.styleable.UeCalendar_weekColor, getResources().getColor(R.color.cl_text_default));
        weekBackground = ta.getColor(R.styleable.UeCalendar_weekBackground, Color.WHITE);

        arrowColor = ta.getColor(R.styleable.UeCalendar_arrowColor, getResources().getColor(R.color.cl_text_gold));
        otherMonthColor = ta.getColor(R.styleable.UeCalendar_otherMonthColor, getResources().getColor(R.color.cl_text_disable));
        reachDayColor = ta.getColor(R.styleable.UeCalendar_reachDayColor, getResources().getColor(R.color.cl_text_readonly));
        unreachDayColor = ta.getColor(R.styleable.UeCalendar_unreachDayColor, getResources().getColor(R.color.cl_text_default));
        selectedDayColor = ta.getColor(R.styleable.UeCalendar_selectedDayColor, getResources().getColor(R.color.cl_text_white));
        selectedDayBackground = ta.getColor(R.styleable.UeCalendar_selectedDayBackground, getResources().getColor(R.color.cl_text_gold));

        daySize = ta.getDimension(R.styleable.UeCalendar_daySize, 16);
        ta.recycle();

        itemAttrs = new HashMap<String, Integer>();
        itemAttrs.put(CalendarItemView.OTHERMONTHCOLOR, otherMonthColor);
        itemAttrs.put(CalendarItemView.DAYSIZE, (int) daySize);
        itemAttrs.put(CalendarItemView.REACHDAYCOLOR, reachDayColor);
        itemAttrs.put(CalendarItemView.UNREACHDAYCOLOR, unreachDayColor);
        itemAttrs.put(CalendarItemView.SELECTEDDAYBACKGROUND, selectedDayBackground);
        itemAttrs.put(CalendarItemView.SELECTEDDAYCOLOR, selectedDayColor);
        itemAttrs.put(CalendarItemView.TODAYCOLOR, todayColor);

    }

    private void addHeader()
    {
        TableRow tablerow = new TableRow(context);
        for(int i=0; i<7; i++){
            TextView textView = new TextView(context);
            textView.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, CommonUtil.dip2px(context, 40), 1.0f));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(weekSize);
            textView.setTextColor(weekColor);
            textView.setText(DateUtil.weekName[i]);
            tablerow.addView(textView);
        }
        calendarHeader.addView(tablerow);
    }

    private void addCalendar()
    {
        CalendarItemView[] views = new CalendarItemView[3];
        for (int i = 0; i < 3; i++) {
            views[i] = new CalendarItemView(context, this, itemAttrs);
        }
        adapter = new CalendarViewAdapter<>(views);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(498);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                measureDirection(position);
                updateCalendarView(position, curDate);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    /**
     * 计算方向
     *
     * @param arg0
     */
    private void measureDirection(int arg0) {

        if (arg0 > mCurrentIndex) {
            mDirection = SildeDirection.RIGHT;

        } else if (arg0 < mCurrentIndex) {
            mDirection = SildeDirection.LEFT;
        }
        mCurrentIndex = arg0;
    }

    // 更新日历视图
    private void updateCalendarView(int arg0, CustomDate date) {
        mShowViews = adapter.getAllItems();
        if (mDirection == SildeDirection.RIGHT) {
            mShowViews[arg0 % mShowViews.length].rightSlide();
        } else if (mDirection == SildeDirection.LEFT) {
            mShowViews[arg0 % mShowViews.length].leftSlide();
        }
        mShowViews[arg0 % mShowViews.length].setCurDate(date);
        mDirection = SildeDirection.NO_SILDE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnPreMonth) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);

        } else if (i == R.id.btnNextMonth) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

        } else {
        }
    }

    @Override
    public void clickDate(CustomDate date) {
        this.curDate = date;
        if(calendarListener!=null){
            calendarListener.onClickDate(date);
        }
    }

    public CustomDate getCurDate() {
        return curDate;
    }

    @Override
    public void changeDate(CustomDate date) {
        tvCurrentMonth.setText(date.year + "年" +date.month + "月");
        if(calendarListener!=null){
            calendarListener.onChangeMonth(date);
        }
    }

    public void setCalendarListener(CalendarListener listener) {
        this.calendarListener = listener;
    }

    public interface CalendarListener {
        void onClickDate(CustomDate date);
        void onChangeMonth(CustomDate date);
    }
}
