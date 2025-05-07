package mx.edu.itson.chilaquilexpress

object PreciosProteinas{
    val precios = mapOf(
        "Pollo" to 10,
        "Frijoles" to 10,
        "Chicharrón" to 15,
        "Chorizo" to 15,
        "Chilorio" to 15,
        "Cochinita" to 15
    )
    fun getPrecio(nombreProteina: String): Int {
        return precios[nombreProteina] ?: 0
    }
}