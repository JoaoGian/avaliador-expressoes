import java.util.Scanner;
import java.lang.Math;

// ===================== PILHAS E LISTAS AUXILIARES =====================

class NoNumero {
    public double valor;
    public NoNumero anterior;

    public NoNumero(double valor) {
        this.valor = valor;
        this.anterior = null;
    }
}

class PilhaNumeros {
    private NoNumero topo;
    private int tamanho;

    public PilhaNumeros() {
        topo = null;
        tamanho = 0;
    }

    public void push(double valor) {
        NoNumero novo = new NoNumero(valor);
        novo.anterior = topo;
        topo = novo;
        tamanho++;
    }

    public double pop() {
        if (topo == null) return 0;
        double valor = topo.valor;
        topo = topo.anterior;
        tamanho--;
        return valor;
    }

    public int tamanho() {
        return tamanho;
    }
}

class NoOperador {
    public String valor;
    public NoOperador anterior;

    public NoOperador(String valor) {
        this.valor = valor;
        this.anterior = null;
    }
}

class PilhaOperadores {
    private NoOperador topo;

    public void push(String valor) {
        NoOperador novo = new NoOperador(valor);
        novo.anterior = topo;
        topo = novo;
    }

    public String pop() {
        if (topo == null) return null;
        String valor = topo.valor;
        topo = topo.anterior;
        return valor;
    }

    public String topo() {
        if (topo == null) return "";
        return topo.valor;
    }

    public boolean vazia() {
        return topo == null;
    }
}

class Token {
    public String valor;
    public Token proximo;

    public Token(String valor) {
        this.valor = valor;
        this.proximo = null;
    }
}

class ListaTokens {
    public Token inicio;
    private Token fim;

    public ListaTokens() {
        inicio = null;
        fim = null;
    }

    public void adicionar(String valor) {
        Token novo = new Token(valor);
        if (inicio == null) {
            inicio = novo;
            fim = novo;
        } else {
            fim.proximo = novo;
            fim = novo;
        }
    }
}

// ===================== VARIÁVEIS =====================

class Variavel {
    public String nome;
    public double valor;
    public boolean definida;

    public Variavel(String nome) {
        this.nome = nome;
        this.definida = false;
    }
}

class NoVariavel {
    public Variavel var;
    public NoVariavel proximo;

    public NoVariavel(Variavel var) {
        this.var = var;
        this.proximo = null;
    }
}

class ListaVariaveis {
    private NoVariavel primeiro;

    public ListaVariaveis() {
        this.primeiro = null;
    }

    public void inserir(Variavel var) {
        NoVariavel novo = new NoVariavel(var);
        novo.proximo = primeiro;
        primeiro = novo;
    }

    public Variavel buscar(String nome) {
        NoVariavel atual = primeiro;
        while (atual != null) {
            if (atual.var.nome.equalsIgnoreCase(nome)) {
                return atual.var;
            }
            atual = atual.proximo;
        }
        return null;
    }

    public boolean vazia() {
        return primeiro == null;
    }

    public void listarDefinidas() {
        NoVariavel atual = primeiro;
        while (atual != null) {
            if (atual.var.definida) {
                System.out.println(atual.var.nome.toUpperCase() + " = " + (int) atual.var.valor);
            }
            atual = atual.proximo;
        }
    }
}

class Variaveis {
    private ListaVariaveis lista;

    public Variaveis() {
        this.lista = new ListaVariaveis();
    }

    public void definir(String nome, double valor) {
        Variavel v = lista.buscar(nome);
        if (v == null) {
            v = new Variavel(nome);
            lista.inserir(v);
        }
        v.valor = valor;
        v.definida = true;
    }

    public boolean existe(String nome) {
        Variavel v = lista.buscar(nome);
        return v != null && v.definida;
    }

    public double obterValor(String nome) {
        Variavel v = lista.buscar(nome);
        return v.valor;
    }

    public void reiniciar() {
        lista = new ListaVariaveis();
    }

