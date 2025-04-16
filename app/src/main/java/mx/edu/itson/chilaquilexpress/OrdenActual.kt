package mx.edu.itson.chilaquilexpress

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class OrdenActual : AppCompatActivity() {
    private lateinit var adaptadorProductos: AdaptadorOrder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_orden_actual)
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
                OrdenManager.limpiarOrden()
                Toast.makeText(this, "Orden finalizada con éxito", Toast.LENGTH_LONG).show()

                val intent = Intent(this, TipoOrdenActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                // No hacer nada si el usuario cancela
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        btnAgregar.setOnClickListener {
            var intent: Intent = Intent(this, ProductosActivity::class.java)
            startActivity(intent)
        }
    }

    private inner class AdaptadorOrder(val contexto: Context, val productos: List<Producto>) : BaseAdapter() {
        override fun getCount(): Int {
            return productos.size
        }

        override fun getItem(position: Int): Any {
            return productos[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

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
            precio.text = "Total: $${prod.precio * prod.cantidad}"
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
                    // Si el producto fue eliminado, actualizar el layout
                    notifyDataSetChanged()
                } else {
                    txtCantidad.text = prod.cantidad.toString()
                    precio.text = "Precio: $${prod.precio * prod.cantidad}"
                }
            }

            return vista
        }
    }
}
