package com.rahmahnajiyahimtihan.portalberita;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    AQuery aq;
    ProgressDialog progressDialog;
    ArrayList<HashMap<String,String>> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new ArrayList<HashMap<String, String>>();
        AmbilData();
        listView = (ListView) findViewById(R.id.listView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void AmbilData() {
        String url = "http://192.168.100.4/portalberita/berita.php";

        aq = new AQuery(MainActivity.this);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading (by Rahmah najiyah imtihan)");
        aq.progress(progressDialog).ajax(url, String.class, new AjaxCallback<String>(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                if (object != null) {
                    Log.d("Respon", object);
                    try {
                        JSONObject json = new JSONObject(object);
                        String result = (String) json.getString("pesan"); //klo string jd get string-call isi string dr php
                        String sukses = (String) json.getString("sukses");

                        //jika datanya ada
                        if (sukses.equalsIgnoreCase("true")) {  //true jg dr php
                            JSONArray jsonArray = json.getJSONArray("berita");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String id_berita = jsonObject.getString("id");
                                String judul_berita = jsonObject.getString("judul");
                                String gambar = jsonObject.getString("gambar");

                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("id", id_berita);
                                map.put("judul", judul_berita);
                                map.put("gambar", gambar);
                                data.add(map);

                                setListAdapter(data);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setListAdapter(final ArrayList<HashMap<String, String>> data) {
        CustomAdapter adapter = new CustomAdapter(this, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 HashMap<String,String> map = data.get(i);
                Intent a = new Intent(getApplicationContext(), DetailActivity.class);
                a.putExtra("id", map.get("id"));
                startActivity(a);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CustomAdapter extends BaseAdapter {
        Activity activity;
        ArrayList<HashMap<String,String>> data2;
        LayoutInflater layoutinflater;

        public CustomAdapter(Activity activity, ArrayList<HashMap<String, String>> data2) {
            this.activity = activity;
            this.data2 = data; //dont forget to change this to data
        }

        @Override
        public int getCount() {
            return data2.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            layoutinflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = layoutinflater.inflate(R.layout.listitem, null);
            TextView id = (TextView) v.findViewById(R.id.idberita);
            TextView jdl = (TextView) v.findViewById(R.id.textjudul);
            CircleImageView img = (CircleImageView) v.findViewById(R.id.listimg);

            HashMap<String, String> data = new HashMap<>();
            data = data2.get(i);

            jdl.setText(data.get("judul"));
            id.setText(data.get("id"));
            String url_foto = "http://192.168.100.4/portalberita/foto_berita/";
            Picasso.with(MainActivity.this).load(url_foto+data.get("gambar")).error(R.drawable.noimage).into(img);

            return v;
        }
    }
}
