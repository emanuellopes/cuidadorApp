package pt.app.cuidador.cuidador;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.LinkedList;

import pt.app.cuidador.cuidador.Objects.Material;
import pt.app.cuidador.cuidador.Objects.Utente;

/**
 * Created by Emanuel Lopes on 02-01-2017.
 */

public enum Manager {
    INSTANCE;

    private LinkedList<Utente> listautentes;
    private LinkedList<Material> listaMaterial;
    private String username;
    private String password;

    Manager() {
        this.listautentes = new LinkedList<>();
        this.listaMaterial = new LinkedList<>();
    }

    public LinkedList<Utente> getListaUtentes() {
        return listautentes;
    }

    public void addUtente(Utente utente){
        listautentes.add(utente);
    }

    public LinkedList<Material> getListaMaterial() {
        return listaMaterial;
    }

    public void addMaterial(Material material) {
        this.listaMaterial.add(material);
    }

    public void setListaUtentes(){
        this.listautentes= new LinkedList<>();
    }

    public void setListaMaterial(){
        this.listaMaterial= new LinkedList<>();
    }


    public void saveLogin(String username, String password, Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
        editor.apply();
        this.username = username;
        this.password = password;
    }

    public void deleteLogin(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.commit();
        editor.apply();
    }

    public String getLogin(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        username =sharedPref.getString("username", "");
        password =sharedPref.getString("password", "");
        return sharedPref.getString("username", "");
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
