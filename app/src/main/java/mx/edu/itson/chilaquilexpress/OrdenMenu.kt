package mx.edu.itson.chilaquilexpress

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class OrdenMenu : AppCompatActivity() {
    var orden: ArrayList<Producto> = ArrayList<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_orden_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val productos = orden
        val listProductos: ListView = findViewById(R.id.listProductos)
        val adaptadorProductos = AdaptadorOrder(this, productos)
        listProductos.adapter = adaptadorProductos

        val btnFinalizar: Button = findViewById(R.id.btnFinalizar)
        val btnAgregar: Button = findViewById(R.id.btnAgregarProducto)

        btnFinalizar.setOnClickListener {
            var intent: Intent = Intent(this, TipoOrdenActivity::class.java)
            startActivity(intent)
        }

        btnAgregar.setOnClickListener {
            var intent: Intent = Intent(this, ProductosActivity::class.java)
            startActivity(intent)
        }

    }
    private class AdaptadorOrder : BaseAdapter{
        var productos = ArrayList<Producto>()
        var contexto: Context?=null

        constructor(contexto : Context, producto : ArrayList<Producto>){
            this.productos = producto
            this.contexto = contexto
        }

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
            var prod = productos [position]
            var inflador = LayoutInflater.from(contexto)
            var vista = inflador.inflate(R.layout.producto_view, null)

            var nombre = vista.findViewById(R.id.producto_nombre) as TextView
            var desc = vista.findViewById(R.id.producto_desc) as TextView
            var precio = vista.findViewById(R.id.producto_precio) as TextView

            nombre.setText(prod.nombre)
            desc.setText(prod.descripcion)
            precio.setText("$${prod.precio}")
            return vista
        }
    }
}
