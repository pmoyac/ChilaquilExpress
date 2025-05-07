package mx.edu.itson.chilaquilexpress

import java.io.Serializable

data class Producto(
    var imagen: Int,
    var nombre: String,
    var descripcion: String,
    var costo: Int,
    var categoria: String,
    var cantidad: Int = 1,
    var toppings: List<String> = emptyList(),
    var proteinas: List<String> = emptyList()
) : Serializable
