package mx.edu.itson.chilaquilexpress

object PreciosProteinas{
    val precios = mapOf(
        "Pollo" to 10.00,
        "Frijoles" to 10.00,
        "Chicharrón" to 15.00,
        "Chorizo" to 15.00,
        "Chilorio" to 15.00,
        "Cochinita" to 15.0
    )
    fun getPrecio(nombreProteina: String): Double {
        return precios[nombreProteina] ?: 0.0
    }
}