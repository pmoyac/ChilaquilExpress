package mx.edu.itson.chilaquilexpress

object OrdenManager {
    val productosEnOrden = mutableListOf<Producto>()

    // Agregar un producto a la orden
    fun agregarProducto(producto: Producto) {
        val productoExistente = productosEnOrden.find {
            it.nombre == producto.nombre &&
                    it.proteinas == producto.proteinas &&
                    it.toppings == producto.toppings
        }

        if (productoExistente != null) {
            productoExistente.cantidad++
        } else {
            productosEnOrden.add(producto)
        }
    }

    // Obtener el total de la orden
    fun getTotal(): Int {
        return productosEnOrden.sumOf { it.costo * it.cantidad }
    }

    // Incrementar la cantidad de un producto
    fun incrementarCantidad(position: Int) {
        if (position >= 0 && position < productosEnOrden.size) {
            productosEnOrden[position].cantidad++
        }
    }

    // Decrementar la cantidad de un producto
    fun decrementarCantidad(position: Int): Boolean {
        if (position >= 0 && position < productosEnOrden.size) {
            val producto = productosEnOrden[position]
            if (producto.cantidad > 1) {
                producto.cantidad--
                return true
            } else {
                // Si la cantidad llega a 0, eliminamos el producto
                productosEnOrden.removeAt(position)
                return false
            }
        }
        return true
    }

    // Limpiar la orden actual
    fun limpiarOrden() {
        productosEnOrden.clear()
    }

    // Obtener la bebida actual
    fun obtenerBebida(): Producto? {
        return productosEnOrden.find { it.categoria == "Bebidas" }
    }

    // Obtener los datos del chilaquil en formato compatible con Firestore
    fun obtenerChilaquilesMapeado(): Map<String, Any> {
        val chilaquil = productosEnOrden.find { it.categoria == "Chilaquiles" } ?: return emptyMap()

        return mapOf(
            "tipo" to if (chilaquil.nombre.contains("Verde", ignoreCase = true)) "verde" else "rojo",
            "proteinas" to chilaquil.proteinas,
            "toppings" to chilaquil.toppings,
            "precioBase" to chilaquil.costo,
            "costoTotal" to chilaquil.costo * chilaquil.cantidad
        )
    }

    // Calcular el total de la orden
    fun calcularTotal(): Int {
        return productosEnOrden.sumOf { it.costo * it.cantidad }
    }
}
