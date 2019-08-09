package com.example.bikeaid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import com.example.bikeaid.Adapter.EmergencyProblemListAdapter;
import com.example.bikeaid.Model.ProblemModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EmergencyBreakDown extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EmergencyProblemListAdapter emergencyProblemListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBar();
        setContentView(R.layout.activity_emergency_break_down);
        initRecycler();
        createList();
    }

    private void hideTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
    }

    private void initRecycler() {
        recyclerView = findViewById(R.id.problemList);
        emergencyProblemListAdapter = new EmergencyProblemListAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(emergencyProblemListAdapter);
    }

    private void createList() {
        Gson gson = new Gson();
        Type type = new TypeToken<List<ProblemModel>>() {
        }.getType();
        List<ProblemModel> problemModels = gson.fromJson(loadJSONFromRaw(), type);
        emergencyProblemListAdapter.setList(problemModels);
    }

    public String loadJSONFromRaw() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.emergency_problem);

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
