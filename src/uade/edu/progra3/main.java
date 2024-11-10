package uade.edu.progra3;


import uade.edu.progra3.model.Bloque;
import uade.edu.progra3.model.Transaccion;

import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) {
        // Crear las transacciones de prueba
        Transaccion t1 = new Transaccion(200, 30, null, 1);
        Transaccion t2 = new Transaccion(150, 40, t1, 1);
        Transaccion t3 = new Transaccion(300, 50, null, 1);
        Transaccion t4 = new Transaccion(100, 20, t3, 1);
        Transaccion t5 = new Transaccion(250, 30, null, 1);

        // Agregar las transacciones a una lista
        List<Transaccion> transacciones = new ArrayList<>();
        transacciones.add(t1);
        transacciones.add(t2);
        transacciones.add(t3);
        transacciones.add(t4);
        transacciones.add(t5);

        // Definir los parámetros para el algoritmo
        int maxTamanioBloque = 1000;
        int maxValorBloque = 100;
        int maxTransacciones = 3;
        int maxBloques = 5;

        // Crear una instancia del algoritmo de Blockchain
        AlgoritmoDeBlockchainImpl algoritmo = new AlgoritmoDeBlockchainImpl();

        // Llamar al método para construir la cadena de bloques
        List<List<Bloque>> cadenaDeBloques = algoritmo.construirBlockchain(transacciones, maxTamanioBloque, maxValorBloque, maxTransacciones, maxBloques);

        // Imprimir los resultados
        System.out.println("Cadena de bloques generada:");
        for (List<Bloque> bloqueList : cadenaDeBloques) {
            for (Bloque bloque : bloqueList) {
                System.out.println("Bloque con transacciones:");
                for (Transaccion transaccion : bloque.getTransacciones()) {
                    System.out.println("Transacción: Tamaño: " + transaccion.getTamanio() + ", Valor: " + transaccion.getValor());
                }
                System.out.println("Tamaño total: " + bloque.getTamanioTotal());
                System.out.println("Valor total: " + bloque.getValorTotal());
            }
        }
    }
}
