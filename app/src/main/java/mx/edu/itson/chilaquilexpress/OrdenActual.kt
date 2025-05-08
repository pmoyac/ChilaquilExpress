package mx.edu.itson.chilaquilexpress

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class OrdenActual : AppCompatActivity() {
    var boton: Int =0
    private lateinit var txtBoton: TextView
    private lateinit var adaptadorProductos: AdaptadorOrder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orden_actual)
        boton = intent.getIntExtra("boton", 0)
        txtBoton = findViewById(R.id.txtBoton)
        txtBoton.setText("${txtBoton.text} $boton")


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val productos = OrdenManager.productosEnOrden

        val listProductos: ListView = findViewById(R.id.listProductos)
        adaptadorProductos = AdaptadorOrder(this, productos)
        listProductos.adapter = adaptadorProductos

        val btnFinalizar: Button = findViewById(R.id.btnFinalizar)
        val btnAgregar: Button = findViewById(R.id.btnAgregarProducto)

        btnFinalizar.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmar orden")
            builder.setMessage("¿Estás seguro de que deseas finalizar la orden?")

            builder.setPositiveButton("Sí") { dialog, which ->
                val db = FirebaseFirestore.getInstance()

                val orden = hashMapOf(
                    "id" to System.currentTimeMillis().toString(),
                    "tipo" to "persona", // o "mesa", según tu lógica
                    "identificador" to "mesa_1", // puedes cambiarlo dinámicamente
                    "fecha" to Timestamp.now(),
                    "costoTotal" to OrdenManager.calcularTotal(),
                    "bebida" to OrdenManager.obtenerBebida()?.nombre,
                    "chilaquiles" to OrdenManager.obtenerChilaquilesMapeado()
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

        btnAgregar.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }
    }

    private inner class AdaptadorOrder(val contexto: Context, val productos: List<Producto>) : BaseAdapter() {
        override fun getCount(): Int = productos.size

        override fun getItem(position: Int): Any = productos[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val prod = productos[position]
            val inflador = LayoutInflater.from(contexto)
            val vista = inflador.inflate(R.layout.item_producto, null)

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
                OrdenManager.incrementarCantidad(position)
                notifyDataSetChanged()
            }

            btnMenos.setOnClickListener {
                val mantenerProducto = OrdenManager.decrementarCantidad(position)
                if (!mantenerProducto) {
                    notifyDataSetChanged()
                } else {
                    txtCantidad.text = prod.cantidad.toString()
                    precio.text = "Precio: $${prod.costo * prod.cantidad}"
                }
            }

            return vista
        }
    }
}
