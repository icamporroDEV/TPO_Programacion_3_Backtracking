package uade.edu.progra3;

import uade.edu.progra3.model.Bloque;
import uade.edu.progra3.model.Transaccion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlgoritmoDeBlockchainImpl implements AlgoritmoDeBlockchain {

    @Override
    public List<List<Bloque>> construirBlockchain(List<Transaccion> transacciones, int maxTamanioBloque, int maxValorBloque, int maxTransacciones, int maxBloques) {
        List<List<Bloque>> cadenaDeBloques = new ArrayList<>();
        Set<Transaccion> transaccionesYaAgregadas = new HashSet<>();
        
        int i = 0;
        // Mientras haya transacciones que procesar
        while (!transacciones.isEmpty()) {
            // Creamos un nuevo bloque vacío
            Bloque bloqueActual = new Bloque();

            // Intentamos crear un bloque con el backtracking
            List<Bloque> bloque = backtrackingConstruirBloque(transacciones, maxTamanioBloque, maxValorBloque, maxTransacciones, transaccionesYaAgregadas, bloqueActual);
            
            if (!bloque.isEmpty()) {
                // Agregamos el bloque generado a la cadena de bloques
                cadenaDeBloques.add(bloque);
                
                // Actualizamos las transacciones restantes
                actualizarTransaccionesRestantes(transacciones, bloque);
                
                i++;
            } else {
                // Si no se pudo crear un bloque, rompemos el ciclo
                break;
            }
            
            // Si llegamos al máximo de bloques, salimos del ciclo
            if (i >= maxBloques) {
                break;
            }
        }

        return cadenaDeBloques;
    }

    private List<Bloque> backtrackingConstruirBloque(List<Transaccion> transacciones, int maxTamanioBloque, int maxValorBloque, int maxTransacciones, Set<Transaccion> transaccionesYaAgregadas, Bloque bloqueActual) {
        int tamanioActual = bloqueActual.getTransacciones().stream().mapToInt(Transaccion::getTamanio).sum();
        int valorActual = bloqueActual.getTransacciones().stream().mapToInt(Transaccion::getValor).sum();

        // Caso base: Si alcanzamos el máximo de transacciones o no hay transacciones disponibles
        if (bloqueActual.getTransacciones().size() == maxTransacciones || transacciones.isEmpty()) {
            if (tamanioActual <= maxTamanioBloque && valorActual <= maxValorBloque && validacionPow(bloqueActual.getTransacciones())) {
                List<Bloque> resultado = new ArrayList<>();
                bloqueActual.setTamanioTotal(tamanioActual);
                bloqueActual.setValorTotal(valorActual);
                resultado.add(bloqueActual);
                return resultado;
            }
        }

        // Iteramos sobre las transacciones restantes
        for (Transaccion transaccion : transacciones) {
            // Asegurarnos de que la transacción no haya sido procesada antes
            if (!transaccionesYaAgregadas.contains(transaccion)) {
                // Actualizamos el tamaño y el valor al agregar la transacción
                bloqueActual.getTransacciones().add(transaccion);
                tamanioActual += transaccion.getTamanio();
                valorActual += transaccion.getValor();
                transaccionesYaAgregadas.add(transaccion);  // Marca la transacción como procesada

                // Si el tamaño o el valor superan los límites, deshacemos la operación
                if (tamanioActual > maxTamanioBloque || valorActual > maxValorBloque) {
                    // No volvemos a reiniciar todo el bloque, solo retiramos la última transacción
                    bloqueActual.getTransacciones().remove(bloqueActual.getTransacciones().size() - 1);
                    tamanioActual -= transaccion.getTamanio();
                    valorActual -= transaccion.getValor();
                    transaccionesYaAgregadas.remove(transaccion);  // Desmarcar la transacción
                    continue;
                }

                // Recursión con el bloque actualizado
                List<Bloque> resultadoRecursivo = backtrackingConstruirBloque(transacciones, maxTamanioBloque, maxValorBloque, maxTransacciones, transaccionesYaAgregadas, bloqueActual);

                // Si encontramos una solución recursiva, la retornamos
                if (!resultadoRecursivo.isEmpty()) {
                    return resultadoRecursivo;
                }

                // Si no se encuentra una solución, retiramos la última transacción
                bloqueActual.getTransacciones().remove(bloqueActual.getTransacciones().size() - 1);
                tamanioActual -= transaccion.getTamanio();
                valorActual -= transaccion.getValor();
                transaccionesYaAgregadas.remove(transaccion);  // Desmarcar la transacción
            }
        }

        // Si el bloque tiene transacciones y cumple con las validaciones, lo devolvemos
        if (!bloqueActual.getTransacciones().isEmpty() &&
            tamanioActual <= maxTamanioBloque && 
            valorActual <= maxValorBloque && 
            validacionPow(bloqueActual.getTransacciones())) {
            
            bloqueActual.setTamanioTotal(tamanioActual);
            bloqueActual.setValorTotal(valorActual);
            
            List<Bloque> resultado = new ArrayList<>();
            resultado.add(bloqueActual); // Devolvemos el bloque actual con las transacciones válidas
            return resultado;
        }

        // Si no se puede construir el bloque con las transacciones restantes, retornamos una lista vacía
        return new ArrayList<>();
    }

    private boolean tamañoBloque(List<Transaccion> actBloque, int maxTamanioBloque) {
        int sumaTotal = 0;
        for (Transaccion transaccion : actBloque) {
            sumaTotal += transaccion.getTamanio();
        }
        return sumaTotal <= maxTamanioBloque;
    }

    private boolean validacionSatoshis(List<Transaccion> actBloque, int maxValorBloque) {
        int sumaTotal = 0;
        for (Transaccion transaccion : actBloque) {
            sumaTotal += transaccion.getValor();
        }
        return sumaTotal <= maxValorBloque;
    }

    private boolean validacionPow(List<Transaccion> actBloque) {
        int sumaTotal = 0;
        for (Transaccion transaccion : actBloque) {
            sumaTotal += transaccion.getValor();
        }
        return sumaTotal % 10 == 0;
    }

    private void actualizarTransaccionesRestantes(List<Transaccion> transacciones, List<Bloque> bloque) {
        for (Bloque b : bloque) { // Iteramos sobre los bloques
            for (Transaccion transaccionAc : b.getTransacciones()) { // Iteramos sobre las transacciones dentro de cada bloque
                transacciones.remove(transaccionAc);
            }
        }
    }
}
