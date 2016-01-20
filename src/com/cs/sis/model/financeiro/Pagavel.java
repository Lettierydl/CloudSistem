package com.cs.sis.model.financeiro;

import com.cs.sis.model.pessoas.Cliente;
import java.util.Calendar;

public interface Pagavel {

    public int getId();

    public double getTotal();

    public double getPartePaga();

    public double getValorNaoPago();

    public boolean isPaga();

    public Cliente getCliente();

    public Calendar getDia();

    public String getDescricao();

    public void acrescentarPartePaga(double partePaga);

}
