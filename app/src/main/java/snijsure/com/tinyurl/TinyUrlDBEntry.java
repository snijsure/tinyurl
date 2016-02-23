package snijsure.com.tinyurl;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by subodhnijsure on 2/9/16.
 */
public class TinyUrlDBEntry extends RealmObject {

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTinyUrl() {
        return tinyUrl;
    }

    public void setTinyUrl(String tinyUrl) {
        this.tinyUrl = tinyUrl;
    }

    @PrimaryKey
    private String url;

    private String tinyUrl;


}
