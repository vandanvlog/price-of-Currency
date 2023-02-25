package com.example.a2023;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText searchEdt;
    private RecyclerView currenciesRV;
    private ProgressBar loadingPB;
    private ArrayList<CurrencyRVModal> currencyRVModalsArrayList;
    private CurrencyRVAdapter currencyRVAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchEdt=findViewById(R.id.idEdtSearch);
        currenciesRV=findViewById(R.id.idCurrenciesRV);
        loadingPB=findViewById(R.id.idPBLoading);
        currencyRVModalsArrayList= new ArrayList<>();
        currencyRVAdapter = new CurrencyRVAdapter(currencyRVModalsArrayList,this);
        currenciesRV.setLayoutManager(new LinearLayoutManager(this));
        currenciesRV.setAdapter(currencyRVAdapter);
        getCurrencyData();

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                fillterCUrrencies(s.toString());

            }
        });


    }

    private void fillterCUrrencies(String currency){
        ArrayList<CurrencyRVModal> fillterList = new ArrayList<>();
        for (CurrencyRVModal item : currencyRVModalsArrayList ){
            if (item.getName().toLowerCase().contains(currency.toLowerCase())){
                fillterList.add(item);
            }
        }
        if (fillterList.isEmpty()){
            Toast.makeText(this, "No currency found that ", Toast.LENGTH_SHORT).show();
        }else {
            currencyRVAdapter.fillterList(fillterList);
        }
    }

    private void getCurrencyData(){

        loadingPB.setVisibility(View.VISIBLE);
        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                try {
                    JSONArray dataArray = response.getJSONArray("data");
                    for (int i=0 ; i<dataArray.length(); i++){
                        JSONObject dataObj =  dataArray.getJSONObject(i);
                        String name = dataObj.getString("name");
                        String symbol = dataObj.getString("symbol");
                        JSONObject quote = dataObj.getJSONObject("quote");
                        JSONObject USD = quote.getJSONObject("USD");
                        double price = USD.getDouble("price");
                        currencyRVModalsArrayList.add(new CurrencyRVModal(name,symbol,price));

                    }
                    currencyRVAdapter.notifyDataSetChanged();

                }catch (JSONException e ){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Fail to extract data ", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Fial to get the data ", Toast.LENGTH_SHORT).show();

            }

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> heaers  =    new HashMap<>();
                heaers.put("X-CMC_PRO_API_KEY","e54916ca-6b3a-40d4-8a26-655ace4ca2e8");
                return heaers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }



}