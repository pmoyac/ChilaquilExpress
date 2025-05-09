package mx.edu.itson.chilaquilexpress

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class OrdenAPagar : AppCompatActivity() {
    private var identificador: String = ""
    private var boton: Int = 0
    private var orden: Orden = Orden()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        identificador = intent.getStringExtra("identificador") ?: ""
        boton = intent.getIntExtra("boton", 0)
        orden = intent.getSerializableExtra("orden") as Orden
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

        val total = OrdenManager.calcularTotal().toDouble() + orden.costoTotal
        val totalIva = total * 1.16

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

        btnAgregar.setOnClickListener {
            val intent = Intent(this, ProductosActivity::class.java)
            intent.putExtra("identificador", identificador)
            intent.putExtra("boton", boton)
            intent.putExtra("orden", orden)
            startActivity(intent)
        }

        btnPagar.setOnClickListener(){
            OrdenManager.limpiarOrden()
            val intent: Intent = Intent(this, TipoOrdenActivity::class.java)
            startActivity(intent)
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