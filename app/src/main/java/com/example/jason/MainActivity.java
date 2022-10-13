package com.example.jason;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.jason.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ArrayList<String> userList;
    ArrayAdapter<String> listAdapter;
    Handler mainHandler=new Handler();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializerUserlist();
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new fetch().start();

            }
        });
    }

    class fetch extends Thread{
        String data="";

        @Override
        public void run() {

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog=new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Fetching Data");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });
            try {
                URL url=new URL("http://aamras.com/dummy/EmployeeDetails.json");
                HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line=bufferedReader.readLine())!=null){
                    data+=line;
                }

                if (!data.isEmpty()){
                    JSONObject jsonObject=new JSONObject(data);
                    JSONArray employees= jsonObject.getJSONArray("employees");
                    userList.clear();
                    for (int i=0;i<employees.length();i++){
                        JSONObject names=employees.getJSONObject(i);
                        String name=names.getString("name");
                        userList.add(name);
                        JSONObject ages=employees.getJSONObject(i);
                        String age=ages.getString("age");
                        userList.add(age);
                        JSONObject salaries=employees.getJSONObject(i);
                        String salary=salaries.getString("salary");
                        userList.add(salary);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    listAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void initializerUserlist() {
        userList=new ArrayList<>();
        listAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,userList);
        binding.userList.setAdapter(listAdapter);
    }
}