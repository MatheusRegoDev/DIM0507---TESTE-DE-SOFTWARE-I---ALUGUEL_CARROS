# Aluguel de carros — Trabalho de Teste de Software

## Autores

- **Marcelo Vieira**
- **Matheus Rodrigues**

## Objetivo

Aplicar técnicas de teste funcional — particionamento em classes de equivalência, análise de valores-limite e tabela de decisão — ao cálculo do custo de locação de veículos, utilizando testes automatizados com JUnit 5.

A implementação de `CalculadoraCustoLocacao` foi fornecida para a atividade e contém defeitos que devem ser evidenciados pelos testes. Conforme o enunciado, a implementação não deve ser corrigida para fazer os testes passarem.

## Estrutura do projeto

- `src/main/java/br/com/aluguelcarros`: enums e implementação do cálculo.
- `src/test/java/br/com/aluguelcarros`: testes automatizados com JUnit 5.
- `ENUNCIADO.md`: especificação da atividade e regras de negócio.
- `pom.xml`: configuração Maven e dependência do JUnit 5.

## Execução dos testes

O comando previsto para executar os testes é:

```bash
mvn test
```

A classe `CalculadoraCustoLocacao` foi originalmente implementada pelo modelo `qwen2.5-coder:3b` e recebeu ajustes manuais para a atividade. A implementação contém defeitos preservados para serem identificados pelos testes e não deve ser corrigida pelos alunos.

O comportamento esperado está definido no [enunciado](ENUNCIADO.md). Testes que evidenciem defeitos podem fazer `mvn test` terminar com falhas; esses resultados devem ser analisados e explicados no relatório conforme o enunciado.

## Organização dos testes executados

A classe `CalculadoraCustoLocacaoTest` contém:

- 1 teste unitário para uma locação básica;
- 7 invocações parametrizadas para as regras R01 a R07 da tabela de decisão;
- 4 invocações parametrizadas para valores numéricos inválidos;
- 3 invocações parametrizadas para parâmetros nulos.

### Cobertura da tabela de decisão

As sete regras da tabela de decisão foram representadas pelos seguintes cenários:

| Regra | Cenário | Resultado esperado |
|---|---|---:|
| R01 | Cliente COMUM sem fidelidade | R$ 120,00 |
| R02 | PRATA abaixo do mínimo de diárias | R$ 684,00 |
| R03 | PRATA com atraso anterior | R$ 756,00 |
| R04 | PRATA elegível | R$ 718,20 |
| R05 | OURO abaixo do mínimo de diárias | R$ 456,00 |
| R06 | OURO com atraso anterior | R$ 570,00 |
| R07 | OURO elegível | R$ 513,00 |

### Cobertura das entradas inválidas

Foram testadas as seguintes situações:

- `numeroDiarias == 0`;
- `numeroDiarias < 0`;
- `quilometrosRodados < 0`;
- `horasAtraso < 0`;
- `categoria == null`;
- `nivelCliente == null`;
- `seguro == null`.

Os testes esperam `IllegalArgumentException` para os valores numéricos inválidos e `NullPointerException` para os parâmetros nulos.

## Análise detalhada das falhas

As seis falhas ocorreram nos cenários da tabela de decisão R02 a R07. O cenário R01 foi aprovado, assim como todos os testes de entradas inválidas.

### R02 — PRATA abaixo do mínimo

- **Caso:** `R02 PRATA abaixo do mínimo`
- **Entradas:** `ECONOMICO, PRATA, 6, 600, 0, SEM_SEGURO, false`
- **Resultado esperado:** `R$ 684,00`
- **Resultado observado:** `R$ 1.470,00`
- **Análise:** Com 6 diárias, o enunciado determina desconto de 5%. Como cada diária inclui 100 km de franquia, a franquia total é de 600 km e não deve haver cobrança adicional de quilometragem. Além disso, subtrai sempre 100 km, em vez de calcular `100 × numeroDiarias`, gerando cobrança indevida de quilometragem.

### R03 — PRATA com atraso anterior

- **Caso:** `R03 PRATA com atraso anterior`
- **Entradas:** `ECONOMICO, PRATA, 7, 700, 0, SEM_SEGURO, true`
- **Resultado esperado:** `R$ 756,00`
- **Resultado observado:** `R$ 1.740,00`
- **Análise:** O atraso anterior impede o benefício de fidelidade, mas a locação com 7 diárias deve receber desconto de 10%. A franquia total deve ser de 700 km. Não ocorre o tratamento corretamente da faixa de 7 a 14 diárias e cobra quilometragem excedente usando uma franquia fixa de 100 km.

