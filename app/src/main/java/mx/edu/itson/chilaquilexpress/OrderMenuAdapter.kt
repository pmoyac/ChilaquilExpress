package mx.edu.itson.chilaquilexpress

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class OrderAdapter(private val context: Context, private val items: List<MasMenos>) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.botones_mas_menos, parent, false)

        val itemNombre = view.findViewById<TextView>(R.id.itemNombre)
        val cantidad = view.findViewById<TextView>(R.id.txtCantidad)
        val btnMenos = view.findViewById<Button>(R.id.btnMenos)
        val btnMas = view.findViewById<Button>(R.id.btnMas)

        val item = items[position]
        itemNombre.text = item.valor
        cantidad.text = item.cantidad.toString()

        btnMas.setOnClickListener {
            item.aumentaCantidad()
            cantidad.text = item.cantidad.toString()
        }

        btnMenos.setOnClickListener {
            item.disminuyeCantidad()
            cantidad.text = item.cantidad.toString()
        }

        return view
    }
}
