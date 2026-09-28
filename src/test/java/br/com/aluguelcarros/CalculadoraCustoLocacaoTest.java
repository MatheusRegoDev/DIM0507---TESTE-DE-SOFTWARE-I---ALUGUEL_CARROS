package br.com.aluguelcarros;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CalculadoraCustoLocacaoTest
{
    private final CalculadoraCustoLocacao calculadora = new CalculadoraCustoLocacao();

    @Test
    void deveCalcularLocacaoBasicaSemCustosAdicionais()
    {
        BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 100, 0, TipoSeguro.SEM_SEGURO, false);

        assertEquals(new BigDecimal("120.00"), resultado);
    }

    @Nested
    @DisplayName("Tabela de Decisão — Fidelidade (R01..R07)")
    class TabelaDecisaoFidelidade
    {
        @ParameterizedTest(name = "[{index}] {0} -> {9}")
        @CsvSource({
                "R01 COMUM sem fidelidade,          ECONOMICO, COMUM, 1, 100, 0, SEM_SEGURO, false, 120.00",
                "R02 PRATA abaixo do mínimo,        ECONOMICO, PRATA, 6, 600, 0, SEM_SEGURO, false, 684.00",
                "R03 PRATA com atraso anterior,     ECONOMICO, PRATA, 7, 700, 0, SEM_SEGURO, true,  756.00",
                "R04 PRATA elegível,                ECONOMICO, PRATA, 7, 700, 0, SEM_SEGURO, false, 718.20",
                "R05 OURO abaixo do mínimo,         ECONOMICO, OURO,  4, 400, 0, SEM_SEGURO, false, 456.00",
                "R06 OURO com atraso anterior,      ECONOMICO, OURO,  5, 500, 0, SEM_SEGURO, true,  570.00",
                "R07 OURO elegível,                 ECONOMICO, OURO,  5, 500, 0, SEM_SEGURO, false, 513.00"
        })
        @DisplayName("Deve aplicar a regra da tabela de decisão corretamente")
        void deveAplicarRegraDaTabelaDecisao(
                String cenario,
                CategoriaVeiculo categoria,
                NivelCliente nivelCliente,
                int numeroDiarias,
                int quilometrosRodados,
                int horasAtraso,
                TipoSeguro seguro,
                boolean possuiAtrasoAnterior,
                BigDecimal esperado)
        {
            BigDecimal obtido = calculadora.calcularCustoLocacao(
                    categoria, nivelCliente, numeroDiarias,
                    quilometrosRodados, horasAtraso, seguro, possuiAtrasoAnterior);

            assertEquals(0, esperado.compareTo(obtido),
                    () -> cenario + " — esperado=" + esperado + ", obtido=" + obtido);
        }
    }

    @Nested
    @DisplayName("Entradas Inválidas")
    class EntradasInvalidas
    {
        @ParameterizedTest
        @CsvSource({
                "ECONOMICO, COMUM, 0, 100, 0, SEM_SEGURO, false",
                "ECONOMICO, COMUM, -1, 100, 0, SEM_SEGURO, false",
                "ECONOMICO, COMUM, 1, -100, 0, SEM_SEGURO, false",
                "ECONOMICO, COMUM, 1, 100, -1, SEM_SEGURO, false",
        })
        @DisplayName("Deve lançar IllegalArgumentException para parâmetros inválidos")
        void deveLancarExcecaoParaParametrosInvalidos(
                CategoriaVeiculo categoria,
                NivelCliente nivelCliente,
                int numeroDiarias,
                int quilometrosRodados,
                int horasAtraso,
                TipoSeguro seguro,
                boolean possuiAtrasoAnterior)
        {
            assertThrows(IllegalArgumentException.class, () -> {
                calculadora.calcularCustoLocacao(categoria, nivelCliente, numeroDiarias, quilometrosRodados, horasAtraso, seguro, possuiAtrasoAnterior);
            });
        }
    }

    @Nested
    @DisplayName("Entradas com valores nulos")
    class EntradasNulas
    {
        @ParameterizedTest
        @CsvSource(
                value = {
                        "null, COMUM, 1, 100, 0, SEM_SEGURO, false",
                        "ECONOMICO, null, 1, 100, 0, SEM_SEGURO, false",
                        "ECONOMICO, COMUM, 1, 100, 0, null, false"
                },
                nullValues = "null"
        )
        @DisplayName("Deve lançar NullPointerException para parâmetros nulos")
        void deveLancarExcecaoParaParametrosNulos(CategoriaVeiculo categoria, NivelCliente nivelCliente, int numeroDiarias, int quilometrosRodados, int horasAtraso, TipoSeguro seguro, boolean possuiAtrasoAnterior)
        {
            assertThrows(NullPointerException.class, () -> {
                calculadora.calcularCustoLocacao(categoria, nivelCliente, numeroDiarias, quilometrosRodados, horasAtraso, seguro, possuiAtrasoAnterior);
            });
        }
    }
}
