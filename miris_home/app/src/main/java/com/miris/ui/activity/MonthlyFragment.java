package com.miris.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.miris.R;
import com.miris.ui.cal.OneMonthView;

import com.miris.ui.view.VerticalViewPager;

import java.util.Calendar;



/**
 * 한달뷰를 포함한 프래그먼트
 * @author brownsoo
 *
 */
public class MonthlyFragment extends Fragment {

    private static final String TAG = MConfig.TAG;
    private static final String NAME = "MonthlyFragment";
    private final String CLASS = NAME + "@" + Integer.toHexString(hashCode());
    
    public static final String ARG_YEAR = "year";
    public static final String ARG_MONTH = "month";

    /**
     * 달력의 변화를 확인하기 위한 리스너
     * @author Brownsoo
     *
     */
    public interface OnMonthChangeListener {
        /**
         * 날짜가 바뀔 때
         * @param year 년
         * @param month 월
         */
        void onChange(int year, int month);
    }
    
    /**
     * 가짜 리스너.
     */
    private OnMonthChangeListener dummyListener = new OnMonthChangeListener() {
        @Override
        public void onChange(int year, int month) {}
    };
    
    private OnMonthChangeListener listener = dummyListener;
    
    private VerticalViewPager vvpager;
    private MonthlySlidePagerAdapter adapter;
    int mYear = -1;
    int mMonth = -1;

    public static MonthlyFragment newInstance(int year, int month) {

        HLog.d(TAG, NAME, "newInstance " + year + "/" + month);

        MonthlyFragment fragment = new MonthlyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_YEAR, year);
        args.putInt(ARG_MONTH, month);
        fragment.setArguments(args);
        return fragment;
    }
    public MonthlyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mYear = getArguments().getInt(ARG_YEAR);
            mMonth = getArguments().getInt(ARG_MONTH);
        }
        else {
            Calendar now = Calendar.getInstance();
            mYear = now.get(Calendar.YEAR);
            mMonth = now.get(Calendar.MONTH);
        }

        HLog.d(TAG, CLASS, "onCreate " + mYear + "." + mMonth);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_monthly, container, false);

        vvpager = (VerticalViewPager) v.findViewById(R.id.vviewPager);
        adapter = new MonthlySlidePagerAdapter(getActivity(), mYear, mMonth);
        vvpager.setAdapter(adapter);
        vvpager.setOnPageChangeListener(adapter);
        vvpager.setCurrentItem(adapter.getPosition(mYear, mMonth));
        vvpager.setOffscreenPageLimit(1);
        
        return v;
    }
    
    @Override
    public void onDetach() {
        setOnMonthChangeListener(null);
        super.onDetach();
    }
    

    public int getYear() {
        return mYear;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setOnMonthChangeListener(OnMonthChangeListener listener) {
        if(listener == null) this.listener = dummyListener;
        else this.listener = listener;
    }

    /**
     * 년월이<br>
     * 년, 월을 담기 위한 간단한 오브젝트
     * @author Brownsoo
     *
     */
    public class YearMonth {
        public int year;
        public int month;
        
        public YearMonth(int year, int month) {
            this.year = year;
            this.month = month;
        }
    }

    /**
     * 
     * 한달씩 뷰를 생성하는 아답터
     * 
     * @author Brownsoo
     *
     */
    class MonthlySlidePagerAdapter extends PagerAdapter
        implements ViewPager.OnPageChangeListener {

        @SuppressWarnings("unused")
        private Context mContext;
        
        private OneMonthView[] monthViews;
        /** 위치계산을 위한 기준 년 */
        final static int BASE_YEAR = 2015;
        /** 위치계산을 위한 기준 월 */
        final static int BASE_MONTH = Calendar.JANUARY;
        /** 뷰페이저에서 재사용할 페이지 갯수 */
        final static int PAGES = 5;
        /** 루프수를 1000 이상 설정할 수 있겠지만, 이정도면 무한 스크롤이라고 생각하자 */
        final static int LOOPS = 1000;
        /** 기준 위치, 기준 날짜에 해당하는 위치 */
        final static int BASE_POSITION = PAGES * LOOPS / 2;
        /** 기준 날짜를 기반한 Calendar */
        final Calendar BASE_CAL;
        /** 이전 위치 */
        private int previousPosition;
        
        public MonthlySlidePagerAdapter(Context context, int startYear, int startMonth) {
            this.mContext = context;
            //기준 Calendar 지정
            Calendar base = Calendar.getInstance();
            base.set(BASE_YEAR, BASE_MONTH, 1);
            BASE_CAL = base;
            
            monthViews = new OneMonthView[PAGES];
            for(int i = 0; i < PAGES; i++) {
                monthViews[i] = new OneMonthView(getActivity());
            }
        }
        
        /**
         * 년월이 구하기
         * @param position 페이지 위치
         * @return position 위치에 해당하는 년월이
         */
        public YearMonth getYearMonth(int position) {
            Calendar cal = (Calendar)BASE_CAL.clone();
            cal.add(Calendar.MONTH, position - BASE_POSITION);
            return new YearMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
        }
        
        /**
         * 페이지 위치 구하기
         * @param year 년
         * @param month 월
         * @return 페이지 위치
         */
        public int getPosition(int year, int month) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, 1);
            return BASE_POSITION + howFarFromBase(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
        }

        /**
         * 기준 날짜를 기준으로 몇달이 떨어져 있는지 확인
         * @param year 비교할 년
         * @param month 비교할 월
         * @return 달 수, count of months
         */
        private int howFarFromBase(int year, int month) {
            
            int disY = (year - BASE_YEAR) * 12;
            int disM = month - BASE_MONTH;
            
            return disY + disM;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            
            HLog.d(TAG, CLASS, "instantiateItem " + position);
            
            int howFarFromBase = position - BASE_POSITION;
            Calendar cal = (Calendar) BASE_CAL.clone();
            cal.add(Calendar.MONTH, howFarFromBase);
            
            position = position % PAGES;
            
            container.addView(monthViews[position]);
            
            monthViews[position].make(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
            
            return monthViews[position];
        }
        
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            HLog.d(TAG, CLASS, "destroyItem " + position);
            container.removeView((View) object);
        }        
        
        @Override
        public int getCount() {
            return PAGES * LOOPS;
        }
        
        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
        
        @Override
        public void onPageScrollStateChanged(int state) {
            switch(state) {
            case ViewPager.SCROLL_STATE_IDLE:
                HLog.d(TAG, CLASS, "SCROLL_STATE_IDLE");
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                HLog.d(TAG, CLASS, "SCROLL_STATE_DRAGGING");
                previousPosition = vvpager.getCurrentItem();
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                HLog.d(TAG, CLASS, "SCROLL_STATE_SETTLING");
                break;
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            
            //HLog.d(TAG, CLASS, position + "-  " + positionOffset);
            if(previousPosition != position) {
                previousPosition = position;
                
                YearMonth ym = getYearMonth(position);
                listener.onChange(ym.year, ym.month);
                
                HLog.d(TAG, CLASS, position + " onPageScrolled-  " + ym.year + "." + ym.month);
            }
        }

        @Override
        public void onPageSelected(int position) {
        }
    }
}