    public void listar() {
        if (lista.vazia()) {
            System.out.println("Nenhuma variável definida.");
        } else {
            lista.listarDefinidas();
        }
    }
}

// ===================== EXPRESSÃO =====================

class Expressao {
    private String expressao;
    private Variaveis variaveis;

    public Expressao(String expressao, Variaveis variaveis) {
        this.expressao = expressao;
        this.variaveis = variaveis;
    }

    public void processar() {
        try {
            ListaTokens posfixa = converterParaPosfixa(expressao);
            double resultado = avaliarPosfixa(posfixa);
            System.out.println((int) resultado);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private ListaTokens converterParaPosfixa(String expr) throws Exception {
        PilhaOperadores pilha = new PilhaOperadores();
        ListaTokens saida = new ListaTokens();

        String[] tokens = expr.replaceAll("\\(", " ( ").replaceAll("\\)", " ) ").trim().split("\\s+");

        for (String token : tokens) {
            if (token.matches("[a-zA-Z]")) {
                if (!variaveis.existe(token)) {
                    throw new Exception("Erro: variável " + token + " não definida.");
                }
                saida.adicionar(token);
            } else if (ehOperador(token)) {
                while (!pilha.vazia() && prioridade(pilha.topo()) >= prioridade(token)) {
                    saida.adicionar(pilha.pop());
                }
                pilha.push(token);
            } else if (token.equals("(")) {
                pilha.push(token);
            } else if (token.equals(")")) {
                while (!pilha.vazia() && !pilha.topo().equals("(")) {
                    saida.adicionar(pilha.pop());
                }
                if (pilha.vazia()) throw new Exception("Erro: expressão inválida.");
                pilha.pop(); // remove '('
            } else {
                throw new Exception("Erro: operador inválido.");
            }
        }

        while (!pilha.vazia()) {
            String op = pilha.pop();
            if (op.equals("(") || op.equals(")")) throw new Exception("Erro: expressão inválida.");
            saida.adicionar(op);
        }

        return saida;
    }

    private double avaliarPosfixa(ListaTokens lista) throws Exception {
        PilhaNumeros pilha = new PilhaNumeros();
        Token atual = lista.inicio;

        while (atual != null) {
            String tk = atual.valor;
            if (tk.matches("[a-zA-Z]")) {
                pilha.push(variaveis.obterValor(tk));
            } else if (tk.matches("-?\\d+(\\.\\d+)?")) {
                pilha.push(Double.parseDouble(tk));
            } else if (ehOperador(tk)) {
                if (pilha.tamanho() < 2) throw new Exception("Erro: expressão inválida.");
                double b = pilha.pop();
                double a = pilha.pop();
                pilha.push(aplicarOperador(a, b, tk));
            }
            atual = atual.proximo;
        }

        if (pilha.tamanho() != 1) throw new Exception("Erro: expressão inválida.");
        return pilha.pop();
    }

    private boolean ehOperador(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("^");
    }

    private int prioridade(String op) {
        if (op.equals("+") || op.equals("-")) return 1;
        if (op.equals("*") || op.equals("/")) return 2;
        if (op.equals("^")) return 3;
        return 0;
    }

    private double aplicarOperador(double a, double b, String op) throws Exception {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return a / b;
            case "^": return Math.pow(a, b);
            default: throw new Exception("Erro: operador inválido.");
        }
    }
}

// ===================== GRAVADOR DE COMANDOS =====================

class Comando {
    public String texto;
    public Comando proximo;

    public Comando(String texto) {
        this.texto = texto;
        this.proximo = null;
    }
}

class GravadorComandos {
    private Comando inicio;
    private int quantidade;

    public GravadorComandos() {
        this.inicio = null;
        this.quantidade = 0;
    }

    public void iniciar() {
        this.inicio = null;
        this.quantidade = 0;
    }

