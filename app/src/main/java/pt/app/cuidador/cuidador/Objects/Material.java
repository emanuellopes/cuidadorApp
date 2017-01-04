package pt.app.cuidador.cuidador.Objects;

/**
 * Created by Emanuel Lopes on 02-01-2017.
 */

public class Material {
    private long id;
    private String tipo;
    private String descricao;
    private String link;

    public Material(long id, String tipo, String descricao, String link) {
        this.id = id;
        this.tipo = tipo;
        this.descricao = descricao;
        this.link = link;
    }

    public Material() {
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return tipo;
    }

}
