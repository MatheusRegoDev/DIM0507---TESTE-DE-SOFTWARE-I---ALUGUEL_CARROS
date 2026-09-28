package br.com.aluguelcarros;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class CalculadoraCustoLocacaoTest
{
    private final CalculadoraCustoLocacao calculadora = new CalculadoraCustoLocacao();

    @Test
    void deveCalcularLocacaoBasicaSemCustosAdicionais()
    {
        BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 100, 0, TipoSeguro.SEM_SEGURO, false);

        assertEquals(new BigDecimal("120.00"), resultado);
    }
}
