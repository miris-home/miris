package com.miris.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.miris.R;
import com.miris.net.AddressListData;
import com.miris.ui.adapter.AddressAdapter;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.InjectView;

/**
 * Created by Miris on 09.02.15.
 */
public class AddressActivity extends BaseActivity
        implements AddressAdapter.OnFeedItemClickListener{
    @InjectView(R.id.rvAddress)
    RecyclerView rvAddress;
    @InjectView(R.id.inputSearch)
    EditText inputSearch;
    LinearLayoutManager linearLayoutManager;
    private AddressAdapter addressAdapter;
    ProgressDialog myLoadingDialog;
    List<ParseObject> ob;

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
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                addressAdapter.getFilter(inputSearch.getText().toString().toLowerCase(Locale.getDefault()));
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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
        messagentent.setData(Uri.parse("smsto:" + addressData.get(position).getuser_phonenumber()));
        startActivity(messagentent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onSendCall(View v, int position) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + addressData.get(position).getuser_phonenumber()));
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
            showDialog();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            addressData = new ArrayList<AddressListData>();

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
                addressData.add(new AddressListData(
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
            addressAdapter = new AddressAdapter(AddressActivity.this, addressData);
            rvAddress.setAdapter(addressAdapter);
            addressAdapter.setOnFeedItemClickListener(AddressActivity.this);

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
            addressAdapter.updateItems(true);
        }
    }
}
