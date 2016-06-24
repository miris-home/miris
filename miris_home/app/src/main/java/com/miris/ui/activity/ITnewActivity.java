package com.miris.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.miris.R;
import com.miris.net.ItnewsListData;
import com.miris.ui.adapter.ITnewAdapter;
import com.parse.ParseObject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import butterknife.InjectView;

/**
 * Created by Miris on 09.02.15.
 */
public class ITnewActivity extends BaseActivity
        implements ITnewAdapter.OnNewsItemClickListener{
    @InjectView(R.id.rvAddress)
    RecyclerView rvAddress;
    LinearLayoutManager linearLayoutManager;
    private ITnewAdapter iTnewAdapter;
    ProgressDialog myLoadingDialog;
    List<ParseObject> ob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itnews);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setupFeed();
    }

    private void setupFeed() {
        linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvAddress.setLayoutManager(linearLayoutManager);
        new loadDataTask().execute();
    }

    @Override
    public void onSendImg(View v, int position) {
        Intent intent = new Intent(this,ITnewDetail.class);
        intent.putExtra("url", itnewsListDatas.get(position).getlink());
        intent.putExtra("title", itnewsListDatas.get(position).getTitle());
        intent.putExtra("img", itnewsListDatas.get(position).getImg());
        startActivity(intent);
    }

    @Override
    public void onSendTitle(View v, int position) {
        Intent intent = new Intent(this,ITnewDetail.class);
        intent.putExtra("url", itnewsListDatas.get(position).getlink());
        intent.putExtra("title", itnewsListDatas.get(position).getTitle());
        intent.putExtra("img", itnewsListDatas.get(position).getImg());
        startActivity(intent);
    }

    @Override
    public void onSendName(View v, int position) {
        Intent intent = new Intent(this,ITnewDetail.class);
        intent.putExtra("url", itnewsListDatas.get(position).getlink());
        intent.putExtra("title", itnewsListDatas.get(position).getTitle());
        intent.putExtra("img", itnewsListDatas.get(position).getImg());
        startActivity(intent);
    }

    @Override
    public void onSendLinearLayout(View v, int position) {
        Intent intent = new Intent(this,ITnewDetail.class);
        intent.putExtra("url", itnewsListDatas.get(position).getlink());
        intent.putExtra("title", itnewsListDatas.get(position).getTitle());
        intent.putExtra("img", itnewsListDatas.get(position).getImg());
        startActivity(intent);
    }

    private void showDialog() {
        myLoadingDialog = new ProgressDialog(ITnewActivity.this);
        myLoadingDialog.setMessage(getString(R.string.show_lodingbar));
        myLoadingDialog.setIndeterminate(false);
        myLoadingDialog.setCancelable(false);
        myLoadingDialog.show();
    }


    class loadDataTask extends AsyncTask<Void, Void, Void> {
        StringBuffer buffer=new StringBuffer();
/*
        String str= "안드로이드"; //EditText에 작성된 Text얻어오기
        String location = URLEncoder.encode(str); //한글의 경우 인식이 안되기에 utf-8 방식으로 encoding..

        String webItpage="http://openapi.naver.com/search"   //요청 URL
                +"?key=c1b406b32dbbbbeee5f2a36ddc14067f"     //key 값
                +"&target=news"                     //검색서비스 api명세
                +"&query="+location                 //지역검색 요청값
                +"&display=10"                      //검색 출력 건수  10~1007
]                +"&start=1"                         //검색 시작 위치  1~1000
                +"&sort=sim";                         //검색 시작 위치  1~1000
*/
        //String webItpage = "http://newssearch.naver.com/search.naver?where=rss&query=IT&field=0";
        //String webItpage = "http://www.inews24.com/rss/news_it.xml";
        String webItpage = "http://media.daum.net/syndication/digital.rss";



        @Override
        protected void onPreExecute() {
            showDialog();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            itnewsListDatas = new ArrayList<ItnewsListData>();

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

            try {
                DocumentBuilder builder = builderFactory.newDocumentBuilder();

                URL url = new URL(webItpage);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                int resCode = conn.getResponseCode();

                InputStream instream = conn.getInputStream();

                Document document = builder.parse(instream);

                Element docEle = document.getDocumentElement();
                NodeList nodelist = docEle.getElementsByTagName("item");
                if ((nodelist != null) && (nodelist.getLength() > 0)) {
                    for (int i = 0; i < nodelist.getLength(); i++) {

                        Element entry = (Element)nodelist.item(i);
                        String img = "";
                        String title = entry.getElementsByTagName("title").item(0).getTextContent();
                        String description = entry.getElementsByTagName("description").item(0).getTextContent();

                        if (entry.getElementsByTagName("enclosure").getLength() > 0) {
                            img = entry.getElementsByTagName("enclosure").item(0).getAttributes().getNamedItem("url").getTextContent();
                        }

                        String link = entry.getElementsByTagName("link").item(0).getTextContent();
                        String dueDate = entry.getElementsByTagName("pubDate").item(0).getTextContent();
                        String author = entry.getElementsByTagName("dc:creator").item(0).getTextContent();

                        Log.e("PHJ", "img===" + img);
                        Log.e("PHJ", "title===" + title);
                        Log.e("PHJ", "description===" + description);
                        Log.e("PHJ", "link===" + link);
                        Log.e("PHJ", "dueDate===" + dueDate);
                        Log.e("PHJ", "author===" + author);

                        itnewsListDatas.add(new ItnewsListData(
                                img,
                                title,
                                description,
                                link,
                                dueDate,
                                author));
                    }
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            catch (SAXException e) {
                e.printStackTrace();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            iTnewAdapter = new ITnewAdapter(ITnewActivity.this, itnewsListDatas);
            rvAddress.setAdapter(iTnewAdapter);
            iTnewAdapter.setOnNewsItemClickListener(ITnewActivity.this);

            rvAddress.setOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
            if (myLoadingDialog != null) {
                myLoadingDialog.dismiss();
            }
            iTnewAdapter.updateItems(true);
        }
    }
}
