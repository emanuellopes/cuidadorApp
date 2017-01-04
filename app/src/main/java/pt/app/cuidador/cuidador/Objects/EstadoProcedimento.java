package pt.app.cuidador.cuidador.Objects;

import android.util.Log;

/**
 * Created by Emanuel Lopes on 03-01-2017.
 */

public enum EstadoProcedimento {
    Concluido("Concluido"), Em_Curso("Em Curso"), A_iniciar("A iniciar"), Cancelado("Cancelado");

    private String value;

    EstadoProcedimento(final String value) {
        this.value = value;
    }

   @Override
    public String toString() {
        return this.value;
    }

}