    public boolean adicionarComando(String texto) {
        if (quantidade >= 10) return false;
        Comando novo = new Comando(texto);
        if (inicio == null) {
            inicio = novo;
        } else {
            Comando atual = inicio;
            while (atual.proximo != null) atual = atual.proximo;
            atual.proximo = novo;
        }
        quantidade++;
        return true;
    }

    public void executar(Variaveis variaveis) {
        if (inicio == null) {
            System.out.println("Não há gravação para ser reproduzida.");
            return;
        }
        System.out.println("Reproduzindo gravação...");
        Comando atual = inicio;
        while (atual != null) {
            String linha = atual.texto;
            System.out.println(linha);
            if (linha.contains("=")) {
                String[] partes = linha.split("=");
                if (partes.length == 2) {
                    String var = partes[0].trim();
                    try {
                        double valor = Double.parseDouble(partes[1].trim());
                        variaveis.definir(var, valor);
                    } catch (Exception e) {
                        System.out.println("Erro: valor inválido.");
                    }
                }
            } else if (linha.equalsIgnoreCase("RESET")) {
                variaveis.reiniciar();
                System.out.println("Variáveis reiniciadas.");
            } else if (linha.equalsIgnoreCase("VARS")) {
                variaveis.listar();
            } else {
                Expressao expressao = new Expressao(linha, variaveis);
                expressao.processar();
            }
            atual = atual.proximo;
        }
    }

    public void apagar() {
        inicio = null;
        quantidade = 0;
    }

    public int getQuantidade() {
        return quantidade;
    }
}

// ===================== REPL =====================

class REPL {
    private Scanner scanner;
    private Variaveis variaveis;
    private GravadorComandos gravador;
    private boolean gravando;

    public REPL(Scanner scanner) {
        this.scanner = scanner;
        this.variaveis = new Variaveis();
        this.gravador = new GravadorComandos();
        this.gravando = false;
    }

    public void iniciar() {
        while (true) {
            System.out.print("> ");
            String linha = scanner.nextLine().trim();

            if (gravando && !linha.equalsIgnoreCase("REC") &&
                !linha.equalsIgnoreCase("STOP") &&
                !linha.equalsIgnoreCase("PLAY") &&
                !linha.equalsIgnoreCase("ERASE") &&
                !linha.equalsIgnoreCase("EXIT")) {

                if (gravador.adicionarComando(linha)) {
                    System.out.println("(REC: " + gravador.getQuantidade() + "/10) " + linha);
                } else {
                    System.out.println("Gravação cheia. Parando automaticamente.");
                    gravando = false;
                }
                continue;
            }

            if (linha.equalsIgnoreCase("EXIT")) break;
            else if (linha.equalsIgnoreCase("RESET")) {
                variaveis.reiniciar();
                System.out.println("Variáveis reiniciadas.");
            } else if (linha.equalsIgnoreCase("VARS")) {
                variaveis.listar();
            } else if (linha.equalsIgnoreCase("REC")) {
                gravador.iniciar();
                gravando = true;
                System.out.println("Iniciando gravação... (REC: 0/10)");
            } else if (linha.equalsIgnoreCase("STOP")) {
                gravando = false;
                System.out.println("Encerrando gravação... (REC: " + gravador.getQuantidade() + "/10)");
            } else if (linha.equalsIgnoreCase("PLAY")) {
                gravador.executar(variaveis);
            } else if (linha.equalsIgnoreCase("ERASE")) {
                gravador.apagar();
                System.out.println("Gravação apagada.");
            } else if (linha.contains("=")) {
                String[] partes = linha.split("=");
                if (partes.length == 2) {
                    String var = partes[0].trim();
                    try {
                        double valor = Double.parseDouble(partes[1].trim());
                        variaveis.definir(var, valor);
                    } catch (Exception e) {
                        System.out.println("Erro: valor inválido.");
                    }
                } else {
                    System.out.println("Erro: comando inválido.");
                }
            } else {
                Expressao expressao = new Expressao(linha, variaveis);
                expressao.processar();
            }
        }
    }
}
