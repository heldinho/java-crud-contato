
public class MultiplicacaoMatriz {

    public static void main(String[] args) {
        // Matriz 4x3
        int[][] matriz = {
            {2, 4, 3},
            {1, 2, 1},
            {1, 2, 1},
            {1, 2, 0}
        };
        // Vetor de multiplicação
        int[] vetor = {3, 2, 4};
        // Vetor de resultado: uma posição por linha
        int[] resultado = new int[matriz.length];
        // Multiplica cada linha pelo vetor e soma os valores
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < vetor.length; j++) {
                resultado[i] += matriz[i][j] * vetor[j];
            }
        }
        // Exibe o resultado
        System.out.println("Resultado da multiplicação:");
        for (int valor : resultado) {
            System.out.println(valor);
        }
    }
}
