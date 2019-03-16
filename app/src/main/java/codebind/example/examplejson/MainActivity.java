package codebind.example.examplejson;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class MainActivity extends AppCompatActivity
{
    String GET_URL = "https://api.github.com/users/hadley/orgs";
    AutoCompleteTextView listView;
    ProgressBar progress;
    List<String> list;
    Button fetch;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        fetch = (Button) findViewById(R.id.button);
        fetch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isOnline())
                {
                    progress.setVisibility(View.VISIBLE);
                    new FetchJSON().execute();
                }
                else
                {

                    Toast.makeText(getApplicationContext(), "No Connection!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    private boolean isOnline()
    {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @SuppressLint("StaticFieldLeak")
    private class FetchJSON extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... voids)
        {
            try
            {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(GET_URL)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return "";
            }
        }
        @Override
        protected void onPostExecute(String res)
        {
            if (res.isEmpty())
            {
                Toast.makeText(getApplicationContext(), "No Connection!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                try {
                    JSONArray array = new JSONArray(res);
                    list = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject ob = array.getJSONObject(i);
                        String item = "Login Name : " + ob.getString("login") + "\n" +
                                "ID : " + ob.getInt("id") + "\n" +
                                "URL : " + ob.getString("url");
                        list.add(item);
                        Log.e("sundar",""+item);
                    }
                    adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_item, list);
                    listView.setThreshold(1);
                    listView.setAdapter(adapter);
                    listView.setVisibility(View.VISIBLE);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            progress.setVisibility(View.GONE);
        }
    }
}