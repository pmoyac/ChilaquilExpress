package mx.edu.itson.chilaquilexpress

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Orden(
    val id:Int,
    val identificador: String,
    val tipo: TipoOrden,
    val chilaquiles: List<Chilaquil>,
    val bebidas: List<Bebida>,
    val fecha: String = obtenerFechaActual(),
    var costoTotal: Double
){
    fun calcularCostoTotal() {
        val costoBebidas = bebidas.sumOf { it.precio }
        val costoChilaquiles = chilaquiles.sumOf { it.costoTotal }
        costoTotal = costoBebidas + costoChilaquiles
    }
}

fun obtenerFechaActual(): String {
    val formato = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
    return formato.format(Date())
}
