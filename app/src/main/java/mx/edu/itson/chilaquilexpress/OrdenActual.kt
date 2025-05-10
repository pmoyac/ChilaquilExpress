package mx.edu.itson.chilaquilexpress

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class OrdenActual : AppCompatActivity() {

    private var boton: Int = 0
    private var identificador: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orden_actual)

        boton = intent.getIntExtra("boton", 0)
        identificador = intent.getStringExtra("identificador") ?: ""


        val productos = OrdenManager.productosEnOrden
        val layoutLista = findViewById<LinearLayout>(R.id.listaDinamica)
        layoutLista.removeAllViews()

        for ((index, prod) in productos.withIndex()) {
            val vista = layoutInflater.inflate(R.layout.item_producto, layoutLista, false)

            val nombre = vista.findViewById<TextView>(R.id.itemNombre)
            val precio = vista.findViewById<TextView>(R.id.itemPrecio)
            val imagen = vista.findViewById<ImageView>(R.id.itemImagen)
            val toppingsProteinas = vista.findViewById<TextView>(R.id.toppingsProteinas)
            val txtCantidad = vista.findViewById<TextView>(R.id.txtCantidad)
            val btnMas = vista.findViewById<Button>(R.id.btnMas)
            val btnMenos = vista.findViewById<Button>(R.id.btnMenos)

            nombre.text = prod.nombre
            precio.text = "Total: $${prod.costo * prod.cantidad}"
            imagen.setImageResource(prod.imagen)
            txtCantidad.text = prod.cantidad.toString()

            if (prod.categoria == "Chilaquiles") {
                val proteinasTexto = if (prod.proteinas.isNotEmpty()) prod.proteinas.joinToString(", ") else "Ninguna"
                val toppingsTexto = if (prod.toppings.isNotEmpty()) prod.toppings.joinToString(", ") else "Ninguno"
                toppingsProteinas.text = "Proteínas: $proteinasTexto | Toppings: $toppingsTexto"
            } else {
                toppingsProteinas.visibility = View.GONE
            }

            btnMas.setOnClickListener {
                OrdenManager.incrementarCantidad(index)
                txtCantidad.text = OrdenManager.productosEnOrden[index].cantidad.toString()
                precio.text = "Total: $${prod.costo * OrdenManager.productosEnOrden[index].cantidad}"
                actualizarTotales()
            }

            btnMenos.setOnClickListener {
                val mantener = OrdenManager.decrementarCantidad(index)
                if (mantener) {
                    txtCantidad.text = OrdenManager.productosEnOrden[index].cantidad.toString()
                    precio.text = "Total: $${prod.costo * OrdenManager.productosEnOrden[index].cantidad}"
                } else {
                    layoutLista.removeView(vista)
                }
                actualizarTotales()
            }

            layoutLista.addView(vista)
        }

        actualizarTotales()

        val btnFinalizar = findViewById<Button>(R.id.btnFinalizar)
        val btnAgregar = findViewById<Button>(R.id.btnAgregarProducto)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        btnFinalizar.setOnClickListener {
            if (!OrdenManager.esOrdenValida()) {
                Toast.makeText(this, "La orden no puede estar vacía", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (boton == 1){
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirmar orden")
                builder.setMessage("¿Deseas hacer otra orden para esta mesa?")

                builder.setPositiveButton("Sí") { _, _ ->
                    val db = FirebaseFirestore.getInstance()
                    val tipoOrden = if (boton == 1) TipoOrden.MESA else TipoOrden.PERSONA
                    val id = System.currentTimeMillis().toString()
                    val orden = hashMapOf(
                        "id" to id,
                        "tipo" to tipoOrden,
                        "identificador" to if (tipoOrden == TipoOrden.MESA) "Mesa #$identificador" else identificador,
                        "fecha" to Timestamp.now(),
                        "costoTotal" to OrdenManager.calcularTotal(),
                        "bebida" to OrdenManager.obtenerBebidas(),
                        "chilaquiles" to OrdenManager.obtenerChilaquilesMapeados(),
                        "pagado" to false
                    )

                    db.collection("ordenes").add(orden)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Orden enviada correctamente", Toast.LENGTH_SHORT).show()
                            OrdenManager.limpiarOrden()
                            val intent = Intent(this, OrdenActual::class.java)
                            intent.putExtra("boton", boton)
                            intent.putExtra("identificador", identificador)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al guardar orden", Toast.LENGTH_SHORT).show()
                        }
                }

                builder.setNegativeButton("No") { dialog, _ ->
                    val db = FirebaseFirestore.getInstance()
                    val tipoOrden = if (boton == 1) TipoOrden.MESA else TipoOrden.PERSONA
                    val id = System.currentTimeMillis().toString()
                    val orden = hashMapOf(
                        "id" to id,
                        "tipo" to tipoOrden,
                        "identificador" to if (tipoOrden == TipoOrden.MESA) "Mesa #$identificador" else identificador,
                        "fecha" to Timestamp.now(),
                        "costoTotal" to OrdenManager.calcularTotal(),
                        "bebida" to OrdenManager.obtenerBebidas(),
                        "chilaquiles" to OrdenManager.obtenerChilaquilesMapeados(),
                        "pagado" to false
                    )

                    db.collection("ordenes").add(orden)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Orden enviada correctamente", Toast.LENGTH_SHORT).show()
                            OrdenManager.limpiarOrden()
                            val intent = Intent(this, TipoOrdenActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al guardar orden", Toast.LENGTH_SHORT).show()
                        }
                }

                builder.setNeutralButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                builder.create().show()
            }else{
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirmar orden")
                builder.setMessage("¿Deseas finalizar la orden?")

                builder.setPositiveButton("Sí") { _, _ ->
                    val db = FirebaseFirestore.getInstance()
                    val tipoOrden = if (boton == 1) TipoOrden.MESA else TipoOrden.PERSONA
                    val id = System.currentTimeMillis().toString()
                    val orden = hashMapOf(
                        "id" to id,
                        "tipo" to tipoOrden,
                        "identificador" to if (tipoOrden == TipoOrden.MESA) "Mesa #$identificador" else identificador,
                        "fecha" to Timestamp.now(),
                        "costoTotal" to OrdenManager.calcularTotal(),
                        "bebida" to OrdenManager.obtenerBebidas(),
                        "chilaquiles" to OrdenManager.obtenerChilaquilesMapeados(),
                        "pagado" to false
                    )

                    db.collection("ordenes").add(orden)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Orden enviada correctamente", Toast.LENGTH_SHORT).show()
                            OrdenManager.limpiarOrden()
                            val intent = Intent(this, TipoOrdenActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al guardar orden", Toast.LENGTH_SHORT).show()
                        }
                }

                builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                builder.create().show()
            }


        }

        btnAgregar.setOnClickListener {
            val intent = Intent(this, ProductosActivity::class.java)
            intent.putExtra("boton", boton)
            intent.putExtra("identificador", identificador)
            startActivity(intent)
        }

        btnCancelar.setOnClickListener {
            OrdenManager.limpiarOrden()
            val intent = Intent(this, TipoOrdenActivity::class.java)
            startActivity(intent)
        }
    }

    private fun actualizarTotales() {
        val txtTotal = findViewById<TextView>(R.id.txtTotal)
        val txtTotalIva = findViewById<TextView>(R.id.txtTotalIva)

        val total = OrdenManager.calcularTotal().toDouble()
        val totalIva = total * 1.16

        txtTotal.text = "Sub total: $${"%.2f".format(total)}"
        txtTotalIva.text = "Total con IVA (16%): $${"%.2f".format(totalIva)}"
    }
}
