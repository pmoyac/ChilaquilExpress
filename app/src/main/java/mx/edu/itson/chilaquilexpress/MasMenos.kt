package mx.edu.itson.chilaquilexpress

class MasMenos(val valor: String, var cantidad: Int = 0) {
    fun aumentaCantidad() {
        cantidad++
    }

    fun disminuyeCantidad() {
        if (cantidad > 0){
            cantidad--
        }
    }
}