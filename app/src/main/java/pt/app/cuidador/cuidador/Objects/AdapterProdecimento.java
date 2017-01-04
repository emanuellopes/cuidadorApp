package pt.app.cuidador.cuidador.Objects;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import pt.app.cuidador.cuidador.Activities.CuidadorActivity;
import pt.app.cuidador.cuidador.Global;
import pt.app.cuidador.cuidador.Manager;
import pt.app.cuidador.cuidador.R;

/**
 * Created by Emanuel Lopes on 02-01-2017.
 */

public class AdapterProdecimento extends ArrayAdapter<Procedimento> implements View.OnClickListener{

    private LinkedList<Procedimento> procedimentoList;
    private Context mContext;

    private ViewHolder viewHolder;

    public AdapterProdecimento(LinkedList<Procedimento> procedimentoList, Context context) {
        super(context, R.layout.row_item_procedimento, procedimentoList);
        this.mContext = context;
        this.procedimentoList = procedimentoList;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtID;
        TextView txtDescricao;
        TextView txtMaterial;
        TextView txtEstado;
        TextView txtDate;
        ImageView delete;
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Procedimento procedimento = getItem(position);
        switch (v.getId()) {
            case R.id.deleteProcedimento:
                try{
                    if (Global.checkNetworkConnection(mContext)) {
                        new deleteProcedimento().execute(Global.eliminarProcedimento, Manager.INSTANCE.getUsername(),
                                Manager.INSTANCE.getPassword(), procedimento.getIdentificador(),
                                Long.toString(procedimento.getIdUtente()));
                        procedimentoList.remove(procedimento);
                    } else {
                        Toast.makeText(mContext, "No network available!", Toast.LENGTH_SHORT).show();
                    }


                    notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Procedimento procedimento = getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_procedimento, parent, false);
            viewHolder.txtID = (TextView) convertView.findViewById(R.id.identificador);
            viewHolder.txtDescricao = (TextView) convertView.findViewById(R.id.descricao);
            viewHolder.txtMaterial = (TextView) convertView.findViewById(R.id.material);
            viewHolder.txtEstado = (TextView) convertView.findViewById(R.id.estado);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.textViewDate);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.deleteProcedimento);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            Log.e("date", procedimento.getDate()+"a");
            viewHolder.txtID.setText("ID: "+procedimento.getIdentificador());
            viewHolder.txtDescricao.setText("Descrição: "+procedimento.getDescricao());
            viewHolder.txtMaterial.setText("Material: "+procedimento.getMaterial().toString());
            viewHolder.txtEstado.setText("Estado: "+procedimento.getEstado().toString());
            viewHolder.txtDate.setText("Date: "+procedimento.getDate());
            viewHolder.delete.setOnClickListener(this);
            viewHolder.delete.setTag(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class deleteProcedimento extends AsyncTask<String, Void, String> {
        InputStream is = null;

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.e("lol", result+"aaa");
                //readXml(result);
            } else {
                Toast.makeText(mContext, "Erro ", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]+params[3]+"/"+params[4]);
                String authStr = params[1] + ":" + params[2];
                String authEncoded = Base64.encodeToString(authStr.getBytes(), 0);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");


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
