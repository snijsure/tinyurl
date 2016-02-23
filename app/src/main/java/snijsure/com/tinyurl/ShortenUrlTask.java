package snijsure.com.tinyurl;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by subodhnijsure on 2/8/16.
 * This class creates Async task to send http request
 * Upon success invokes onSuccess, onFailure interface methods.
 * It implements some methods that would allow us to set
 * shorter longer timeouts for testing in the future.
 */

// In real shipping product one would use somthing like
// retrofit, okhttp etc. 
public class ShortenUrlTask extends AsyncTask<String, String, String> {

    final static String TAG = "ShortenUrlTask";
    int mReadTimeout;
    int mConnectionTimeout;
    String mTinyUrl;
    OnTaskResult mListener;

    enum ResultCode {
        SUCCESS,
        PROTOCOL_ERROR,
        BAD_URL,
        IO_ERROR
    }
    String mUrlToShorten;
    ResultCode taskStatus;

    public ShortenUrlTask(String url, OnTaskResult listener) {
        mUrlToShorten = url;
        mListener = listener;
        mReadTimeout = 10000;
        mConnectionTimeout = 30000;
    }
    public void executeTask() {
        execute(mUrlToShorten);
    }
    //
    public void setReadTimeout(int value) {
        mReadTimeout = value;
    }

    public void setConnectionTimeout(int value) {
        mConnectionTimeout = value;
    }

    protected String doInBackground(String... args) {
        InputStream is = null;
        if (!args[0].isEmpty()) {
            try {
                String requestUrl = "http://tinyurl.com/api-create.php?url=" + args[0];
                URL url = new URL(requestUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(mReadTimeout);
                conn.setConnectTimeout(mConnectionTimeout);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.connect();
                is = conn.getInputStream();
                int response = conn.getResponseCode();
                if ( response == 200 ) {
                    taskStatus = ResultCode.SUCCESS;
                    return convertInputStreamToString(is);
                }
                else {
                    Log.d(TAG, "Http request failed result code " + Integer.toString(response));
                    taskStatus = ResultCode.IO_ERROR;
                    return "";
                }

            } catch (ProtocolException e) {
                taskStatus = ResultCode.PROTOCOL_ERROR;
            } catch (MalformedURLException e) {
                taskStatus = ResultCode.BAD_URL;
            } catch (IOException e) {
                taskStatus = ResultCode.IO_ERROR;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        taskStatus = ResultCode.IO_ERROR;
                    }
                }
            }
        } else {
            Log.d(TAG, "No input to ShortenUrlTask ");
            if (mListener != null) {
                mListener.onTaskError(R.string.invalid_url_error);
            }
        }
        return null;
    }

    protected void onPostExecute(String retString) {
        if (mListener != null) {
            switch (taskStatus) {
                case SUCCESS:
                    mListener.onTaskSuccess(retString);
                    break;
                case PROTOCOL_ERROR:
                    mListener.onTaskError(R.string.protocol_error_string);
                    break;
                case BAD_URL:
                    mListener.onTaskError(R.string.bad_url_error_string);
                    break;
                case IO_ERROR:
                    mListener.onTaskError(R.string.io_error_string);
                    break;
            }
        }
    }


    public String convertInputStreamToString(InputStream s) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedInputStream is = new BufferedInputStream(s);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            sb.append(inputLine);
        }
        return sb.toString();
    }
}
