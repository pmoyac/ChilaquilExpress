package mx.edu.itson.chilaquilexpress

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class ProductosActivity : AppCompatActivity() {
    var menu: ArrayList<Producto> = ArrayList()
    lateinit var listChilaquiles: ListView
    lateinit var listBebidas: ListView
    lateinit var adaptadorChilaquiles: AdaptadorProductos
    lateinit var adaptadorBebidas: AdaptadorProductos
    var boton: Int = 0
    var identificador: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        boton = intent.getIntExtra("boton", 0)
        identificador = intent.getStringExtra("identificador") ?: ""
        setContentView(R.layout.activity_productos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listChilaquiles = findViewById(R.id.listChilaquiles)
        listBebidas = findViewById(R.id.listBebidas)

        cargarProductosLocales()
        cargarBebidasDesdeFirestore()

        listChilaquiles.setOnItemClickListener { _, _, position, _ ->
            val chilaquilSeleccionado = adaptadorChilaquiles.productos[position]
            val intent = Intent(this, MenuChilaquil::class.java)
            intent.putExtra("nombre", chilaquilSeleccionado.nombre)
            intent.putExtra("costo", chilaquilSeleccionado.costo)
            intent.putExtra("boton",boton)
            intent.putExtra("identificador", identificador)
            startActivity(intent)
        }

        listBebidas.setOnItemClickListener { _, _, position, _ ->
            val bebidaSeleccionada = adaptadorBebidas.productos[position]
            val producto = Producto(
                imagen = bebidaSeleccionada.imagen,
                nombre = bebidaSeleccionada.nombre,
                descripcion = bebidaSeleccionada.descripcion,
                costo = bebidaSeleccionada.costo,
                categoria = bebidaSeleccionada.categoria,
                cantidad = 1
            )
            OrdenManager.agregarProducto(producto)
            var intent: Intent = Intent(this, OrdenActual::class.java)
            intent.putExtra("boton",boton)
            intent.putExtra("identificador", identificador)
            startActivity(intent)
        }
    }

    private fun cargarProductosLocales() {
        val chilaquiles = arrayListOf(
            Producto(
                R.drawable.chilaquiles, "Chilaquiles Salsa Verde",
                "Chilaquiles en salsa verde con Proteínas y Toppings a elegir", 50, "Chilaquiles"
            ),
            Producto(
                R.drawable.chilaquiles, "Chilaquiles Salsa Roja",
                "Chilaquiles en salsa roja con Proteínas y Toppings a elegir", 50, "Chilaquiles"
            )
        )
        menu.addAll(chilaquiles)

        adaptadorChilaquiles = AdaptadorProductos(this, chilaquiles)
        listChilaquiles.adapter = adaptadorChilaquiles
    }

    private fun cargarBebidasDesdeFirestore() {
        adaptadorBebidas = AdaptadorProductos(this, ArrayList())
        listBebidas.adapter = adaptadorBebidas

        val db = FirebaseFirestore.getInstance()
        db.collection("bebidas").get().addOnSuccessListener { result ->
            val bebidasFirestore = result.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: return@mapNotNull null
                val nombre = doc.getString("nombre") ?: return@mapNotNull null
                val costo = doc.getLong("costo")?.toInt() ?: return@mapNotNull null

                val imageResId = getImageResourceByName(id)
                Producto(
                    imagen = imageResId,
                    nombre = nombre,
                    descripcion = "Bebida de $nombre",
                    costo = costo,
                    categoria = "Bebidas"
                )
            }

            // Actualizar datos del adaptador
            adaptadorBebidas.productos.clear()
            adaptadorBebidas.productos.addAll(bebidasFirestore)
            adaptadorBebidas.notifyDataSetChanged()
        }
    }

    private fun getImageResourceByName(name: String): Int {
        return resources.getIdentifier(name, "drawable", packageName)
    }

    class AdaptadorProductos(
        private val contexto: Context,
        val productos: ArrayList<Producto>
    ) : BaseAdapter() {

        override fun getCount(): Int = productos.size

        override fun getItem(position: Int): Any = productos[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val producto = productos[position]
            val vista = LayoutInflater.from(contexto).inflate(R.layout.producto_view, null)
            val imagen = vista.findViewById<ImageView>(R.id.producto_img)
            val nombre = vista.findViewById<TextView>(R.id.producto_nombre)
            val desc = vista.findViewById<TextView>(R.id.producto_desc)
            val precio = vista.findViewById<TextView>(R.id.producto_precio)

            imagen.setImageResource(producto.imagen)
            nombre.text = producto.nombre
            desc.text = producto.descripcion
            precio.text = "$${producto.costo}"
            return vista
            }
        }
}