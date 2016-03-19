package snijsure.com.tinyurl;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by subodhnijsure on 3/12/16.
 */
public interface ShortenUrlInterface {
    @GET("/api-create.php")
    Call<ResponseBody> doIt(@Query("url") String url);
}
