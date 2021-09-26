package com.example.myweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private EditText user_field;//объявление соответствующих объектов
    private Button main_button;
    private TextView result_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//вызывается в момент создания Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);//прсваиваем объектам ссылки
        main_button = findViewById(R.id.main_button);
        result_info = findViewById(R.id.result_info);

        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//срабатывает каждый раз при нажатии на кнопку

                if(user_field.getText().toString().trim().equals("")){//получаем_текст.конвертируем_в_строку.обрезаем_пробелы.равно_ли_""_(пустой_строке)
                    /*
                    * Toast - класс объектов в java, с помощью которого можно, к примеру, вызывать всплывающие окна или указывать время в секундах
                    * makeText - всплывающее окно:
                    * 1 аргумент: активити(окно), в котром будет показано вплывающее окно;
                    * 2 аргумент: указание на текст в string.xml
                    * 3 аргумент: время показа окна(можно задать вручную в мс)
                    * .show() - метод, который показывет данное окно
                    * */
                    Toast.makeText(MainActivity.this, R.string.no_user_input_info,Toast.LENGTH_LONG).show();

                }
                else{
                    String city = user_field.getText().toString().replaceAll("\\s+","");//получаем текст о городе;replaceAll("\\s+","") - удаляет все пробелы и невидимые символы
                    String key = "60e66b700f2b165b191d0f3e6a135bb4";//ключ пользователя(с сайта о погоде)
                    String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+key+"&units=metric&lang=ru";//Адресс сервиса для получения данных о погоде

                    new GetURLData().execute(url);//создаем объект и вызываем метод execute
                }

            }
        });

    }
    private class GetURLData extends AsyncTask<String,String,String>{//во аремя работы приложения параллельно(благодаря классу AsyncTask) выполняется подключение к URL адесу и считываение
        protected void onPreExecute(){//метод срабатывает как только отправляем данные по URL
            super.onPreExecute();//обращаемся к родительскому классу
            result_info.setText("Ожидайте...");
        }

        @Override
        protected String doInBackground(String... strings) {//срабатывает во время получения данных
            HttpsURLConnection connection = null;//подключение
            BufferedReader reader = null;//

            try {//try-catch предлагается автоматически
                //объект ниже (URL url =...) необходим для обращения по url адресу
                URL url = new URL(strings[0]);//0-ой эллемент(первый элемент массива в параметрах) - то, что мы передали в метод в качестве параметра, т.е. URL адрес
                connection = (HttpsURLConnection) url.openConnection();//приводим к значение к классу HttpURLConnection; открываем соединение
                connection.connect();

                /*
                * InputStream позволяет считывать данные с URL адреса
                * */
                InputStream stream =connection.getInputStream();
                /* для считывания потока из stream в формате Строки,
                необходимо воспользоваться BufferedReader*/
                reader = new BufferedReader(new InputStreamReader(stream));

                /*переменная для хранения счтанной информации()
                * то же самое,что и String*/
                StringBuffer buffer  = new StringBuffer();
                String line ="";

                /*Цикл работает пока в reader есть строки(!=null)*/
                while((line = reader.readLine())!=null){
                    /*добавляем в buffer одну прочитаннуб строку
                    * после добавляем перевод на новую строку*/
                    buffer.append(line).append("\n");
                    return buffer.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {//данное исключение предлагается автоматически в стр. connection=...
                e.printStackTrace();
            } finally {//в данном блоке закрывем все соединения
                if(connection!=null)
                    connection.disconnect();
                if(reader!=null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result){//срабатывает когда мы уже получили все данные
            super.onPostExecute(result);
            result_info.setText(result);

            /*Поскольку мы работаем с JSON объектом(именно такой объект приходит с URL адреса)
            * нам необходимо его обработать */

            try {
                JSONObject jsonObject = new JSONObject(result);// создаем объект; try-catch автоматически
                /*обращаемся изначально к ключу main, затем к ключу tamp(Double)
                * ключи находятся в файле json на "сайте"*/
                result_info.setText("Температура: "+ jsonObject.getJSONObject("main").getDouble("temp"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}