### R04 — PRATA elegível

- **Caso:** `R04 PRATA elegível`
- **Entradas:** `ECONOMICO, PRATA, 7, 700, 0, SEM_SEGURO, false`
- **Resultado esperado:** `R$ 718,20`
- **Resultado observado:** `R$ 1.734,00`
- **Análise:** Com 7 diárias, o desconto é de 10%. Como o cliente PRATA não possui atraso anterior, recebe ainda 5% sobre o valor das diárias após o desconto: `R$ 756,00 × 5% = R$ 37,80`, resultando em `R$ 718,20`. Não deve haver cobrança de quilometragem porque os 700 km estão dentro da franquia total. O código implementado não aplica corretamente o desconto de duração para 7 diárias e cobra quilometragem excedente indevida.

### R05 — OURO abaixo do mínimo

- **Caso:** `R05 OURO abaixo do mínimo`
- **Entradas:** `ECONOMICO, OURO, 4, 400, 0, SEM_SEGURO, false`
- **Resultado esperado:** `R$ 456,00`
- **Resultado observado:** `R$ 732,00`
- **Análise:** Com 4 diárias, o cliente OURO ainda não tem direito ao benefício de fidelidade. A faixa de duração de 3 a 6 diárias, entretanto, exige desconto de 5%, e os 400 km estão dentro da franquia. O código implementado associa o valor de 10% ao valor 4 e usa franquia fixa de 100 km, provocando erro no valor total.

### R06 — OURO com atraso anterior

- **Caso:** `R06 OURO com atraso anterior`
- **Entradas:** `ECONOMICO, OURO, 5, 500, 0, SEM_SEGURO, true`
- **Resultado esperado:** `R$ 570,00`
- **Resultado observado:** `R$ 1.110,00`
- **Análise:** O atraso anterior impede o benefício OURO. Com 5 diárias, o desconto por duração correto é de 5%, totalizando R$ 570,00 antes de outros componentes. Os 500 km estão dentro da franquia total. O código implementado aplica desconto de 15% ao valor 5 e cobra quilometragem excedente indevida.

### R07 — OURO elegível

- **Caso:** `R07 OURO elegível`
- **Entradas:** `ECONOMICO, OURO, 5, 500, 0, SEM_SEGURO, false`
- **Resultado esperado:** `R$ 513,00`
- **Resultado observado:** `R$ 1.098,00`
- **Análise:** Com 5 diárias e sem atraso anterior, o cliente OURO recebe 10% sobre o valor das diárias após o desconto de duração. O valor das diárias após 5% de desconto é R$ 570,00; o desconto de fidelidade é R$ 57,00, resultando em R$ 513,00. A franquia total é 500 km e não há custo de quilometragem. O código implementado aplica o desconto de duração incorreto e cobra quilometragem além da franquia.

## Defeitos evidenciados na implementação

### Desconto por duração tratado por valores exatos

O enunciado define as seguintes faixas:

- 1 a 2 diárias: 0%;
- 3 a 6 diárias: 5%;
- 7 a 14 diárias: 10%;
- 15 ou mais diárias: 15%.

A implementação utiliza chaves exatas no mapa de descontos:

```java
DESCONTOS_DIARIAS.put(1, BigDecimal.ZERO);
DESCONTOS_DIARIAS.put(2, BigDecimal.ZERO);
DESCONTOS_DIARIAS.put(3, BigDecimal.valueOf(0.05));
DESCONTOS_DIARIAS.put(4, BigDecimal.valueOf(0.1));
DESCONTOS_DIARIAS.put(5, BigDecimal.valueOf(0.15));
```

Consequentemente, quantidades como 6, 7, 14 e 15 não recebem os descontos definidos nas faixas. Além disso, 4 diárias recebem 10% e 5 diárias recebem 15%, contrariando as regras do enunciado.

### Franquia de quilometragem calculada incorretamente

O enunciado determina 100 km de franquia para cada diária. Portanto:

```text
franquia total = 100 × numeroDiarias
```

A implementação calcula o excedente subtraindo sempre 100 km da quilometragem rodada. Isso causa cobrança adicional indevida em todos os casos em que a quilometragem está entre 101 km e a franquia total correta.
