package  com.boleteria.model;

import java.util.List;

import javax.xml.crypto.Data;

/*TODO: 

funcion GetPrecio() para leer  dato
extraer del mismo documento csv
comer papitas ricas


*/
public class Asiento {
    private String idAsiento;
    private double precio;
    private String estado;

    public Asiento(String idAsiento, double precio, String estado){

        this.idAsiento = idAsiento;
        this.precio = precio;
        this.estado = estado;


    }

    public static List<Asiento> cargarAsientos(){
        String rutaArchivo = "laboratiorio2-boletas/data/asientos.csv";
        String separador = ";";
    }
}
