package mx.edu.itson.chilaquilexpress

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class OrdenAPagar : AppCompatActivity() {
    private var identificador: String = ""
    private var boton: Int = 0
    private var orden: Orden = Orden()
    private var ordenes: List<Orden> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        identificador = intent.getStringExtra("identificador") ?: ""
        boton = intent.getIntExtra("boton", 0)
        orden = intent.getSerializableExtra("orden") as Orden
        if(identificador == "completa") ordenes = intent.getSerializableExtra("ordenes") as List<Orden>
        setContentView(R.layout.activity_orden_apagar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val productos = OrdenManager.productosEnOrden
        val layoutLista = findViewById<LinearLayout>(R.id.listaDinamica)
        layoutLista.removeAllViews()

        val txtTotal = findViewById<TextView>(R.id.txtTotal)
        val txtTotalIva = findViewById<TextView>(R.id.txtTotalIva)

        var total: Double = 0.0
        var totalIva: Double = 0.0

        if (identificador == "completa"){
            for (or in ordenes){
                total += OrdenManager.calcularTotal().toDouble() + or.costoTotal
            }
        }
        else{
            total = OrdenManager.calcularTotal().toDouble() + orden.costoTotal
        }
        totalIva = total * 1.16
        txtTotal.text = "Sub total: $${"%.2f".format(total)}"
        txtTotalIva.text = "Total con IVA (16%): $${"%.2f".format(totalIva)}"

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
                val proteinasTexto =
                    if (prod.proteinas.isNotEmpty()) prod.proteinas.joinToString(", ") else "Ninguna"
                val toppingsTexto =
                    if (prod.toppings.isNotEmpty()) prod.toppings.joinToString(", ") else "Ninguno"
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


        val btnAgregar: Button = findViewById(R.id.btnAgregarProducto)
        val btnPagar: Button = findViewById(R.id.btnPagar)
        val btnFinalizar: Button = findViewById(R.id.btnEditarOrden)

        btnFinalizar.setOnClickListener(){
            if(identificador == "completa"){
                Toast.makeText(this, "Solo Puedes pagar la cuenta", Toast.LENGTH_SHORT).show()
            }
            else{
                if (OrdenManager.productosEnOrden.isEmpty()){
                    Toast.makeText(this, "Agrega otro producto para esto", Toast.LENGTH_SHORT).show()
                }else{
                    val db = FirebaseFirestore.getInstance()
                    var chilaNuevos = orden.chilaquiles + OrdenManager.obtenerChilaquilesMapeados()
                    val bebidasMapeadas = OrdenManager.obtenerBebidas().map { bebida ->
                        mapOf(
                            "nombre" to bebida.nombre,
                            "descripcion" to bebida.descripcion,
                            "costo" to bebida.costo,
                            "imagen" to bebida.imagen,
                            "categoria" to bebida.categoria,
                            "cantidad" to bebida.cantidad
                        )
                    }
                    var bebidasNuevo = orden.bebidas + bebidasMapeadas

                    db.collection("ordenes")
                        .whereEqualTo("id", orden.id)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val document = querySnapshot.documents[0]
                                val ordenRef = document.reference

                                ordenRef.update(
                                    mapOf(
                                        "bebida" to bebidasNuevo,
                                        "chilaquiles" to chilaNuevos,
                                        "costoTotal" to totalIva
                                    )
                                ).addOnSuccessListener {
                                    Toast.makeText(this, "Orden actualizada con éxito", Toast.LENGTH_SHORT).show()
                                    OrdenManager.limpiarOrden()
                                    val intent = Intent(this, VerOrdenes::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }.addOnFailureListener {
                                    Toast.makeText(this, "Error al actualizar la orden", Toast.LENGTH_SHORT).show()
                                }
                            }

                            else {
                                Toast.makeText(this, "Orden no encontrada", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al buscar la orden", Toast.LENGTH_SHORT).show()
                        }

                }

            }
        }

        btnAgregar.setOnClickListener {
            if (identificador == "completa"){
                Toast.makeText(this, "Solo se pueden agregar producto cuando es cuenta Individual de la mesa", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(this, ProductosActivity::class.java)
                intent.putExtra("identificador", identificador)
                intent.putExtra("boton", boton)
                intent.putExtra("orden", orden)
                startActivity(intent)
            }

        }

        btnPagar.setOnClickListener {
            if (identificador == "completa"){
                val db = FirebaseFirestore.getInstance()

                db.collection("ordenes")
                    .whereEqualTo("identificador", orden.identificador)
                    .whereEqualTo("pagado", false)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val batch = db.batch()

                            for (document in querySnapshot.documents) {
                                val docRef = db.collection("ordenes").document(document.id)
                                batch.update(docRef, "pagado", true)
                            }

                            batch.commit()
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Órdenes marcadas como pagadas", Toast.LENGTH_SHORT).show()

                                    OrdenManager.limpiarOrden()
                                    val intent = Intent(this, TipoOrdenActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error al actualizar las órdenes", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "No se encontraron órdenes pendientes", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al buscar las órdenes", Toast.LENGTH_SHORT).show()
                    }

            }else{
                val db = FirebaseFirestore.getInstance()
                val idOrden = orden.id

                db.collection("ordenes")
                    .whereEqualTo("id", idOrden)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val document = querySnapshot.documents[0]
                            val docRef = db.collection("ordenes").document(document.id)

                            docRef.update("pagado", true)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Orden marcada como pagada", Toast.LENGTH_SHORT).show()

                                    OrdenManager.limpiarOrden()
                                    val intent = Intent(this, TipoOrdenActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error al actualizar la orden", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "No se encontró la orden", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al buscar la orden", Toast.LENGTH_SHORT).show()
                    }
            }

        }


    }
    fun actualizarTotales() {
        val txtTotal = findViewById<TextView>(R.id.txtTotal)
        val txtTotalIva = findViewById<TextView>(R.id.txtTotalIva)

        val total = OrdenManager.calcularTotal().toDouble() + orden.costoTotal
        val totalIva = total * 1.16

        txtTotal.text = "Sub total: $${"%.2f".format(total)}"
        txtTotalIva.text = "Total con IVA (16%): $${"%.2f".format(totalIva)}"
    }
}