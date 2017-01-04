package pt.app.cuidador.cuidador.Activities;

import android.app.Dialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.scalified.fab.ActionButton;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;

import pt.app.cuidador.cuidador.Global;
import pt.app.cuidador.cuidador.Manager;
import pt.app.cuidador.cuidador.Objects.AdapterProdecimento;
import pt.app.cuidador.cuidador.Objects.AdapterUtente;
import pt.app.cuidador.cuidador.Objects.EstadoProcedimento;
import pt.app.cuidador.cuidador.Objects.Material;
import pt.app.cuidador.cuidador.Objects.Procedimento;
import pt.app.cuidador.cuidador.Objects.Utente;
import pt.app.cuidador.cuidador.R;

public class ProcedimentoActivity extends AppCompatActivity {

    private AdapterProdecimento adapter;
    private ListView listView;
    private int position;
    private LinkedList<Procedimento> procedimentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procedimento);

        ActionButton actionButton = (ActionButton) findViewById(R.id.action_button);
        actionButton.setImageResource(R.drawable.fab_plus_icon);
        actionButton.setButtonColor(R.color.fab_material_red_500);
        actionButton.setButtonColorPressed(R.color.fab_material_red_900);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddProcedimento(null, 0,false);
            }
        });

        position = getIntent().getIntExtra(Global.UTENTE, -1);

        listView = (ListView) findViewById(R.id.listViewProcedimentos);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Procedimento procedimento = procedimentos.get(i);
                AddProcedimento(procedimento, i,true);
            }
        });
        updateFromInternet();

        EstadoProcedimento estado = EstadoProcedimento.A_iniciar;

        Log.e("estado bacano", estado.name());
    }

    private void updateFromInternet() {
        if (Global.checkNetworkConnection(ProcedimentoActivity.this)) {
            Utente utente = Manager.INSTANCE.getListaUtentes().get(position);
            new getProcedimentos().execute(Global.listarProcedimentosUtente,
                    Long.toString(utente.getId()), Manager.INSTANCE.getUsername(), Manager.INSTANCE.getPassword());
        } else {
            Toast.makeText(getApplicationContext(), "No network available!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateList(){
        procedimentos = Manager.INSTANCE.getListaUtentes().get(position).getProcedimentos();
        adapter = new AdapterProdecimento(procedimentos, getApplicationContext());
        listView.setAdapter(adapter);
    }



    private void AddProcedimento(Procedimento proceEdit, final int posEdit, final boolean edit){
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Holo_Light_NoActionBar);

        View view = getLayoutInflater().inflate(R.layout.dialog_add_procedimento, null);
        final EditText identificador = (EditText) view.findViewById(R.id.editTextIdentificador);
        if(edit){
            identificador.setEnabled(false);
        }
        final EditText descricao = (EditText) view.findViewById(R.id.editTextDescricao);


        final Spinner spinnerMaterial = (Spinner) view.findViewById(R.id.spinnerMaterial);
        final Spinner spinnerEstado = (Spinner) view.findViewById(R.id.spinnerEstado);

        final ArrayAdapter<Material> adapterMaterial = new ArrayAdapter<>(ProcedimentoActivity.this,
                R.layout.laoyout_spinner, Manager.INSTANCE.getListaMaterial());

        spinnerMaterial.setAdapter(adapterMaterial);

        final ArrayAdapter<EstadoProcedimento> adapterEstado = new ArrayAdapter<>(ProcedimentoActivity.this,
                R.layout.laoyout_spinner, EstadoProcedimento.values());

        spinnerEstado.setAdapter(adapterEstado);

        if(edit){
            identificador.setText(proceEdit.getIdentificador());
            descricao.setText(proceEdit.getDescricao());
            int i=0;
            for (Material material:Manager.INSTANCE.getListaMaterial()){
                if(material.getTipo().equals(proceEdit.getMaterial())){
                    spinnerMaterial.setSelection(i);
                }
                i++;
            }
            for(i=0 ; i<adapterEstado.getCount() ; i++){
                EstadoProcedimento estado = adapterEstado.getItem(i);
                if(estado==proceEdit.getEstado()){
                    spinnerEstado.setSelection(i);
                }
            }
       }
        Button buttonOK = (Button) view.findViewById(R.id.buttonDone);
        Button buttonCancel = (Button) view.findViewById(R.id.buttonCancel);


        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO button ok
                if(identificador.getText().length()<1 || descricao.getText().length()<1){
                    Toast.makeText(ProcedimentoActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    return;
                }
                Material materialSelected = (Material)spinnerMaterial.getSelectedItem();
                EstadoProcedimento newEstado = (EstadoProcedimento) spinnerEstado.getSelectedItem();
                Log.e("Material id=", newEstado.toString());

                Procedimento procedimento = new Procedimento(Manager.INSTANCE.getListaUtentes().get(position).getId(),
                        identificador.getText().toString(),
                        descricao.getText().toString(), materialSelected, newEstado, "data hoje");

                if(!edit) {
                    Manager.INSTANCE.getListaUtentes().get(position).addProcedimento(procedimento);
                    new createProcedimento().execute(procedimento);
                }else{
                    Manager.INSTANCE.getListaUtentes().get(position).setProcedimento(posEdit, procedimento);
                    new updateProcedimento().execute(procedimento);
                }

                updateList();
                dialog.dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private class getProcedimentos extends AsyncTask<String, Void, String> {
        InputStream is = null;

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                readXml(result);
                updateList();
            } else {
                Toast.makeText(getApplicationContext(), "Erro ", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]+params[1]);
                String authStr = params[2] + ":" + params[3];
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

    private void readXml(String xml) {
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            Procedimento currentProcedimento = null;
            Material newMaterial = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        Manager.INSTANCE.getListaUtentes().get(position).setProcedimentos();
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("Procedimento")) {
                            currentProcedimento = new Procedimento();
                            currentProcedimento.setIdUtente(Manager.INSTANCE.getListaUtentes().get(position).getId());
                            newMaterial = new Material();
                        } else if (currentProcedimento != null) {
                            if (name.equals("id")) {
                                if(currentProcedimento.getIdentificador()==null){
                                    currentProcedimento.setIdentificador(parser.nextText());
                                }else{
                                    newMaterial.setId(Long.parseLong(parser.nextText()));
                                }
                            } else if (name.equals("descricao")) {
                                if(currentProcedimento.getDescricao()==null){
                                    currentProcedimento.setDescricao(parser.nextText());
                                }else{
                                    newMaterial.setDescricao(parser.nextText());
                                }
                            }else if (name.equals("estado")) {
                                currentProcedimento.setEstado(EstadoProcedimento.valueOf(parser.nextText()));
                            }else if (name.equals("tipo")) {
                                newMaterial.setTipo(parser.nextText());
                            } else if (name.equals("link")) {
                                newMaterial.setLink(parser.nextText());
                            } else if (name.equals("date")) {
                                currentProcedimento.setDate(parser.nextText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("Procedimento") && currentProcedimento != null) {
                            currentProcedimento.setMaterial(newMaterial);
                            Manager.INSTANCE.getListaUtentes().get(position).addProcedimento(currentProcedimento);
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

    private class createProcedimento extends AsyncTask<Procedimento, Void, String> {
        InputStream is = null;

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                readXml(result);
                updateList();
            } else {
                Toast.makeText(getApplicationContext(), "Erro ", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Procedimento... params) {
            try {
                URL url = new URL(Global.createProcedimento);
                String authStr = Manager.INSTANCE.getUsername() + ":" + Manager.INSTANCE.getPassword();
                String authEncoded = Base64.encodeToString(authStr.getBytes(), 0);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");


                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:50.0) Gecko/20100101 Firefox/50.0");
                urlConnection.setConnectTimeout(30000);
                urlConnection.setReadTimeout(30000);

                urlConnection.addRequestProperty("Authorization", "Basic " + authEncoded);
                urlConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                StringBuilder sb = new StringBuilder();
                sb.append("identificador=").append(URLEncoder.encode(params[0].getIdentificador(), "UTF-8"));
                sb.append("&descricao=").append(URLEncoder.encode(params[0].getDescricao(), "UTF-8"));
                sb.append("&material=").append(URLEncoder.encode(Long.toString(params[0].getMaterial().getId()), "UTF-8"));
                sb.append("&utente=").append(URLEncoder.encode(Long.toString(params[0].getIdUtente()), "UTF-8"));
                sb.append("&estado=").append(URLEncoder.encode(params[0].getEstado().name(), "UTF-8"));


                byte[] outputInBytes = sb.toString().getBytes("UTF-8");
                OutputStream os = urlConnection.getOutputStream();
                os.write( outputInBytes );
                os.close();

                urlConnection.connect();

                is = urlConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));


                String line;
                sb = new StringBuilder();
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

    private class updateProcedimento extends AsyncTask<Procedimento, Void, String> {
        InputStream is = null;

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                readXml(result);
                updateList();
            } else {
                Toast.makeText(getApplicationContext(), "Erro ", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Procedimento... params) {
            try {
                URL url = new URL(Global.updateProcedimento);
                String authStr = Manager.INSTANCE.getUsername() + ":" + Manager.INSTANCE.getPassword();
                String authEncoded = Base64.encodeToString(authStr.getBytes(), 0);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");


                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:50.0) Gecko/20100101 Firefox/50.0");
                urlConnection.setConnectTimeout(30000);
                urlConnection.setReadTimeout(30000);

                urlConnection.addRequestProperty("Authorization", "Basic " + authEncoded);
                urlConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                StringBuilder sb = new StringBuilder();
                sb.append("identificador=").append(URLEncoder.encode(params[0].getIdentificador(), "UTF-8"));
                sb.append("&descricao=").append(URLEncoder.encode(params[0].getDescricao(), "UTF-8"));
                sb.append("&material=").append(URLEncoder.encode(Long.toString(params[0].getMaterial().getId()), "UTF-8"));
                sb.append("&utente=").append(URLEncoder.encode(Long.toString(params[0].getIdUtente()), "UTF-8"));
                sb.append("&estado=").append(URLEncoder.encode(params[0].getEstado().name(), "UTF-8"));


                byte[] outputInBytes = sb.toString().getBytes("UTF-8");
                OutputStream os = urlConnection.getOutputStream();
                os.write( outputInBytes );
                os.close();

                urlConnection.connect();

                is = urlConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));


                String line;
                sb = new StringBuilder();
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
