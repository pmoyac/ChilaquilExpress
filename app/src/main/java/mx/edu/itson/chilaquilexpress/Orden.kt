package mx.edu.itson.chilaquilexpress

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Orden(
    val id:String,
    val identificador: String,
    val tipo: TipoOrden,
    val chilaquiles: List<Chilaquil>,
    val bebidas: List<Producto>,
    val fecha: String = obtenerFechaActual(),
    var costoTotal: Float=0.0F,
    var pagado: Boolean = false
): Serializable{
    constructor() : this("", "", TipoOrden.PERSONA, emptyList(), emptyList(), "", 0.0F, false)

    fun calcularCostoTotal() {
        val costoBebidas = bebidas.sumOf { it.costo }
        val costoChilaquiles = chilaquiles.sumOf { it.costoTotal }
        costoTotal = (costoBebidas + costoChilaquiles).toFloat()
    }
}

fun obtenerFechaActual(): String {
    val formato = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return formato.format(Date())
}
