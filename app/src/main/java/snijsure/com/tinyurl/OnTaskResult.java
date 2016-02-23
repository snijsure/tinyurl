package snijsure.com.tinyurl;

/**
 * Created by subodhnijsure on 2/8/16.
 */
interface OnTaskResult {
    void onTaskSuccess(String result);
    void onTaskError(int id);
}
