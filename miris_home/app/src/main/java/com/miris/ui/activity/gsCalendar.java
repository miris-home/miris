package com.miris.ui.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class gsCalendar extends Activity
{
    /// 당연한 이야기지만 한줄에 7일씩 안들어갈 경우 휴일처리는 고려하지 않았다 -_-;;
    final static int ROWS = 7 ; /// 줄/행수
    final static int COLS = 7 ; /// 칸/열수

//	final static int ROWS = 4 ; /// 줄/행수
//	final static int COLS = 14 ; /// 칸/열수
//

    Context m_context ;				/// context

    LinearLayout m_targetLayout ;	/// 달력을 넣을 레이아웃
    Button [] m_controlBtn ;		/// 달력 컨트롤할 버튼 4개 [전년도, 다음년도, 전달, 다음달]
    TextView [] m_viewTv ;			/// 년 월 일 표시할 텍스트뷰 3개[년, 월 , 일]

    Calendar m_Calendar ;			/// 사용할 달력

    LinearLayout [ ] m_lineLy ;		/// 7라인 요일표시 + 최대 6주
    LinearLayout [ ] m_cellLy ;		/// 7칸
    TextView [ ] m_cellTextBtn ;	/// 각 칸마다 넣을 텍스트뷰 (버튼처럼 이벤트 주려고 Btn 이라 붙였음)
    /// 사실 버튼으로 하고싶은데 버튼에 텍스트 넣으면 죽어도 상하좌우 여백이 들어가서
    /// 텍스트가 짤려서 TextView로 만들 수 밖에 없음

    LinearLayout [ ] m_horizontalLine ; /// 경계선 라인 가로
    LinearLayout [ ] m_verticalLine ;	/// 경계선 라인 세로

    int m_startPos ;				/// 요일을 찍기 시작 할 위치
    int m_lastDay ;					/// 그 달의 마지막날
    int m_selDay ;					/// 현재 선택된 날짜

    ////////////////////////////////////////

    float m_displayScale ;			/// 화면 사이즈에 따른 텍스트 크기 보정값 저장용
    float m_textSize ;				/// 텍스트 사이즈(위 라인의 변수와 곱해짐)
    float m_topTextSize ;			/// 요일텍스트 사이즈(역시 보정값과 곱해짐)

    int m_tcHeight = 100 ;			/// 요일 들어가는 부분 한칸의 높이
    int m_cWidth = 150 ;			/// 한칸의 넓이
    int m_cHeight = 200 ;			/// 한칸의 높이
    int m_lineSize = 1 ;			/// 경계선의 굵기

    static public class gsCalendarColorParam
    {
        int m_lineColor 			= 0xff000000 ;	/// 경계선 색
        int m_cellColor 			= 0xffffffff ;	/// 칸의 배경색
        int m_topCellColor 			= 0xffcccccc ;	/// 요일 배경색
        int m_textColor 			= 0xff000000 ;	/// 글씨색
        int m_sundayTextColor 		= 0xffff0000 ;	/// 일요일 글씨색
        int m_saturdayTextColor 	= 0xff0000ff ;	/// 토요일 글씨색
        int m_topTextColor 			= 0xff000000 ; 	/// 요일 글씨색
        int m_topSundayTextColor 	= 0xffff0000 ; 	/// 요일 일요일 글씨색
        int m_topSaturdatTextColor 	= 0xff0000ff ; 	/// 요일 토요일 글씨색

        int m_todayCellColor		= 0x999999ff ;	/// 선택날짜의 배경색
        int m_todayTextColor		= 0xffffffff ;  /// 선택날짜의 글씨색
    }

    gsCalendarColorParam m_colorParam ;

    /// 있으면 적용하고 없으면 bgcolor로 처리함( 각각 개별적으로 )
    Drawable m_bgImgId = null ;				/// 전체 배경이미지
    Drawable m_cellBgImgId = null ;			/// 한칸의 배경 이미지
    Drawable m_topCellBgImgId = null ;		/// 상단 요일 들어가는 부분의 배경 이미지

    Drawable m_todayCellBgImgId = null ; 	/// 선택 날짜의 배경 이미지

    /// 상단에 표시하는 요일 텍스트
    String [] m_dayText ={ "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" } ;

    ///////////////////////////////////////////

    Button m_preYearBtn ;			/// 전년도 버튼
    Button m_nextYearBtn ;			/// 다음년도 버튼
    Button m_preMonthBtn ;			/// 전월 버튼
    Button m_nextMonthBtn ;			/// 다음월 버튼

    TextView m_yearTv ;				/// 년 표시용 텍스트
    TextView m_mothTv ;				/// 월 표시용 텍스트
    TextView m_dayTv ;				/// 날짜 표시용 텍스트


    /// 휴일을 MMdd형식으로 넣는다.
    /// 구정이 2월 4 5 6이라면
    /// [0204] [0205] [0206] 이렇게 넣음
    ArrayList< Integer > m_holiDay = new ArrayList< Integer >( ) ;


    /// 생성자
    public gsCalendar( Context context, LinearLayout layout )
    {
        /// context저장
        m_context = context ;

        /// 타겟 레이아웃 저장
        m_targetLayout = layout ;

        /// 오늘 잘짜로 달력 생성
        m_Calendar = Calendar.getInstance( ) ;

        /// 표시할 각각의 레이어 생성
        m_lineLy = new LinearLayout[ COLS ] ;						/// 7줄의 레이아웃 생성
        m_cellLy = new LinearLayout[ COLS * ROWS ] ;				/// 그안에  줄당 7개씩 총 49개의 레이아웃 생성
        m_cellTextBtn = new TextView[ COLS * ROWS ] ;				/// 동일한 갯수의 버튼도 생성
        m_horizontalLine = new LinearLayout[ COLS - 1 ] ; 			/// 세로 구분선 레이아웃
        m_verticalLine = new LinearLayout[ ( COLS - 1 ) * ROWS ] ;	/// 가로 구분선 레이아웃

        /// 화면의 크기에 따른 보정값
        m_displayScale = context.getResources( ).getDisplayMetrics( ).density ;

        m_topTextSize = m_displayScale * 7.0f ;
        m_textSize = m_displayScale * 7.0f ;

        m_colorParam = new gsCalendarColorParam( ) ;


//        ROWS = 7 ; /// 줄/행수
//    	COLS = 14 ; /// 칸/열수
    }

    /// 달력을 생성한다.( 모든 옵션들[컬러값, 텍스트 크기 등]을 설정한 후에 마지막에 출력 할 때 호출)
    public void initCalendar( )
    {
        createViewItem( ) ;
        setLayoutParams( ) ;
        setLineParam( ) ;
        setContentext( ) ;
        setOnEvent( ) ;
        printView( ) ;
    }

    /// 컬러값 파라메터 설정
    public void setColorParam( gsCalendarColorParam param )
    {
        m_colorParam = param ;
    }

    /// 배경으로 쓸 이미지를 설정
    public void setBackground( Drawable bg )
    {
        m_bgImgId = bg ;
    }
    public void setCellBackground( Drawable bg )
    {
        m_cellBgImgId = bg ;
    }
    public void setTopCellBackground( Drawable bg )
    {
        m_topCellBgImgId = bg ;
    }

    public void setCalendarSize( int width, int height  )
    {
        m_cWidth = ( width - ( m_lineSize * COLS - 1 ) ) / COLS ;
        m_cHeight = ( height - ( m_lineSize * ROWS - 1 ) ) / ROWS ;
        m_tcHeight = ( height - ( m_lineSize * COLS - 1 ) ) / COLS ;
    }

    public void setCellSize( int cellWidth, int cellHeight, int topCellHeight  )
    {
        m_cWidth = cellWidth ;
        m_cHeight = cellHeight ;
        m_tcHeight = topCellHeight ;
    }

    public void setTopCellSize( int topCellHeight  )
    {
        m_tcHeight = topCellHeight ;
    }

    public void setCellSize( int allCellWidth, int allCellHeight )
    {
        m_cWidth = allCellWidth ;
        m_cHeight = allCellHeight ;
        m_tcHeight = allCellHeight ;
    }

    public void setTextSize( float size )
    {
        m_topTextSize = m_displayScale * size ;
        m_textSize = m_displayScale * size ;
    }

    public void setTextSize( float textSize, float topTextSize )
    {
        m_topTextSize = m_displayScale * topTextSize ;
        m_textSize = m_displayScale * textSize ;
    }


    public void redraw( )
    {
        m_targetLayout.removeAllViews( ) ;
        initCalendar( ) ;

    }


    //////////////////// 선택한 날짜칸에 변화를 주는 함수 //////////////////////////
    /// 이녀석이 불러졌을때 상태는 날짜가 오늘로 선택되어있거나 뭔가 선택했을 것임
    /// 그럼으로 m_cellLy[ 날짜 + m_startPos ].setTextColor( ) ;
    /// m_startPos가 구해져 있으니 날짜를 더하면 해당 날짜칸을 마음대로 바꿀 수 있음
    /// ////////////////////////////////////////////////////////////////////
    /// 선택된 날짜칸에 변화를 주기위한 함수 1호
    public void setSelectedDay( int cellColor, int textColor )
    {
        m_colorParam.m_todayCellColor = cellColor ;
        m_colorParam.m_todayTextColor = textColor ;
        m_cellTextBtn[ m_Calendar.get( Calendar.DAY_OF_MONTH ) + m_startPos - 1 ].setTextColor( textColor ) ;
        m_cellTextBtn[ m_Calendar.get( Calendar.DAY_OF_MONTH ) + m_startPos - 1 ].setBackgroundColor( cellColor ) ;
    }

    /// 선택된 날짜칸에 변화를 주기위한 함수 2호
    public void setSelectedDayTextColor( int textColor )
    {
        m_colorParam.m_todayTextColor = textColor ;
        m_cellTextBtn[ m_Calendar.get( Calendar.DAY_OF_MONTH ) + m_startPos - 1 ].setTextColor( textColor ) ;
    }

    /// 선택된 날짜칸에 변화를 주기위한 함수 3호
    public void setSelectedDay( Drawable bgimg )
    {
        m_todayCellBgImgId = bgimg ;
        m_colorParam.m_todayCellColor = 0x00000000 ;
        m_cellTextBtn[ m_Calendar.get( Calendar.DAY_OF_MONTH ) + m_startPos - 1 ].setBackgroundDrawable( bgimg ) ;
        Log.d("===",(m_Calendar.get( Calendar.DAY_OF_MONTH ) -1)+ "" ) ;
    }


    ///////////////////////////// 공휴일 처리 ///////////////////////
    /// 휴일을 MMdd형식으로 넣는다.
    /// 구정이 2월 4 5 6이라면
    /// [0204] [0205] [0206] 이렇게 넣음
    public void addHoliday( int holiday_MMdd )
    {
        m_holiDay.add( holiday_MMdd ) ;
    }

    /// 공휴일 리스트를 루프돌면서 해당 날짜를 일요일과 같은 색으로 변경
    public void applyHoliday( )
    {
        /// 현재 달력의 월을 구함
        Integer iMonth = m_Calendar.get( Calendar.MONTH ) + 1 ;

        /// 휴일로 저장된 모든 날짜 루프 줄줄 돌음
        for( int k = 0 ; k < m_holiDay.size( ) ; k++ )
        {
            int holiday = m_holiDay.get( k ) ;	/// 월과 일을 구한다음
            if( holiday / 100 == iMonth )		/// 월이 동일할 경우
            {
                /// 해당 날짜를 휴일 컬러로 변경
                m_cellTextBtn[ holiday % 100 + m_startPos ].setTextColor( m_colorParam.m_sundayTextColor ) ;
            }
        }
    }




    /// 세로줄 수만큼 루프 세로줄은 날짜 표기되는 줄 + 경계선 줄수
    public void createViewItem( )
    {
        /// 7줄이면 경계선 라인까지 합해서 13줄을 생성해야한다.
        for( int i = 0 ; i < ROWS * 2 - 1 ; i++ )
        {
            /// 짝수라인일때는
            if( i % 2 == 0 )
            {
                /// 라인은 13개 라인이지만 내용이 표시되는 라인은 7개임
                m_lineLy[i/2] = new LinearLayout( m_context ) ;
                m_targetLayout.addView( m_lineLy[i/2] ) ; // 만든 레이아웃을 자식으로 등록

                /// 날짜표기 칸과 경계선 합해서 13개의 칸을 생성
                for( int j = 0 ; j < COLS * 2 - 1 ; j++ )
                {

                    /// 달력 내용이 들어가는 칸
                    if( j % 2 == 0 )
                    {
                        int pos = ( ( i / 2 ) * COLS ) + ( j / 2 ) ;

                        Log.d( "pos1", "" +  pos ) ;
                        m_cellLy[ pos ] = new LinearLayout( m_context ) ;
                        m_cellTextBtn[ pos ] = new TextView( m_context ) ;
                        m_lineLy[ i / 2 ].addView( m_cellLy[ pos ] ) ;
                        m_cellLy[ pos ].addView( m_cellTextBtn[ pos ] ) ;

                    }
                    else	/// 이건 단순한 경계선
                    {
                        int pos = ( ( i / 2 ) * (COLS - 1) ) + ( j - 1 ) / 2 ;

                        Log.d( "pos2", "" +  pos ) ;
                        m_verticalLine[ pos ] = new LinearLayout( m_context ) ;
                        m_lineLy[ i / 2 ].addView( m_verticalLine[ pos ] ) ;
                    }
                }
            }
            else	/// 이건 가로줄 경계선
            {
                m_horizontalLine[ ( i - 1 ) / 2 ] = new LinearLayout( m_context ) ;
                m_targetLayout.addView( m_horizontalLine[ ( i - 1 ) / 2 ] ) ;


            }
        }
    }

    /// 레이아웃과 버튼의 배경색, 글씨색 등 ViewParams를 셋팅
    public void setLayoutParams( )
    {
        /// 메인 레이아웃은 세로로 나열
        m_targetLayout.setOrientation( LinearLayout.VERTICAL ) ;
        /// 만약 전체 배경이 있으면 넣어줌
        if( m_bgImgId != null )
        {
            m_targetLayout.setBackgroundDrawable( m_bgImgId ) ;
        }

        /// 세로줄 수만큼 루프 세로줄은 날짜 표기되는 줄 + 경계선 줄수
        for( int i = 0 ; i < ROWS * 2 - 1 ; i++ )
        {
            if( i % 2 == 0 )
            {
                /// 각 라인을 구성하는 레이아웃들은 가로로 나열~
                m_lineLy[i/2].setOrientation( LinearLayout.HORIZONTAL ) ;
                m_lineLy[i/2].setLayoutParams(	/// 레이아웃 사이즈는 warp_content로 설정
                        new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ) ) ;

                /// 한칸한칸 옵션을 설정
                for( int j = 0 ; j < COLS ; j++ )
                {
                    int cellnum = ( ( i / 2 ) * COLS ) + j ;
                    /// 한칸한칸을 구성하는 레이아웃 사이즈는 역시 wrap_content로 설정
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ) ;
                    //param.setMargins( 1, 1, 1, 1 ) ;	/// 마진을 1씩 줘서 라인을 그린다.
                    m_cellLy[ cellnum ].setLayoutParams( param ) ;
                    /// 한칸한칸 들어가는 버튼
                    m_cellTextBtn[ cellnum ].setGravity( Gravity.CENTER ) ;


                    /// 이하는 배경색 글씨색 글씨 크기 설정하는 부분


                    /// 첫라인은 월화수목금토일 표시하는 부분
                    if( i == 0 )
                    {
                        /// 요일 표시하는 부분의 넓이 높이
                        m_cellTextBtn[ cellnum ].setLayoutParams( new LinearLayout.LayoutParams( m_cWidth, m_tcHeight ) ) ;

                        /// 배경과 글씨색
                        if( m_topCellBgImgId != null )
                        {
                            m_cellLy[ cellnum ].setBackgroundDrawable( m_topCellBgImgId ) ;
                        }
                        else
                        {
                            m_cellLy[ cellnum ].setBackgroundColor( m_colorParam.m_topCellColor ) ;
                        }

                        /// 토요일과 일요일은 다른 컬러로 표시한다.
                        switch( j )
                        {
                            case 0:
                                m_cellTextBtn[ cellnum ].setTextColor( m_colorParam.m_topSundayTextColor ) ;
                                break ;
                            case 6:
                                m_cellTextBtn[ cellnum ].setTextColor( m_colorParam.m_topSaturdatTextColor ) ;
                                break ;
                            default:
                                m_cellTextBtn[ cellnum ].setTextColor( m_colorParam.m_topTextColor ) ;
                                break ;
                        }

                        /// 글씨 크기
                        m_cellTextBtn[ cellnum ].setTextSize( m_topTextSize ) ;
                    }
                    else			/// 이하는 날짜 표시하는 부분
                    {

                        /// 숫자 표시되는 부분의 넓이와 높이
                        m_cellTextBtn[ cellnum ].setLayoutParams( new LinearLayout.LayoutParams( m_cWidth, m_cHeight ) ) ;

                        /// bg와 글씨색
                        if( m_cellBgImgId != null )
                        {
                            m_cellLy[ cellnum ].setBackgroundDrawable( m_cellBgImgId ) ;
                        }
                        else
                        {
                            m_cellLy[ cellnum ].setBackgroundColor( m_colorParam.m_cellColor ) ;
                        }

                        /// 토요일과 일요일은 다른 컬러로 표시한다.
                        switch( j )
                        {
                            case 0:
                                m_cellTextBtn[ cellnum ].setTextColor( m_colorParam.m_sundayTextColor ) ;
                                break ;
                            case 6:
                                m_cellTextBtn[ cellnum ].setTextColor( m_colorParam.m_saturdayTextColor ) ;
                                break ;
                            default:
                                m_cellTextBtn[ cellnum ].setTextColor( m_colorParam.m_textColor ) ;
                                break ;
                        }

                        /// 글씨 크기
                        m_cellTextBtn[ cellnum ].setTextSize( m_textSize ) ;
                    }


                }
            }
        }
    }

    public void setLineParam( )
    {
        for( int i = 0 ; i < ROWS - 1 ; i ++ )
        {
            m_horizontalLine[ i ].setBackgroundColor( m_colorParam.m_lineColor ) ;	/// 라인색
            m_horizontalLine[ i ].setLayoutParams(	/// 가로 라인이니까 가로는 꽉 세로는 두께만큼
                    new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, m_lineSize ) ) ;
        }
        for( int i = 0 ; i < ROWS ; i ++ )
        {
            for( int j = 0 ; j < COLS - 1 ; j++ )
            {
                int pos = ( i * ( COLS - 1 ) ) + j ;
                m_verticalLine[ pos ].setBackgroundColor( m_colorParam.m_lineColor ) ; /// 라인색
                m_verticalLine[ pos ].setLayoutParams(	/// 세로 라인이니까 세로는 쭉~ 가로는 두께만큼
                        new LinearLayout.LayoutParams( m_lineSize, LayoutParams.FILL_PARENT ) ) ;
            }
        }
    }

    /// 달력을 구성하는 년 월 일을 셋팅하기
    public void setContentext( )
    {
        /// 달력을 하나 복사해서 작업한다.
        Calendar iCal = (Calendar) m_Calendar.clone( ) ;

        /// 날짜를 겟~
        m_selDay = iCal.get( Calendar.DATE ) ;

        /// 날짜를 1로 셋팅하여 달의 1일이 무슨 요일인지 구함
        iCal.set( Calendar.DATE, 1 ) ;
        /// 요일표기하는 맨 위 7칸 + 요일이 첫 시작하는 칸임
        m_startPos = COLS + iCal.get( Calendar.DAY_OF_WEEK ) - Calendar.SUNDAY ;

        /// 1달 더해서 다음달 1일로 만들었다가 1일을 빼면 달의 마지막날이 구해짐
        iCal.add( Calendar.MONTH, 1 ) ;
        iCal.add( Calendar.DATE, -1 ) ;

        m_lastDay = iCal.get( Calendar.DAY_OF_MONTH ) ;         /// 해달 달의 마지막날 겟~

        /// 0부터 6번칸까지는 월화수목금토일~ 로 채워넣음
        for( int k = 0 ; k < COLS ; k++ )
        {
            m_cellTextBtn[ k ].setText(  m_dayText[ k % 7 ] ) ;
        }

        /// 7번부터 처음 시작위치 전까지는 공백으로 채움
        for( int i = COLS ; i < m_startPos ; i++ )
        {
            m_cellTextBtn[ i ].setText( "" ) ;
        }

        /// 시작위치부터는 1부터 해서 달의 마지막날까지 숫자로 채움
        for( int i = 0 ; i < m_lastDay ; i++ )
        {
            m_cellTextBtn[ i + m_startPos ].setText( ( i + 1 ) + "" ) ;
        }

        /// 마지막날부터 끝까지는 공백으로 채움
        for( int i = m_startPos + m_lastDay ; i < COLS * ROWS ; i++ )
        {
            m_cellTextBtn[ i ].setText( "" ) ;
        }
    }

    /// 각 버튼들에 setOnClickListener 주기
    public void setOnEvent( )
    {
        /// 월화수목금토일 들어가있는 부분에는 눌러도 반응할 필요 없음
        for( int i = COLS ; i < COLS * ROWS ; i++ )
        {
            final int k = i ;
            m_cellTextBtn[i].setOnClickListener( new Button.OnClickListener( )
            {
                @Override
                public void onClick(View v)
                {

                    if( m_cellTextBtn[k].getText( ).toString( ).length() > 0 )
                    {
                        m_Calendar.set( Calendar.DATE, Integer.parseInt( m_cellTextBtn[k].getText( ).toString( ) ) ) ;
                        if( m_dayTv != null )
                            m_dayTv.setText( m_Calendar.get( Calendar.DAY_OF_MONTH ) + "" ) ;
                        printView( ) ;
                        myClickEvent( 	m_Calendar.get( Calendar.YEAR ),
                                m_Calendar.get( Calendar.MONTH ),
                                m_Calendar.get( Calendar.DAY_OF_MONTH ) ) ;
                    }
                }
            } ) ;
        }
    }

    /// 달력을 띄운 다음 년 월 일을 출력해줌
    public void printView( )
    {
        /// 텍스트 뷰들이 있으면 그 텍스트 뷰에다가 년 월 일을 적어넣음
        if( m_yearTv != null )
            m_yearTv.setText( m_Calendar.get( Calendar.YEAR ) + "" ) ;
        if( m_mothTv != null )
        {
            //int imonth =  iCal.get( Calendar.MONTH ) ;
            m_mothTv.setText( ( m_Calendar.get( Calendar.MONTH ) + 1 ) + "" ) ;
        }
        if( m_dayTv != null )
            m_dayTv.setText( m_Calendar.get( Calendar.DAY_OF_MONTH ) + "" ) ;

    }

    /// 년도와 월을 앞~ 뒤~로
    public void preYear( )
    {
        m_Calendar.add( Calendar.YEAR, -1 ) ;
        setContentext( ) ;
        printView( ) ;
    }
    public void nextYear( )
    {
        m_Calendar.add( Calendar.YEAR, 1 ) ;
        setContentext( ) ;
        printView( ) ;
    }
    public void preMonth( )
    {
        m_Calendar.add( Calendar.MONTH, -1 ) ;
        setContentext( ) ;
        printView( ) ;
    }
    public void nextMonth( )
    {
        m_Calendar.add( Calendar.MONTH, 1 ) ;
        setContentext( ) ;
        printView( ) ;
    }

    /// 텍스트뷰를 넣어주면 각각 뿌려줌 (빈게 들어있으면 안뿌림)
    public void setViewTarget( TextView [] tv )
    {
        m_yearTv = tv[0] ;
        m_mothTv = tv[1] ;
        m_dayTv = tv[2] ;
    }

    /// 버튼을 넣어주면 알아서 옵션 넣어줌 (역시나 빈게 있으면 이벤트 안넣음)
    public void setControl( Button [] btn )
    {
        m_preYearBtn = btn[0] ;
        m_nextYearBtn = btn[1] ;
        m_preMonthBtn = btn[2] ;
        m_nextMonthBtn = btn[3] ;

        if( m_preYearBtn != null )
            m_preYearBtn.setOnClickListener( new Button.OnClickListener( )
            {
                @Override
                public void onClick(View v)
                {
                    preYear( ) ;
                }
            } ) ;
        if( m_nextYearBtn != null )
            m_nextYearBtn.setOnClickListener( new Button.OnClickListener( )
            {
                @Override
                public void onClick(View v)
                {
                    nextYear( ) ;
                }
            } ) ;
        if( m_preMonthBtn != null )
            m_preMonthBtn.setOnClickListener( new Button.OnClickListener( )
            {
                @Override
                public void onClick(View v)
                {
                    preMonth( ) ;
                }
            } ) ;
        if( m_nextMonthBtn != null )
            m_nextMonthBtn.setOnClickListener( new Button.OnClickListener( )
            {
                @Override
                public void onClick(View v)
                {
                    nextMonth( ) ;
                }
            } ) ;
    }

    /// 원하는 포멧대로 날짜를 구해줌
    /// 예)
    /// String today = getData( "yyyy-MM-dd" )이런식?
    public String getData( String format )
    {
        SimpleDateFormat sdf = new SimpleDateFormat( format, Locale.US ) ;
        return sdf.format( new Date( m_Calendar.getTimeInMillis( ) ) ) ;
    }

    /// 달력에서 날짜를 클릭하면 이 함수를 부른다.
    public void myClickEvent( int yyyy, int MM, int dd )
    {
        Log.d( "yyyy", "" + yyyy ) ;
        Log.d( "MM", "" + MM ) ;
        Log.d( "dd", "" + dd ) ;
    }

    public int pixelToDip( int arg )
    {
        m_displayScale = m_context.getResources( ).getDisplayMetrics( ).density ;
        return (int) ( arg * m_displayScale ) ;
    }

    public gsCalendarColorParam getBasicColorParam( )
    {
        return new gsCalendarColorParam( ) ;
    }
}

