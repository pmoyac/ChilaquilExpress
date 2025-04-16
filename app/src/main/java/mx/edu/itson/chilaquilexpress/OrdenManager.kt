package mx.edu.itson.chilaquilexpress

object OrdenManager {
    val productosEnOrden = mutableListOf<Producto>()

    // agregar un producto a la orden
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

    fun getTotal(): Double {
        return productosEnOrden.sumOf { it.precio * it.cantidad }
    }

    // incrementar la cantidad de un producto
    fun incrementarCantidad(position: Int) {
        if (position >= 0 && position < productosEnOrden.size) {
            productosEnOrden[position].cantidad++
        }
    }

    // decrementar la cantidad de un producto
    fun decrementarCantidad(position: Int): Boolean {
        if (position >= 0 && position < productosEnOrden.size) {
            val producto = productosEnOrden[position]
            if (producto.cantidad > 1) {
                producto.cantidad--
                return true
            } else {
                // Si la cantidad llega a 0 eliminamos el producto
                productosEnOrden.removeAt(position)
                return false
            }
        }
        return true
    }

    fun limpiarOrden() {
        productosEnOrden.clear()
    }
}