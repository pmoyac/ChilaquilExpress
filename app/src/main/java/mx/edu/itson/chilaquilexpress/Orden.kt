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
    var costoTotal: Int=0,
    var pagado: Boolean = false
){
    fun calcularCostoTotal() {
        val costoBebidas = bebidas.sumOf { it.costo }
        val costoChilaquiles = chilaquiles.sumOf { it.costoTotal }
        costoTotal = costoBebidas + costoChilaquiles
    }
}

fun obtenerFechaActual(): String {
    val formato = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return formato.format(Date())
}
