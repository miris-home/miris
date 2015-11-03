package com.miris.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.miris.R;
import com.miris.net.CalendarListData;
import com.miris.ui.adapter.AddressAdapter;
import com.miris.ui.view.SwipeLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by Miris on 09.02.15.
 */
public class AddressActivity extends BaseActivity
        implements AddressAdapter.OnFeedItemClickListener{
    @InjectView(R.id.rvAddress)
    RecyclerView rvAddress;
    LinearLayoutManager linearLayoutManager;
    private AddressAdapter addressAdapterr;
    ProgressDialog myLoadingDialog;
    List<ParseObject> ob;

    private SwipeLayout sample1, sample2, sample3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

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
    public void onSendMessage(View v, int position) {
        Intent messagentent = new Intent(Intent.ACTION_SENDTO);
        messagentent.setData(Uri.parse("smsto:" + calendarData.get(position).getuser_phonenumber()));
        startActivity(messagentent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onSendCall(View v, int position) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + calendarData.get(position).getuser_phonenumber()));
        startActivity(callIntent);
        overridePendingTransition(0, 0);
    }

    private void showDialog() {
        myLoadingDialog = new ProgressDialog(AddressActivity.this);
        myLoadingDialog.setMessage(getString(R.string.show_lodingbar));
        myLoadingDialog.setIndeterminate(false);
        myLoadingDialog.setCancelable(false);
        myLoadingDialog.show();
    }

    class loadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (myLoadingDialog != null) {
                if (!myLoadingDialog.isShowing()) {
                    showDialog();
                }
            }

        }
        @Override
        protected Void doInBackground(Void... arg0) {
            calendarData = new ArrayList<CalendarListData>();

            ParseQuery<ParseObject> offerQuery = ParseQuery.getQuery("miris_member");
            offerQuery.orderByDescending("createdAt");

            try {
                ob = offerQuery.find();
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            for (ParseObject country : ob) {
                ParseFile userFile = (ParseFile) country.get("user_img");
                String userImgurl = null;
                userImgurl = userFile.getUrl();
                if (isCancelled()) {
                    return null;
                }
                calendarData.add(new CalendarListData(
                        country.get("user_id").toString(),
                        country.get("user_name").toString(),
                        country.get("user_age").toString(),
                        userImgurl,
                        country.get("user_rank").toString(),
                        country.get("user_email").toString(),
                        country.get("user_phonenumber").toString()));
            }
            return null ;
        }
        @Override
        protected void onPostExecute(Void result) {
            addressAdapterr = new AddressAdapter(AddressActivity.this, calendarData);
            rvAddress.setAdapter(addressAdapterr);
            addressAdapterr.setOnFeedItemClickListener(AddressActivity.this);

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
            addressAdapterr.updateItems(true);
        }
    }
}
