package pt.app.cuidador.cuidador.Objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.LinkedList;

import pt.app.cuidador.cuidador.R;

/**
 * Created by Emanuel Lopes on 02-01-2017.
 */

public class AdapterUtente extends ArrayAdapter<Utente> {

    private LinkedList<Utente> utenteList;
    private Context mContext;

    private ViewHolder viewHolder;

    public AdapterUtente(LinkedList<Utente> utenteList, Context context) {
        super(context, R.layout.row_item_utente, utenteList);
        this.mContext = context;
        this.utenteList = utenteList;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtEmail;
        TextView txtMorada;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Utente utente = getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_utente, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.nome_utente);
            viewHolder.txtEmail = (TextView) convertView.findViewById(R.id.email);
            viewHolder.txtMorada = (TextView) convertView.findViewById(R.id.morada);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            viewHolder.txtName.setText("Nome: "+utente.getNome());
            viewHolder.txtEmail.setText("Email: "+utente.getEmail());
            viewHolder.txtMorada.setText("Morada: "+utente.getMorada());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }


}
