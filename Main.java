
// Joao Pedro Gianfaldoni 10409524
// Joao Rocha Murgel  10410293
// Andres Nunes Filipe 10409599
// Referências utilizadas:
// - ChatGPT (OpenAI): auxílio na estruturação do REPL e lógica de avaliação de expressões.
// - Vídeos sobre  pilhas no canal Curso em Vídeo (YouTube).
// - GDB Online Compiler (https://www.onlinegdb.com/) para testes do código Java.

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        REPL repl = new REPL(scanner);
        repl.iniciar();
    }
}
