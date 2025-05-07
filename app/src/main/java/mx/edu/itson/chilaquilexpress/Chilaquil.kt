package mx.edu.itson.chilaquilexpress

data class Chilaquil(
    var id: Int,
    val tipoSalsa: TipoSalsa,
    val costo: Int = 50,
    val proteinas: List<Int>,
    val toppings: List<Int>,
    var costoTotal: Int
){
    fun getNombre(): String = "Chilaquiles ${tipoSalsa.name.lowercase().capitalize()}"

    fun calcularCostoTotal(proteinaLista: List<Proteina>) {
        val costoProteinas = proteinas.mapNotNull { id ->
            proteinaLista.find { it.id == id }?.costo
        }.sum()
        costoTotal = costo + costoProteinas
    }
}
