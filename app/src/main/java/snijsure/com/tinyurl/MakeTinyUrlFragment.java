package snijsure.com.tinyurl;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by subodhnijsure on 2/8/16.
 * This fragment shows text field and button. When user
 * clicks on button an AsyncTask is started to send GET request
 * Upon success a new fragment is shown.
 */
public class MakeTinyUrlFragment extends Fragment implements OnTaskResult {
    final static String TAG = "TinUrlFragment";

    private EditText mUrlTextView;
    private Button mActionButton;
    ProgressDialog mProgressDialog;
    Context mContext;
    Realm mRealm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fragment, container, false);
        mUrlTextView = (EditText) v.findViewById(R.id.url_text_id);
        mActionButton = (Button) v.findViewById(R.id.ok_button_id);
        mProgressDialog = new ProgressDialog(this.getActivity());
        mProgressDialog.setMessage("Shortening URL");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shortenUrlClick();
            }
        });
        try {
            mRealm = Realm.getInstance(getActivity());
            Log.d(TAG, "Realm object is created");
        }
        catch(io.realm.exceptions.RealmMigrationNeededException e) {
            mRealm = null;
        }
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        mContext = ctx;

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        if ( mProgressDialog != null )
            mProgressDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if ( mProgressDialog != null )
            mProgressDialog.dismiss();
    }

    // Check if object already exists in REALM database.
    // Returns true if does else false.
    public boolean checkIfItemExistsInDb(final String searchString) {

        RealmResults<TinyUrlDBEntry> results1 =
                mRealm.where(TinyUrlDBEntry.class).equalTo("url", searchString).findAll();
        if (results1.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public String getTinyUrlFromDB(final String searchString) {

        RealmResults<TinyUrlDBEntry> results1 =
                mRealm.where(TinyUrlDBEntry.class).equalTo("url", searchString).findAll();
        if (results1.size() == 1) {
            TinyUrlDBEntry entry = results1.get(0);
            return entry.getTinyUrl();
        } else {
            return "";
        }
    }

    public void shortenUrlClick() {
        String str = mUrlTextView.getText().toString();
        if (URLUtil.isValidUrl(str)) {
            // This item already exists in our DB so need to save it
            if (checkIfItemExistsInDb(str)) {
                mProgressDialog = null;
                String result = getTinyUrlFromDB(str);
                onTaskSuccess(result);
            } else {
                Log.d(TAG, "Shorten url " + str);

                mProgressDialog.show();
                ShortenUrlTask task = new ShortenUrlTask(str, this);
                task.executeTask();
            }
        } else {
            Toast.makeText(this.getActivity(), R.string.invalid_url_error,
                    Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onTaskSuccess(String result) {
        String originalUrl;
        Log.d(TAG, "Shorten Url task was successful");
        if (mProgressDialog != null)
            mProgressDialog.dismiss();

        originalUrl = mUrlTextView.getText().toString();

        if (mRealm != null) {
            if (!checkIfItemExistsInDb(originalUrl)) {
                mRealm.beginTransaction();
                TinyUrlDBEntry realmObj = mRealm.createObject(TinyUrlDBEntry.class);
                realmObj.setUrl(originalUrl);
                realmObj.setTinyUrl(result);
                Log.v(TAG, "Adding object to DB URL : " + realmObj.getUrl() + " TinyURL " + realmObj.getTinyUrl());
                mRealm.commitTransaction();
            } else {
                Log.d(TAG, "This object " + originalUrl + " already exists in DB");
            }
            RealmResults<TinyUrlDBEntry> allObjs = mRealm.where(TinyUrlDBEntry.class).findAll();
            Log.d(TAG, "There are " + Integer.toString(allObjs.size()) + " objects in database");

            for (TinyUrlDBEntry c : allObjs) {
                Log.d(TAG, " URL " + c.getUrl() + " Tiny " + c.getTinyUrl());
            }
        } else {
            Log.e(TAG, "Database instance is not created");
        }
        ShowUrlFragment urlFragment = new ShowUrlFragment();
        Bundle bundle = new Bundle();
        bundle.putString("origUrl", originalUrl);
        bundle.putString("shortUrl", result);
        urlFragment.setArguments(bundle);


        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, urlFragment)
                .addToBackStack("TinyUrlFragment")
                .commit();
    }


    @Override
    public void onTaskError(int id) {
        Log.d(TAG, "Shorten Url task was failed error " + getResources().getString(id));
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        Toast.makeText(this.getActivity(), getResources().getString(id),
                Toast.LENGTH_SHORT).show();
    }
}
