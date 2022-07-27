package com.example.covidtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.example.covidtracker.api.ApiUtilities;
import com.example.covidtracker.api.CountryData;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView totalConfirm, totalActive, totalRecovered,totalDeath, totalTests; //Переменные для общего количества случаев
    private TextView todayConfirm,todayRecovered, todayDeath, date; //Переменные для дневного количества случаем
    private PieChart pieChart; //Круг статистики

    private List<CountryData> list; //Лист с данными по стране

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<>();

        init();

        findViewById(R.id.cname).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this,CountryActivity.class)));

        ApiUtilities.getApiInterface().getCountryData().enqueue(new Callback<List<CountryData>>() {
                    @Override
                    public void onResponse(Call<List<CountryData>> call, Response<List<CountryData>> response) {
                        list.addAll(response.body());

                        for (int i = 0; i<list.size();i++)
                        {
                            if (list.get(i).getCountry().equals("Russia")) //Делаем запрос по ключу Россия
                            {
                                int confirm = Integer.parseInt(list.get(i).getCases());         //Записываем значения в локальные переменные
                                int active = Integer.parseInt(list.get(i).getActive());
                                int recovered = Integer.parseInt(list.get(i).getRecovered());
                                int death = Integer.parseInt(list.get(i).getDeaths());

                                totalConfirm.setText(NumberFormat.getInstance().format(confirm)); //Перезаписываем в переменные для вывода на экран. Тотальные данные
                                totalActive.setText(NumberFormat.getInstance().format(active));
                                totalRecovered.setText(NumberFormat.getInstance().format(recovered));
                                totalDeath.setText(NumberFormat.getInstance().format(death));

                                todayDeath.setText("+ " + NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayDeaths()))); //Перезаписываем в переменные для вывода на экран.
                                todayConfirm.setText("+ " + NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayCases()))); //Дневные данные
                                todayRecovered.setText("+ " + NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayRecovered())));
                                totalTests.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTests())));

                                setText(list.get(i).getUpdated());
                                //Настраиваем pieChart(Круг статистики), кастомизируем и присваиваем в круг поля
                                pieChart.addPieSlice(new PieModel("Confirm",confirm, getResources().getColor(R.color.yellow)));
                                pieChart.addPieSlice(new PieModel("Active",active, getResources().getColor(R.color.blue_pie)));
                                pieChart.addPieSlice(new PieModel("Recovered",recovered, getResources().getColor(R.color.green_pie)));
                                pieChart.addPieSlice(new PieModel("Death",death, getResources().getColor(R.color.red_pie)));

                                pieChart.startAnimation(); //Анимируем круг
                            }
                        }
                    }
                    //Переопределяем метод, который будет вызываться в случае неудачи
                    @Override
                    public void onFailure(Call<List<CountryData>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Метод, отвечающий за точный вывод даты в формате Месяц, день, год
    private void setText(String updated) {
        DateFormat format = new SimpleDateFormat("MMM dd, yyyy");

        long milliseconds = Long.parseLong(updated);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        date.setText("Актуально на " + format.format(calendar.getTime()));
    }
    //Инициализируем переменные исходя из id, которые мы указали в xml разметке
    private void init()
    {
        totalConfirm = findViewById(R.id.totalConfirm);
        totalActive = findViewById(R.id.totalActive);
        totalRecovered = findViewById(R.id.totalRecovered);
        totalDeath = findViewById(R.id.totalDeath);
        totalTests = findViewById(R.id.totalTests);
        todayConfirm = findViewById(R.id.todayConfirm);
        todayRecovered = findViewById(R.id.todayRecovered);
        todayDeath = findViewById(R.id.todayDeath);
        pieChart = findViewById(R.id.pieChart);
        date = findViewById(R.id.date);
    }
}