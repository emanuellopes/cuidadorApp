package pt.app.cuidador.cuidador.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pt.app.cuidador.cuidador.Global;
import pt.app.cuidador.cuidador.Manager;
import pt.app.cuidador.cuidador.Objects.AdapterUtente;
import pt.app.cuidador.cuidador.Objects.EstadoProcedimento;
import pt.app.cuidador.cuidador.Objects.Material;
import pt.app.cuidador.cuidador.Objects.Procedimento;
import pt.app.cuidador.cuidador.Objects.Utente;
import pt.app.cuidador.cuidador.R;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText txtUsername;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUsername = (EditText) findViewById(R.id.editTextUsername);
        txtPassword = (EditText) findViewById(R.id.editTextPassword);
        //testeInfo();
        loginButton = (Button) findViewById(R.id.buttonLogin);

        if (Manager.INSTANCE.getLogin(this) != "") {
            startActivity(new Intent(MainActivity.this, CuidadorActivity.class));
            new UpdateMaterial().execute(Global.listarMateriais, Manager.INSTANCE.getUsername(),
                    Manager.INSTANCE.getPassword());
        }
    }

    private void testeInfo() {
        Manager.INSTANCE.addMaterial(new Material(1,"tipo", "descricao", "google.pt"));
        Manager.INSTANCE.addMaterial(new Material(2, "tipo cadeira", "descricao bacana", "google.pt"));
        Manager.INSTANCE.addMaterial(new Material(3, "tipo material ouvido", "descricao top", "google.pt"));

        Manager.INSTANCE.addUtente(new Utente(10, "Ze manel", "email@email.com", "Morada muito linda, esta é top capa de revista", "91858584"));
        Manager.INSTANCE.addUtente(new Utente(10, "esquim manel", "email@email.com", "Morada muito linda, esta é top capa de revista", "91858584"));
        Manager.INSTANCE.addUtente(new Utente(10, "gracinda manel", "email@email.com", "Morada muito linda, esta é top capa de revista", "91858584"));
        Manager.INSTANCE.addUtente(new Utente(10, "joaquina manel", "email@email.com", "Morada muito linda, esta é top capa de revista", "91858584"));
        Manager.INSTANCE.addUtente(new Utente(10, "Ermilinda manel", "email@email.com", "Morada muito linda, esta é top capa de revista", "91858584"));


        //Manager.INSTANCE.getListaUtentes().get(0).addProcedimento(new Procedimento("identificador", "descricao", "tipo", EstadoProcedimento.A_iniciar));
        //Manager.INSTANCE.getListaUtentes().get(0).addProcedimento(new Procedimento("identificador", "descricao", "tipo cadeira", EstadoProcedimento.Concluido));

    }

    public void loginView(View view) {
        if (Global.checkNetworkConnection(MainActivity.this)) {
            if(txtUsername.getText().length()<1 || txtPassword.getText().length()<1){
                Toast.makeText(getApplicationContext(), "Insira Username ou password!", Toast.LENGTH_LONG).show();
                return;
            }
            new LoginAsync().execute(Global.login, txtUsername.getText().toString(), txtPassword.getText().toString());
        } else {
            Toast.makeText(getApplicationContext(), "No network available!", Toast.LENGTH_SHORT).show();
        }
    }

    private class LoginAsync extends AsyncTask<String, Void, String> {
        InputStream is = null;

        @Override
        protected void onPostExecute(String result) {

            if(result!=null){
                startActivity(new Intent(MainActivity.this, CuidadorActivity.class));
                Manager.INSTANCE.saveLogin(txtUsername.getText().toString(),
                        txtPassword.getText().toString(), getApplicationContext());
                new UpdateMaterial().execute(Global.listarMateriais, Manager.INSTANCE.getUsername(), Manager.INSTANCE.getPassword());
            } else {
                Toast.makeText(getApplicationContext(), "Erro no login!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                Log.e("URL", url.toString());
               String authStr = params[1] + ":" + params[2];
                Log.e("auth", authStr);
                String authEncoded = Base64.encodeToString(authStr.getBytes(),0);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");


                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:50.0) Gecko/20100101 Firefox/50.0");
                urlConnection.setConnectTimeout(30000);
                urlConnection.setReadTimeout(30000);

                urlConnection.addRequestProperty("Authorization", "Basic " + authEncoded);
                //urlConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                urlConnection.connect();

                is = urlConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));



               String line;
                final StringBuilder sb = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                return sb.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private class UpdateMaterial extends AsyncTask<String, Void, String> {
        InputStream is = null;

        @Override
        protected void onPostExecute(String result) {

            if(result!=null){
                Log.e("Material", "aa"+result);
                parseXMlMateriais(result);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                Log.e("URL", url.toString());
                String authStr = params[1] + ":" + params[2];
                Log.e("auth", authStr);
                String authEncoded = Base64.encodeToString(authStr.getBytes(),0);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");


                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:50.0) Gecko/20100101 Firefox/50.0");
                urlConnection.setConnectTimeout(30000);
                urlConnection.setReadTimeout(30000);

                urlConnection.addRequestProperty("Authorization", "Basic " + authEncoded);
                //urlConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                urlConnection.connect();

                is = urlConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));



                String line;
                final StringBuilder sb = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                return sb.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void parseXMlMateriais(String xml){
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            Material currentMaterial = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        Manager.INSTANCE.setListaMaterial();
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("Material")) {
                            currentMaterial = new Material();
                        } else if (currentMaterial != null) {
                            if (name.equals("id")) {
                                currentMaterial.setId(Long.parseLong(parser.nextText()));
                            } else if (name.equals("tipo")) {
                                currentMaterial.setTipo(parser.nextText());
                            } else if (name.equals("link")) {
                                currentMaterial.setLink(parser.nextText());
                            } else if (name.equals("descricao")) {
                                currentMaterial.setDescricao(parser.nextText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("Material") && currentMaterial != null) {
                            Manager.INSTANCE.addMaterial(currentMaterial);
                        }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
