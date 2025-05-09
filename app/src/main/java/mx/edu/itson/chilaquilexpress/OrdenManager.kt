package mx.edu.itson.chilaquilexpress

object OrdenManager {

    val productosEnOrden = mutableListOf<Producto>()

    fun agregarProducto(producto: Producto) {
        // Verifica si ya existe el producto con mismo nombre y categoría
        val existente = productosEnOrden.find { it.nombre == producto.nombre && it.categoria == producto.categoria }

        if (existente != null) {
            existente.cantidad += 1
        } else {
            productosEnOrden.add(producto)
        }
    }

    fun incrementarCantidad(pos: Int) {
        productosEnOrden[pos].cantidad++
    }

    fun decrementarCantidad(pos: Int): Boolean {
        val producto = productosEnOrden[pos]
        producto.cantidad--
        return if (producto.cantidad <= 0) {
            productosEnOrden.removeAt(pos)
            false
        } else {
            true
        }
    }

    fun limpiarOrden() {
        productosEnOrden.clear()
    }

    fun calcularTotal(): Int {
        return productosEnOrden.sumOf { it.costo * it.cantidad }
    }

    fun esOrdenValida(): Boolean {
        return productosEnOrden.isNotEmpty()
    }

    fun obtenerChilaquiles(): List<Producto> {
        return productosEnOrden.filter { it.categoria.equals("Chilaquiles", ignoreCase = true) }
    }

    fun obtenerBebidas(): List<Producto> {
        return productosEnOrden.filter { it.categoria.equals("Bebidas", ignoreCase = true) }
    }

    fun obtenerChilaquilesMapeados(): List<Map<String, Any>> {
        return obtenerChilaquiles().map { chilaquil ->
            mapOf(
                "tipo" to if (chilaquil.nombre.contains("Verde", ignoreCase = true)) "verde" else "roja",
                "precioBase" to chilaquil.costo,
                "proteinas" to chilaquil.proteinas,
                "toppings" to chilaquil.toppings,
                "costoTotal" to chilaquil.costo * chilaquil.cantidad
            )
        }
    }
}
