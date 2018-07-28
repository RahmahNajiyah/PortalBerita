package com.rahmahnajiyahimtihan.portalberita;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.StringTokenizer;

public class DetailActivity extends AppCompatActivity {
    //inisial component
    TextView txtJudul;
    ImageView img;
    WebView web;
    AQuery aq;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //get data
        Intent a = getIntent();
        id = a.getStringExtra("id");
        getDetailBerita();
    }

    private void getDetailBerita() {
       String url = "http://192.168.100.4/portalberita/detailberita.php";
        HashMap<String, String> params = new HashMap<>();
        params.put("id_berita", id);
        ProgressDialog pdialog = new ProgressDialog(DetailActivity.this);
        pdialog.setMessage("Loading.....");
        aq = new AQuery(DetailActivity.this);
        aq.progress(pdialog).ajax(url, params, String.class, new AjaxCallback<String>(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                //jika datanya ada
                if (object !=null) {
                    Log.d("Respon Detail:", object);
                    try {
                        JSONObject json = new JSONObject(object);
                        String result = json.getString("pesan");
                        String sukses = json .getString("sukses");
                        if (sukses.equalsIgnoreCase("true")) {
                            JSONArray jsonArray = json.getJSONArray("berita");
                            JSONObject jsonOb = jsonArray.getJSONObject(0);
                            String judul = jsonOb.getString("judul");
                            String gambar = jsonOb.getString("gambar");
                            String isi = jsonOb.getString("deskripsi");

                            ActionGet(judul,gambar,isi);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void ActionGet(String judul, String gambar, String isi) {
        txtJudul = (TextView) findViewById(R.id.txtdetailjudul);
        img = (ImageView) findViewById(R.id.imgdetail);
        web = (WebView) findViewById(R.id.webView);

        txtJudul.setText(judul);
        String url_foto = "http://192.168.100.4/portalberita/foto_berita/";
        Picasso.with((DetailActivity.this)).load(url_foto + gambar).error(R.drawable.noimage);
        String htmltext = "<html><body style=\"text-align:justify\">" + "%s </body></html>";
        web.loadData(String.format(htmltext, isi),"text/html","uft-8");
    }
}
