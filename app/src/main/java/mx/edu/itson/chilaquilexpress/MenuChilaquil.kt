package mx.edu.itson.chilaquilexpress

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat

class MenuChilaquil : AppCompatActivity() {
    var boton: Int=0;
    var identificador: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        boton = intent.getIntExtra("boton", 0)
        identificador = intent.getStringExtra("identificador") ?: ""
        setContentView(R.layout.activity_menu_chilaquil)

        val nombre = intent.getStringExtra("nombre") ?: ""
        val precioBase = intent.getIntExtra("costo", 0)
        findViewById<TextView>(R.id.tipoChilaquil).text = "$nombre $${precioBase}"

        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)

        val cbPollo = findViewById<CheckBox>(R.id.cbPollo)
        val cbFrijoles = findViewById<CheckBox>(R.id.cbFrijoles)
        val cbChicharron = findViewById<CheckBox>(R.id.cbChicharron)
        val cbChorizo = findViewById<CheckBox>(R.id.cbChorizo)
        val cbChilorio = findViewById<CheckBox>(R.id.cbChilorio)
        val cbCochinita = findViewById<CheckBox>(R.id.cbCochinita)

        cbFrijoles.text = HtmlCompat.fromHtml(getString(R.string.frijoles), HtmlCompat.FROM_HTML_MODE_LEGACY)
        cbPollo.text = HtmlCompat.fromHtml(getString(R.string.pollo), HtmlCompat.FROM_HTML_MODE_LEGACY)
        cbChorizo.text = HtmlCompat.fromHtml(getString(R.string.chorizo), HtmlCompat.FROM_HTML_MODE_LEGACY)
        cbChilorio.text = HtmlCompat.fromHtml(getString(R.string.chilorio), HtmlCompat.FROM_HTML_MODE_LEGACY)
        cbChicharron.text = HtmlCompat.fromHtml(getString(R.string.chicharron), HtmlCompat.FROM_HTML_MODE_LEGACY)
        cbCochinita.text = HtmlCompat.fromHtml(getString(R.string.cochinita), HtmlCompat.FROM_HTML_MODE_LEGACY)

        val cbCrema = findViewById<CheckBox>(R.id.cbCrema)
        val cbQueso = findViewById<CheckBox>(R.id.cbQueso)
        val cbCilantro = findViewById<CheckBox>(R.id.cbCilantro)
        val cbCebolla = findViewById<CheckBox>(R.id.cbCebolla)

        btnConfirmar.setOnClickListener {
            val proteinasSeleccionadas = mutableListOf<String>()
            if (cbPollo.isChecked) proteinasSeleccionadas.add("Pollo")
            if (cbFrijoles.isChecked) proteinasSeleccionadas.add("Frijoles")
            if (cbChicharron.isChecked) proteinasSeleccionadas.add("Chicharrón")
            if (cbChorizo.isChecked) proteinasSeleccionadas.add("Chorizo")
            if (cbChilorio.isChecked) proteinasSeleccionadas.add("Chilorio")
            if (cbCochinita.isChecked) proteinasSeleccionadas.add("Cochinita")

            val toppingsSeleccionados = mutableListOf<String>()
            if (cbCrema.isChecked) toppingsSeleccionados.add("Crema")
            if (cbQueso.isChecked) toppingsSeleccionados.add("Queso")
            if (cbCilantro.isChecked) toppingsSeleccionados.add("Cilantro")
            if (cbCebolla.isChecked) toppingsSeleccionados.add("Cebolla")

            var precioTotal = precioBase
            for (proteina in proteinasSeleccionadas) {
                precioTotal += PreciosProteinas.getPrecio(proteina)
            }

            val producto = Producto(
                imagen = R.drawable.chilaquiles,
                nombre = nombre,
                descripcion = "Chilaquiles personalizados",
                costo = precioTotal,
                categoria = "Chilaquiles",
                cantidad = 1,
                toppings = toppingsSeleccionados,
                proteinas = proteinasSeleccionadas
            )

            OrdenManager.agregarProducto(producto)
            val intent = Intent(this, OrdenActual::class.java)
            intent.putExtra("boton",boton)
            intent.putExtra("identificador", identificador)
            startActivity(intent)
        }
    }
}