package pt.app.cuidador.cuidador.Objects;

import java.util.LinkedList;

/**
 * Created by Emanuel Lopes on 02-01-2017.
 */

public class Utente {
    private long id;
    private String nome;
    private String email;
    private String morada;
    private String contacto;

    private LinkedList<Procedimento> procedimentos;

    public Utente(){
        procedimentos = new LinkedList<>();
    }
    public Utente(long id, String nomeUtente, String email, String morada, String telefone) {
        this.id = id;
        this.nome = nomeUtente;
        this.email = email;
        this.morada = morada;
        this.contacto = telefone;
        procedimentos = new LinkedList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public LinkedList<Procedimento> getProcedimentos() {
        return procedimentos;
    }

    public void addProcedimento(Procedimento procedimento) {
        this.procedimentos.add(procedimento);
    }

    public void setProcedimento(int pos,Procedimento procedimento){
        this.procedimentos.set(pos, procedimento);
    }

    public void setProcedimentos() {
        procedimentos = new LinkedList<>();
    }
}
