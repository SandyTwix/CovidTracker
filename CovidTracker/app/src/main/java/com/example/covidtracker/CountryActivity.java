package com.example.covidtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;
import com.example.covidtracker.api.ApiUtilities;
import com.example.covidtracker.api.CountryData;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountryActivity extends AppCompatActivity {

    private RecyclerView recyclerView; //Компонент для отображения элементов списка
    private List<CountryData> list; //Лист с данными стран
    private ProgressDialog dialog; //Диалоговое окно для пользователя

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);

        recyclerView = findViewById(R.id.countries);
        list = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        countryAdapter adapter = new countryAdapter(this, list); //создаем экземплят класса countryAdapter
        recyclerView.setAdapter(adapter);

        //Показываем пользователю в диалоговом окне сообщение с загрузкой
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.show();

        ApiUtilities.getApiInterface().getCountryData().enqueue(new Callback<List<CountryData>>() {
            @Override
            public void onResponse(Call<List<CountryData>> call, Response<List<CountryData>> response) {
                list.addAll(response.body());

                adapter.notifyDataSetChanged();

                dialog.dismiss();
            }

            //В случае ошибки
            @Override
            public void onFailure(Call<List<CountryData>> call, Throwable t) {
                dialog.dismiss();

                Toast.makeText(CountryActivity.this, "Error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}