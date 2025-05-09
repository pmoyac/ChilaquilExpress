package mx.edu.itson.chilaquilexpress

import java.io.Serializable

data class Bebida(
    val id: String="",
    val nombre: String="",
    val costo: Int
) : Serializable
