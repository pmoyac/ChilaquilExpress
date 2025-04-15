package mx.edu.itson.chilaquilexpress

data class Chilaquil(
    var id: Int,
    val tipoSalsa: TipoSalsa,
    val precio: Double = 50.00,
    val proteinas: List<Int>,
    val toppings: List<Int>,
    var costoTotal: Double
){
    fun getNombre(): String = "Chilaquiles ${tipoSalsa.name.lowercase().capitalize()}"

    fun calcularCostoTotal(proteinaLista: List<Proteina>) {
        val costoProteinas = proteinas.mapNotNull { id ->
            proteinaLista.find { it.id == id }?.costo
        }.sum()
        costoTotal = precio + costoProteinas
    }
}
