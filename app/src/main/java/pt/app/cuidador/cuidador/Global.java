package pt.app.cuidador.cuidador;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Emanuel Lopes on 02-01-2017.
 */

public class Global {

    public static final String UTENTE = "UTENTE";
    public static String ip= "http://10.42.0.1:8080";

    public static String login = ip+"/EnterpriseApplicationHealthManager-war/webapi/cuidador/login";
    public static String listarUtentes = ip+"/EnterpriseApplicationHealthManager-war/webapi/cuidador/utentes/";
    public static String listarMateriais = ip+"/EnterpriseApplicationHealthManager-war/webapi/cuidador/material/";

    //http://localhost:8080/EnterpriseApplicationHealthManager-war/webapi/cuidador/procedimentos/1
    public static String listarProcedimentosUtente = ip+"/EnterpriseApplicationHealthManager-war/webapi/cuidador/procedimentos/"; //+/{id}

    //Eliminar procedimento
    //{idProcedimento}/{idUtente}
    //http://localhost:8080/EnterpriseApplicationHealthManager-war/webapi/cuidador/procedimentos/delete/1/1
    public static String eliminarProcedimento = ip+"/EnterpriseApplicationHealthManager-war/webapi/cuidador/procedimentos/delete/";


    //criar procedimento
    //Content-Type application/x-www-form-urlencoded
    //body
    /*
        identificador:23
        descricao:descr
        material:7
        idUtente:1
     */
    public static String createProcedimento = ip+"/EnterpriseApplicationHealthManager-war/webapi/cuidador/procedimentos/create";

    public static String updateProcedimento = ip+"/EnterpriseApplicationHealthManager-war/webapi/cuidador/procedimentos";


    public static boolean checkNetworkConnection(Context c){
        ConnectivityManager connMan = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMan.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
