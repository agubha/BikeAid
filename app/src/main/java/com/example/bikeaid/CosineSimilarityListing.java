package com.example.bikeaid;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.bikeaid.Adapter.CosineRecyclerAdapter;
import com.example.bikeaid.CosineSimilarityAlgorithm.CosineVectorSimilarity;
import com.example.bikeaid.CosineSimilarityAlgorithm.PorterStemmer;
import com.example.bikeaid.CosineSimilarityAlgorithm.StoppedWord;
import com.example.bikeaid.Model.Locations;
import com.example.bikeaid.Model.WorkShopLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CosineSimilarityListing extends AppCompatActivity {
    List<Locations> locationsList;
    private String problemString;
    List<Locations> filteredList;
    List<Double> filteredCosineValueList;
    RecyclerView recyclerView;
    CosineRecyclerAdapter cosineRecyclerAdaper;
    boolean isService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cosine_similarity_listing);
        filteredList = new ArrayList<>();
        locationsList = createList();
        filteredCosineValueList = new ArrayList<>();
        getIntents();
        String[] split_string = split(problemString);
        loadIntentProblem async = new loadIntentProblem("Intent Problem");
        async.execute(split_string);
        recyclerView = findViewById(R.id.cosineValueList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        cosineRecyclerAdaper = new CosineRecyclerAdapter(this);
        recyclerView.setAdapter(cosineRecyclerAdaper);
    }

    @SuppressLint("StaticFieldLeak")
    public class loadIntentProblem extends AsyncTask<String[], Void, ArrayList<String>> {
        String name;

        public loadIntentProblem(String name) {
            this.name = name;
        }

        @Override
        protected ArrayList<String> doInBackground(String[]... strings) {
            String[] wordtostop = new String[100];//capped to 500 words
            String[] wordtostem = strings[0];
            ArrayList<String> valuetoreturn;
            for (int i = 0; i < wordtostem.length; i++) {
                PorterStemmer porterStemmer2 = new PorterStemmer();
                wordtostop[i] = porterStemmer2.stemWord(wordtostem[i]);
            }

            ArrayList<String> stemmed_list_before_stopword = new ArrayList<>(Arrays.asList(wordtostop));
            StoppedWord stoppedword3 = new StoppedWord();
            valuetoreturn = stoppedword3.main(stemmed_list_before_stopword);
            return valuetoreturn;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            if (strings == null) {
                Log.d("onPostExecute:", "NULL");
            } else if (strings.size() == 0) {
                Log.d("onPostExecute", "Size Zero:" + strings.toString());
            } else {
                Log.d("onPostExecute", "Size ZELSELSADASD");
                strings.removeAll(Arrays.asList(null, ""));
                String[] mStringArray = new String[strings.size()];
                mStringArray = strings.toArray(mStringArray);
                for (String s : mStringArray) {
                    Log.d("string is", s);
                }
                for (int i = 0; i < locationsList.size(); i++) {
                    for (int k = 0; k < locationsList.get(i).getTag().size(); k++) {
                        String[] split_string = split(locationsList.get(i).getTag().get(k));
                        if (CosineVectorSimilarity.consineTextSimilarity(mStringArray, split_string) > 0.0) {
                            filteredList.add(locationsList.get(i));
                            filteredCosineValueList.add(CosineVectorSimilarity.consineTextSimilarity(mStringArray, split_string));
                        }
                    }
                }
                cosineRecyclerAdaper.setList(filteredList);
                cosineRecyclerAdaper.setCosineValues(filteredCosineValueList);
                cosineRecyclerAdaper.setBooleanValue(isService);

            }
        }
    }

    private void getIntents() {
        if (getIntent().hasExtra("problem")) {
            problemString = getIntent().getStringExtra("problem");
        }
        if (getIntent().hasExtra("isService")) {
            isService = getIntent().getBooleanExtra("isService", false);
        }
    }

    private String[] split(String book_detail) {
        return book_detail.split("\\s+");
    }


    private List<Locations> createList() {
        Gson gson = new Gson();
        Type type = new TypeToken<WorkShopLocation>() {
        }.getType();
        WorkShopLocation workShopLocations = gson.fromJson(loadJSONFromRaw(), type);
        return workShopLocations.getLocation();
    }

    public String loadJSONFromRaw() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.workshop_location);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}
