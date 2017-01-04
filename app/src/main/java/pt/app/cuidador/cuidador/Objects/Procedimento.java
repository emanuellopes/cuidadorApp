package pt.app.cuidador.cuidador.Objects;

import java.util.Date;

/**
 * Created by Emanuel Lopes on 02-01-2017.
 */

public class Procedimento {
    private String identificador;
    private String descricao;
    private Material material;
    private EstadoProcedimento estado;
    private long idUtente;
    private String date;


    public Procedimento(long idUtente, String identificador, String descricao, Material material,
                        EstadoProcedimento estado, String date) {
        this.idUtente = idUtente;
        this.identificador = identificador;
        this.descricao = descricao;
        this.material = material;
        this.estado = estado;
        this.date = date;
    }

    public Procedimento() {
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public EstadoProcedimento getEstado() {
        return estado;
    }

    public void setEstado(EstadoProcedimento estado) {
        this.estado = estado;
    }

    public long getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(long idUtente) {
        this.idUtente = idUtente;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
