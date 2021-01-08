package com.example.booksapi;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    EditText ed;
    TextView tv;
    String url;
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ed=findViewById(R.id.bookname);
        tv=findViewById(R.id.res);
        iv=findViewById(R.id.image);
    }

    public void search(View view) {
        String bookname=ed.getText().toString();
        url="https://www.googleapis.com/books/v1/volumes?q="+bookname;
        BookTask task=new BookTask();
        task.execute();

    }
    class BookTask extends AsyncTask<String,Void,String>{
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(MainActivity.this);
            pd.setMessage("please wait...");
            pd.setCancelable(false);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL u=new URL(url);
                HttpsURLConnection connection=(HttpsURLConnection)u.openConnection();
                InputStream is=connection.getInputStream();
                BufferedReader reader=new BufferedReader(new InputStreamReader(is));
                String line="";
                StringBuilder builder=new StringBuilder();
                while((line=reader.readLine())!=null){
                    builder.append(line);
                }
                return builder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject root=new JSONObject(s);
                JSONArray itemArray=root.getJSONArray("items");
                JSONObject indexObject=itemArray.getJSONObject(0);
                JSONObject volumeInfoObject=indexObject.getJSONObject("volumeInfo");
                String bookTitle=volumeInfoObject.getString("title"),authors=volumeInfoObject.getString("authors");
                JSONObject imagelinkobj=volumeInfoObject.getJSONObject("imageLinks");
                String imageurl=imagelinkobj.getString("thumbnail");
                Glide.with(MainActivity.this).load(imageurl).into(iv);
                tv.setText(bookTitle+"\n"+authors);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
