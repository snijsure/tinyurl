package snijsure.com.tinyurl;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.webkit.WebViewClient;
/**
 * Created by subodhnijsure on 2/8/16.
 * This fragment show button with tinyurl.
 * also shows preview using webview.
 */
public class ShowUrlFragment extends Fragment {
    static final String TAG = "ShowUrl";

    String mShortUrl;
    Button mButton;
    WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.url_viewer_fragment, container,false);

        String originalUrl = this.getArguments().getString("origUrl");
        mShortUrl = this.getArguments().getString("shortUrl");

        Log.d(TAG, "OriginalUrl " + originalUrl);
        Log.d(TAG, "shortUrl + " + mShortUrl);
        mButton = (Button) v.findViewById(R.id.open_url_button_id);
        mButton.setText(mShortUrl);

        TextView t = (TextView) v.findViewById(R.id.original_url);
        t.setText(getResources().getString(R.string.result_title_string)
                + " " + originalUrl);
        mWebView = (WebView) v.findViewById(R.id.webpreview);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl();
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(mShortUrl);
    }

    private void openUrl() {
        Log.d(TAG, "Onclick is called for mActionButton how");
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(mShortUrl));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setPackage("com.android.chrome");
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            // One could also query package manager to see chrome is installed.
            // Chrome is probably not installed
            // Try with the default browser
            i.setPackage(null);
            startActivity(i);
        }
    }
    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
    }
}
