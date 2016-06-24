package com.miris.ui.comp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.miris.R;


/**
 * Created by Chenyc on 2015/6/29.
 */
public class DetailFragment extends Fragment {

    private static String urlName;

    public static DetailFragment newInstance(String info, String url) {
        Bundle args = new Bundle();
        DetailFragment fragment = new DetailFragment();
        args.putString("info", info);
        fragment.setArguments(args);
        urlName = url;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, null);

        WebView webView = (WebView) view.findViewById(R.id.webView);
        final ProgressBar home_progressbar = (ProgressBar) view.findViewById(R.id.home_progressbar);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.loadUrl(urlName);

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                home_progressbar.setVisibility(View.VISIBLE);

                if (newProgress == 100) {
                    home_progressbar.setProgress(newProgress);
                    home_progressbar.setVisibility(View.GONE);
                }
                home_progressbar.setProgress(newProgress);
            }
        });

        return view;

    }
}
