package com.example.pjt_student;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity implements TabHost.OnTabChangeListener {

    ImageView studentImageView;
    TextView nameView;
    TextView phoneView;
    TextView emailView;
    TabHost host;

    int studentId = 1; //select where 조건

    TextView addScoreView;
    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0, btnBack, btnAdd;

    MyView scoreView;

    ListView listView;
    //SimpleAdapter 가 요구하는 항목구성 집합데이터...
    ArrayList<HashMap<String, String>> scoreList;
    SimpleAdapter sa;

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initData();
        initTab();
        initAddScore();
        initSpanneable();
        initList();
        initWebView();
    }

    private void initData() {
        studentImageView = findViewById(R.id.detail_student_image);
        nameView = findViewById(R.id.detail_name);
        phoneView = findViewById(R.id.detail_phone);
        emailView = findViewById(R.id.detail_email);
        scoreView = findViewById(R.id.detail_score);

        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from tb_student where _id = ?", new String[]{String.valueOf(studentId)});

        String photo = null;


        if(cursor.moveToFirst()) {
            nameView.setText(cursor.getString(1));
            emailView.setText(cursor.getString(2));
            phoneView.setText(cursor.getString(3));

            photo = cursor.getString(4);

            int score = 0;
            Cursor scoreCursor = db.rawQuery("select score from tb_score where student_id=? order by date desc limit 1",
                    new String[]{String.valueOf(studentId)});

            if(scoreCursor.moveToFirst()) {
                score = scoreCursor.getInt(0);
            }

            scoreView.setScore(score);
        }

    }

    private void initTab() {
        host = findViewById(R.id.host);
        host.setOnTabChangedListener(this);
        //너네 하위에 FrameLayout, TabWidget이 선언되어 있을거다. 선언된대로 초기화
        host.setup();

        //tab 화면 하나가 TabSpec로 표현되고 spec에 button 이 눌렸을 때, 나오는 content 가 결합

        TabHost.TabSpec spec = host.newTabSpec("tab1");
        spec.setIndicator("score");
        spec.setContent(R.id.detail_score_list);

        host.addTab(spec);


        spec = host.newTabSpec("tab2");
        spec.setIndicator("chart");
        spec.setContent(R.id.detail_score_chart);

        host.addTab(spec);

        spec = host.newTabSpec("tab3");
        spec.setIndicator("add");
        spec.setContent(R.id.detail_score_add);

        host.addTab(spec);

        spec = host.newTabSpec("tab4");
        spec.setIndicator("memo");
        spec.setContent(R.id.detail_memo);

        host.addTab(spec);

    }

    private void initAddScore() {

        btn0 = (Button) findViewById(R.id.key_0);
        btn1 = (Button) findViewById(R.id.key_1);
        btn2 = (Button) findViewById(R.id.key_2);
        btn3 = (Button) findViewById(R.id.key_3);
        btn4 = (Button) findViewById(R.id.key_4);
        btn5 = (Button) findViewById(R.id.key_5);
        btn6 = (Button) findViewById(R.id.key_6);
        btn7 = (Button) findViewById(R.id.key_7);
        btn8 = (Button) findViewById(R.id.key_8);
        btn9 = (Button) findViewById(R.id.key_9);
        btnBack = (Button) findViewById(R.id.key_back);
        btnAdd = (Button) findViewById(R.id.key_add);

        addScoreView = (TextView) findViewById(R.id.key_edit);

        btn0.setOnClickListener(addScoreListener);
        btn1.setOnClickListener(addScoreListener);
        btn2.setOnClickListener(addScoreListener);
        btn3.setOnClickListener(addScoreListener);
        btn4.setOnClickListener(addScoreListener);
        btn5.setOnClickListener(addScoreListener);
        btn6.setOnClickListener(addScoreListener);
        btn7.setOnClickListener(addScoreListener);
        btn8.setOnClickListener(addScoreListener);
        btn9.setOnClickListener(addScoreListener);
        btnBack.setOnClickListener(addScoreListener);
        btnAdd.setOnClickListener(addScoreListener);

    }

    View.OnClickListener addScoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v == btnAdd) {
                long date = System.currentTimeMillis();
                String score = addScoreView.getText().toString();

                DBHelper helper = new DBHelper(DetailActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();

                db.execSQL("insert into tb_score (student_id, date, score) values (?,?,?)" ,
                        new String[]{String.valueOf(studentId), String.valueOf(date), score});

                db.close();

                host.setCurrentTab(0);
                addScoreView.setText("0");

                scoreView.setScore(Integer.parseInt(score));

                HashMap<String, String> map = new HashMap<>();
                map.put("score", score);
                Date d = new Date(date);
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                map.put("date", sd.format(d));
                scoreList.add(map);

                //반영명령
                sa.notifyDataSetChanged();

            } else if(v == btnBack) {
                String score = addScoreView.getText().toString();
                if(score.length() == 1) {
                    addScoreView.setText("0");
                } else {
                    String newScore = score.substring(0, score.length()-1);
                    addScoreView.setText(newScore);
                }
            } else {
                //이벤트가 발생한 버튼 객체 획득해서 그 문자열을 숫자값으로...
                Button btn = (Button) v;
                String txt = btn.getText().toString();

                String score = addScoreView.getText().toString();
                if(score.equals("0")) {
                    addScoreView.setText(txt);
                } else {
                    String newScore = score + txt;
                    int intScore = Integer.parseInt(newScore);

                    if(intScore > 100) {
                        Toast toast = Toast.makeText(DetailActivity.this, R.string.read_add_score_over_score, Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        addScoreView.setText(newScore);
                    }
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initSpanneable() {

        TextView spanView = findViewById(R.id.spanView);
        TextView htmlView = findViewById(R.id.htmlView);

        String data = spanView.getText().toString();
        Spannable spannable = (Spannable) spanView.getText();

        URLSpan urlSpan = new URLSpan("") {
            @Override
            public void onClick(View widget) {
                Toast toast = Toast.makeText(DetailActivity.this, "more click", Toast.LENGTH_SHORT);
                toast.show();
            }
        };

        int pos = data.indexOf("EXID");

        while(pos > -1) {
            spannable.setSpan(new ForegroundColorSpan(Color.RED), pos, pos+4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            pos = data.indexOf("EXID", pos + 1);
        }

        pos = data.indexOf("more");
        spannable.setSpan(urlSpan, pos, pos+4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanView.setMovementMethod(LinkMovementMethod.getInstance());

        String html = "<font color='blue'>HANI</font><br/><img src='myImage'/>";
        htmlView.setText(Html.fromHtml(html, new MyImageGetter(), null));

    }

    class MyImageGetter implements Html.ImageGetter {

        //매개변수가 img 태그의 src 값
        @Override
        public Drawable getDrawable(String source) {
            if(source.equals("myImage")) {
                Drawable dr = ResourcesCompat.getDrawable(getResources(), R.drawable.hani_1, null);
                dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());

                return dr;
            }
            return null;
        }
    }

    private void initList() {
        listView = findViewById(R.id.detail_score_list);
        scoreList = new ArrayList<>();

        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select score, date from tb_score where student_id=? order by date desc", new String[]{String.valueOf(studentId)});

        while(cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("score", cursor.getString(0));
            Date d = new Date(Long.parseLong(cursor.getString(1)));
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            map.put("date", sd.format(d));
            scoreList.add(map);
        }
        db.close();

        sa = new SimpleAdapter(this, scoreList, R.layout.read_list_item, new String[]{"score", "date"}, new int[]{R.id.read_list_score, R.id.read_list_date});

        listView.setAdapter(sa);
    }

    private void initWebView() {
        webView = findViewById(R.id.detail_score_chart);

        WebSettings settings = webView.getSettings();
        //js 엔진이 기본적으로 꺼져있다.
        settings.setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new JavascritTest(), "android");

    }

    public class JavascritTest {

        @JavascriptInterface
        public String getWebData() {
            StringBuffer sb = new StringBuffer();
            sb.append("[");

            if(scoreList.size() <= 10) {
                int j = 0 ;

                for(int i = scoreList.size(); i>0 ;  i--) {
                    sb.append("[" + j + ",");
                    sb.append(scoreList.get(i-1).get("score"));
                    sb.append("]");

                    if(i>1) sb.append(",");
                    j++;
                }
            } else {

                int j = 0 ;

                for(int i = 10; i>0 ;  i--) {
                    sb.append("[" + j + ",");
                    sb.append(scoreList.get(i-1).get("score"));
                    sb.append("]");

                    if(i>1) sb.append(",");
                    j++;
                }
            }

            sb.append("]");
            Log.d("kkang", sb.toString());

            return sb.toString();

        }
    }

    @Override
    public void onTabChanged(String tabId) {
        if(tabId.equals("tab2")) {
            webView.loadUrl("file:///android_asset/test.html");
        }
    }
}
