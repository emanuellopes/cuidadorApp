package pt.app.cuidador.cuidador.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import pt.app.cuidador.cuidador.Objects.Utente;
import pt.app.cuidador.cuidador.R;

public class CuidadorActivity extends AppCompatActivity {

    private AdapterUtente adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuidador);

        setTitle("Cuidador - "+ Manager.INSTANCE.getUsername());

        listView = (ListView) findViewById(R.id.listViewUtentes);

        updateFromInternet();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), ProcedimentoActivity.class);
                intent.putExtra(Global.UTENTE, position);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        final MenuItem action = menu.findItem(R.id.logout);
        action.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Manager.INSTANCE.deleteLogin(CuidadorActivity.this);
                finish();
                return true;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private void updateFromInternet() {
        if (Global.checkNetworkConnection(CuidadorActivity.this)) {
            new CuidadorActivity.getUtentes().execute(Global.listarUtentes, Manager.INSTANCE.getUsername(), Manager.INSTANCE.getPassword());
        } else {
            Toast.makeText(getApplicationContext(), "No network available!", Toast.LENGTH_SHORT).show();
        }
    }

    private void readXml(String xml) {
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            Utente currentUtente = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        Manager.INSTANCE.setListaUtentes();
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("Utente")) {
                            currentUtente = new Utente();
                        } else if (currentUtente != null) {
                            if (name.equals("contacto")) {
                                currentUtente.setContacto(parser.nextText());
                            } else if (name.equals("email")) {
                                currentUtente.setEmail(parser.nextText());
                            } else if (name.equals("morada")) {
                                currentUtente.setMorada(parser.nextText());
                            } else if (name.equals("nome")) {
                                currentUtente.setNome(parser.nextText());
                            } else if (name.equals("id")) {
                                currentUtente.setId(Long.parseLong(parser.nextText()));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("Utente") && currentUtente != null) {
                            Manager.INSTANCE.addUtente(currentUtente);
                        }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Manager.INSTANCE.getListaUtentes().size() < 1) {
            Toast.makeText(getApplicationContext(), "Sem utentes!", Toast.LENGTH_SHORT).show();
        } else {
            adapter = new AdapterUtente(Manager.INSTANCE.getListaUtentes(), getApplicationContext());
            listView.setAdapter(adapter);
        }
    }

    private class getUtentes extends AsyncTask<String, Void, String> {
        InputStream is = null;

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                readXml(result);
            } else {
                Toast.makeText(getApplicationContext(), "Erro ", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                String authStr = params[1] + ":" + params[2];
                String authEncoded = Base64.encodeToString(authStr.getBytes(), 0);

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
}
