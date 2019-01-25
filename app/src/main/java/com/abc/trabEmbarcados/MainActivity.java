package com.abc.trabEmbarcados;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/*
* Infos dos alimentos obtidos em https://github.com/raulfdm/taco-api
* */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Debug";

    ArrayList<Registro> alimentos = new ArrayList<Registro>();
    FirebaseHandler fbh = new FirebaseHandler();
    Calendar cal = Calendar.getInstance();
    String fileName = "meusArquivosTemp";
    TextView result;
    Context context;
    private Boolean specialist = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        while (!this.specialist) {
            fbh.UploadLogs();
        }
    }

    public void PatientScreen(View view){
        this.specialist = false;
        alimentos = getSavedArrayList();
        fbh.UploadLogs();

        setContentView(R.layout.activity_main_patient);
        result = (TextView) findViewById(R.id.textView2);
    }

    public void SpecialistScreen(View view){
        this.specialist = true;
        fbh.DownloadLogs();
        alimentos = getSavedArrayList();

        setContentView(R.layout.activity_main_specialist);
        result = (TextView) findViewById(R.id.textView2);

        Toast.makeText(this, "Updating..", Toast.LENGTH_SHORT).show();
    }

    public void update(View view) {
        fbh.DownloadLogs();
        Toast.makeText(this, "Updating..", Toast.LENGTH_SHORT).show();
    }

    public void upload(View view) {
        fbh.UploadLogs();
        Toast.makeText(this, "Updating..", Toast.LENGTH_SHORT).show();
    }

    public void getSpeechInput(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale o nome do alimento");

        try{
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException e){
            Toast.makeText(this, "Seu dispositivo não aceita reconhecimento de voz", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 100:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> resultado = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    getJson(resultado.get(0));
                }
                break;
        }
    }

    public void showLogs(View view){
        Intent startNewActivity = new Intent(this, LogActivity.class);
        startNewActivity.putExtra("alimentos", getSavedArrayList());
        startActivity(startNewActivity);
    }

    public void showGraphs(View view){
        Intent startNewActivity = new Intent(this, GraphActivity.class);
        startNewActivity.putExtra("alimentos", getSavedArrayList());
        startActivity(startNewActivity);
    }

    public void getJson(String nomeAlimento){
        String json = null;
        boolean j = false;
        try {
            InputStream is = getAssets().open("alimentos.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);

                if(obj.getString("descricacao").contains(nomeAlimento) && !(obj.getString("carboidrato").contains("*"))) {
                    alimentos.add(new Registro(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(cal.getTime()), nomeAlimento, 1, obj.getString("carboidrato")));
                    saveArrayList(alimentos);
                    fbh.UploadLogs();
                    j = true;

                    break;
                }
            }
            if(j){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Alimento "+nomeAlimento+" foi adicionado com sucesso!")
                        .setTitle(R.string.dialog_title);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Alimento "+nomeAlimento+" não encontrado na base de dados!")
                        .setTitle("Erro");
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Registro> getSavedArrayList() {
        ArrayList<Registro> savedArrayList = null;
        try {
            FileInputStream inputStream = openFileInput(fileName);
            ObjectInputStream in = new ObjectInputStream(inputStream);
            savedArrayList = (ArrayList<Registro>) in.readObject();
            in.close();
            inputStream.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return savedArrayList;
    }

    public void saveArrayList(ArrayList<Registro> arrayList) {
        try {
            FileOutputStream fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(arrayList);
            out.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset(View view){
        alimentos = new ArrayList<Registro>();
        alimentos.add(new Registro("06/01/2018 09:40:40", "queijo", 1, "10"));
        alimentos.add(new Registro("06/02/2018 11:30:20", "queijo", 2, "40"));
        alimentos.add(new Registro("06/03/2018 14:08:09", "laranja", 1, "40"));
        alimentos.add(new Registro("06/04/2018 11:30:20", "morango", 3, "90"));
        alimentos.add(new Registro("06/05/2018 16:34:11", "torta", 1, "150"));
        alimentos.add(new Registro("06/06/2018 20:50:10", "peixe", 2, "230"));
        alimentos.add(new Registro("06/07/2018 09:40:40", "batata", 4, "70"));
        alimentos.add(new Registro("06/08/2018 23:55:00", "pizza", 1, "200"));
        alimentos.add(new Registro("06/09/2018 11:30:20", "queijo", 6, "40"));
        alimentos.add(new Registro("06/10/2018 14:08:09", "laranja", 3, "40"));
        alimentos.add(new Registro("06/11/2018 20:50:10", "arroz", 2, "100"));
        alimentos.add(new Registro("06/12/2018 14:08:09", "biscoito", 2, "150"));
        alimentos.add(new Registro("06/13/2018 11:30:20", "maçã", 5, "50"));
        alimentos.add(new Registro("06/14/2018 09:40:40", "feijão", 1, "80"));
        alimentos.add(new Registro("06/15/2018 23:55:00", "batata", 2, "70"));
        alimentos.add(new Registro("06/16/2018 20:50:10", "pizza", 1, "200"));
        alimentos.add(new Registro("06/17/2018 11:30:20", "queijo", 4, "40"));
        alimentos.add(new Registro("06/18/2018 14:08:09", "laranja", 3, "40"));
        alimentos.add(new Registro("06/19/2018 11:30:20", "morango", 1, "90"));
        alimentos.add(new Registro("06/20/2018 16:34:11", "torta", 1, "150"));
        alimentos.add(new Registro("06/21/2018 20:50:10", "peixe", 2, "230"));
        alimentos.add(new Registro("06/22/2018 09:40:40", "arroz", 3, "100"));
        alimentos.add(new Registro("06/23/2018 20:50:10", "biscoito", 1, "150"));
        alimentos.add(new Registro("06/24/2018 23:55:00", "maçã", 5, "50"));
        alimentos.add(new Registro("06/25/2018 14:08:09", "feijão", 2, "80"));
        alimentos.add(new Registro("06/26/2018 11:30:20", "batata", 4, "70"));
        alimentos.add(new Registro("06/27/2018 14:08:09", "pizza", 3, "200"));
        alimentos.add(new Registro("06/28/2018 11:30:20", "queijo", 2, "200"));
        alimentos.add(new Registro("06/29/2018 20:50:10", "queijo", 10, "10"));
        alimentos.add(new Registro("06/30/2018 23:55:00", "queijo", 6, "40"));
        alimentos.add(new Registro("07/01/2018 09:40:40", "laranja", 4, "40"));
        alimentos.add(new Registro("07/02/2018 08:30:20", "morango", 2, "15"));
        alimentos.add(new Registro("07/02/2018 10:05:20", "maçã", 1, "45"));
        alimentos.add(new Registro("07/02/2018 12:00:11", "torta", 2, "150"));
        alimentos.add(new Registro("07/02/2018 15:34:11", "bolo", 1, "120"));
        alimentos.add(new Registro("07/02/2018 19:34:11", "pastel", 2, "110"));
        alimentos.add(new Registro("07/02/2018 22:50:10", "peixe", 1, "230"));
        saveArrayList(alimentos);
        Toast.makeText(this, "Reseting", Toast.LENGTH_SHORT).show();
    }

    public void empty_reset(View view){
        alimentos = new ArrayList<Registro>();
        saveArrayList(alimentos);
        Toast.makeText(this, "Reseting", Toast.LENGTH_SHORT).show();
    }
}
