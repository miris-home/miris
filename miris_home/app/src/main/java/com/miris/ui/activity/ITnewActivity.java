package com.miris.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.miris.R;
import com.miris.net.ItnewsBanner;
import com.miris.net.ItnewsListData;
import com.miris.ui.adapter.ITnewAdapter;
import com.miris.ui.view.BannerLayout;

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

        //다음 IT뉴스 리스트
        String webItpage = "http://media.daum.net/syndication/digital.rss";
        //노컷뉴스 속보 배너
        String webItpage_banner = "http://rss.nocutnews.co.kr/nocutnews.xml";

        @Override
        protected void onPreExecute() {
            showDialog();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            itnewsListDatas = new ArrayList<ItnewsListData>();
            itnewsBanners = new ArrayList<ItnewsBanner>();

            try {
                DocumentBuilderFactory builderFactory1 = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder1 = builderFactory1.newDocumentBuilder();
                URL url1 = new URL(webItpage_banner);

                HttpURLConnection conn1 = (HttpURLConnection) url1.openConnection();

                InputStream instream1 = conn1.getInputStream();

                Document document1 = builder1.parse(instream1);

                Element docEle1 = document1.getDocumentElement();
                NodeList nodelist1 = docEle1.getElementsByTagName("item");
                if ((nodelist1 != null) && (nodelist1.getLength() > 0)) {
                    for (int i = 0; i < nodelist1.getLength(); i++) {

                        Element entry1 = (Element)nodelist1.item(i);
                        String title1 = entry1.getElementsByTagName("title").item(0).getTextContent();
                        String description1 = entry1.getElementsByTagName("description").item(0).getTextContent();
                        String img1 = entry1.getElementsByTagName("image").item(0).getTextContent();
                        String link1 = entry1.getElementsByTagName("link").item(0).getTextContent();

                        if (img1.equals("")){
                            continue;
                        }

                        itnewsBanners.add(new ItnewsBanner(
                                img1,
                                title1,
                                description1,
                                link1));
                    }
                }

                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();

                URL url = new URL(webItpage);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

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

            BannerLayout bannerLayout = (BannerLayout) findViewById(R.id.banner);

            final List<String> urls = new ArrayList<>();
            final List<String> urlstext = new ArrayList<>();
            for (int i= 0; i < 5; i++) {
                urls.add(itnewsBanners.get(i).getImg());
                urlstext.add(itnewsBanners.get(i).getTitle());
            }
            bannerLayout.setViewUrls(urls, urlstext);

            bannerLayout.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
                @Override
                public void onItemClick(int position) {

                    Intent intent = new Intent(getApplication(),ITnewDetail.class);
                    intent.putExtra("url", itnewsBanners.get(position).getlink());
                    intent.putExtra("title", itnewsBanners.get(position).getTitle());
                    intent.putExtra("img", itnewsBanners.get(position).getImg());
                    startActivity(intent);
                }
            });

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
