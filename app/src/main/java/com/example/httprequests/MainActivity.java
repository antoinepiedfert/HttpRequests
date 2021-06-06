package com.example.httprequests;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public volatile String[] titles;
    public volatile String[] years;
    public volatile int index;
    public volatile StringBuffer sbTitles = new StringBuffer();

    private TextView mTextViewResult;
    private Button mButton;
    private RecyclerView mListView;
    private EditText mNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mTextViewResult = (TextView) findViewById(R.id.text_view_result);
        this.mButton = (Button) findViewById(R.id.button);
        this.mListView = (RecyclerView) findViewById(R.id.film_list_view);
        this.mNumber = (EditText) findViewById(R.id.editTextNumber);

        OkHttpClient client= new OkHttpClient();
        Constants csts = new Constants();
        String url = csts.url;

        titles = new String[csts.maxArray];
        years = new String[csts.maxArray];
        mTextViewResult.setText(Thread.currentThread().getName());

        MyAdapter adapter= new MyAdapter(this, titles, years);
        mListView.setAdapter(adapter);
        mListView.setLayoutManager(new LinearLayoutManager(this));

        Request request = new Request.Builder()
                .url(url)
                .build();

        Callback mCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    String myResponse = response.body().string();

                    try {

                        JSONArray filmArray = new JSONArray(myResponse);
                        for (int i = 0 ; i < 10 ; i++) {
                            JSONObject currentFilm = filmArray.getJSONObject(i);
                            titles[i] = currentFilm.getString("title");
                            years[i] = currentFilm.getString("release_date");
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                if (Integer.parseInt(mNumber.getText().toString()) > 0
                                        && Integer.parseInt(mNumber.getText().toString()) < csts.maxArray){
                                    index = Integer.parseInt(mNumber.getText().toString()) - 1;
                                    mTextViewResult.setText(titles[index] + ", " + years[index]);
                                }
                                else{
                                    mTextViewResult.setText("Wrong number");
                                }


                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                client.newCall(request).enqueue(mCallback);
            }
        });


    }
}