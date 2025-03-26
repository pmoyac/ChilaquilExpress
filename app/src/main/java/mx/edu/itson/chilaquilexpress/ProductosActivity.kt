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

class ProductosActivity : AppCompatActivity() {
    var menu: ArrayList<Producto> = ArrayList<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_productos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        agregarProductos()
        var listChilaquiles: ListView = findViewById(R.id.listChilaquiles) as ListView
        var listBebidas: ListView = findViewById(R.id.listBebidas) as ListView

        val chilaquiles = menu.filter { it.categoria == "Chilaquiles" }
        val adaptadorChilaquiles = AdaptadorProductos(this, ArrayList(chilaquiles))
        listChilaquiles.adapter = adaptadorChilaquiles

        val drinks = menu.filter { it.categoria == "Bebidas" }
        val adaptadorDrinks = AdaptadorProductos(this, ArrayList(drinks))
        listBebidas.adapter = adaptadorDrinks

        listChilaquiles.setOnItemClickListener { parent, view, position, id ->
            val chilaquilSeleccionado = chilaquiles[position]

            val intent = Intent(this, MenuChilaquil::class.java)

            // Pasar datos del producto seleccionado
            intent.putExtra("nombre", chilaquilSeleccionado.nombre)
            intent.putExtra("precio", chilaquilSeleccionado.precio)

            startActivity(intent)
        }
        listBebidas.setOnItemClickListener{ parentActivityIntent, view, position, id ->
            val bebidaSeleccionada = drinks[position]

            val intent = Intent(this, OrdenMenu::class.java)
            intent.putExtra("nombre", bebidaSeleccionada.nombre)
            intent.putExtra("precio", bebidaSeleccionada.precio)

            startActivity(intent)
        }

        val bebidas = menu.filter { it.categoria == "Bebidas" }
        val adaptadorBebidas = AdaptadorProductos(this, ArrayList(bebidas))
        listBebidas.adapter = adaptadorBebidas
    }

    fun agregarProductos(){
        menu.add(Producto(R.drawable.chilaquiles, "Chilaquiles Salsa Verde",
            "Chilaquiles en salsa verde con Proteínas y Toppings a elegir", 50.00, "Chilaquiles"))
        menu.add(Producto(R.drawable.chilaquiles, "Chilaquiles Salsa Roja"
            , "Chilaquiles en salsa roja con Proteínas y Toppings a elegir", 50.00, "Chilaquiles"))

        menu.add(Producto(R.drawable.jamaica, "Jamaica",
            "Agua natural de jamaica 600 ml", 20.00, "Bebidas"))
        menu.add(Producto(R.drawable.pina, "Piña",
            "Agua natural de piña 600 ml", 20.00, "Bebidas"))
        menu.add(Producto(R.drawable.cebada, "Cebada",
            "Agua natural de cebada 600 ml", 20.00, "Bebidas"))
        menu.add(Producto(R.drawable.horchata, "Horchata",
            "Agua natural de horchata 600 ml", 20.00, "Bebidas"))
        menu.add(Producto(R.drawable.cocacola, "Cola-Cola",
            "Refreso Coca-Cola 600 ml", 20.00, "Bebidas"))
    }

    private class AdaptadorProductos : BaseAdapter{
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

            var imagen = vista.findViewById(R.id.producto_img) as ImageView
            var nombre = vista.findViewById(R.id.producto_nombre) as TextView
            var desc = vista.findViewById(R.id.producto_desc) as TextView
            var precio = vista.findViewById(R.id.producto_precio) as TextView

            imagen.setImageResource(prod.imagen)
            nombre.setText(prod.nombre)
            desc.setText(prod.descripcion)
            precio.setText("$${prod.precio}")
            return vista
        }
    }


}