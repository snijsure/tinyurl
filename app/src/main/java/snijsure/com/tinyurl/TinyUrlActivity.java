package snijsure.com.tinyurl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/*
 * This is main activity that starts everything */
public class TinyUrlActivity extends AppCompatActivity {
    static final String TAG = "MainFragment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiny_url);
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MakeTinyUrlFragment())
                    .commit();
        }
    }
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
