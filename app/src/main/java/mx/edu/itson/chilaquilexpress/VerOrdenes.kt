package mx.edu.itson.chilaquilexpress

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import mx.edu.itson.chilaquilexpress.ProductosActivity.AdaptadorProductos

class VerOrdenes : AppCompatActivity() {
    lateinit var adaptadorOrdenes: AdaptadorOrdenes
    lateinit var listOrdenes: ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_ordenes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listOrdenes = findViewById(R.id.listOrdenes)

        cargarOrdenesDesdeFirestore()

        listOrdenes.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val ordenSeleccionada = parent.getItemAtPosition(position) as Orden
            if (ordenSeleccionada.identificador.contains("Mesa", ignoreCase = true)) {
                // Aquí haces lo que necesitas si contiene "Mesa"
                mostrarDialogoTipoPago(this) { tipoPago ->
                    when (tipoPago) {
                        "individual" -> {
                            // Lógica para pago individual
                            Toast.makeText(this, "Es una orden de mesa individual", Toast.LENGTH_SHORT).show()
                        }
                        "mesa_completa" -> {
                            // Lógica para pago de mesa completa
                            Toast.makeText(this, "Es una orden de mesa completa", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                val intent: Intent = Intent(this, OrdenAPagar::class.java)
                intent.putExtra("identificador", "individual")
                intent.putExtra("boton", 3)
                intent.putExtra("orden", ordenSeleccionada)
                startActivity(intent)
                Toast.makeText(this, "No es una orden de mesa", Toast.LENGTH_SHORT).show()
            }
        }



    }

    private fun mostrarDialogoTipoPago(context: Context, onSeleccionado: (String) -> Unit) {
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
            gravity = Gravity.CENTER
        }

        val btnIndividual = Button(context).apply {
            text = "Pago Individual"
        }

        val btnMesaCompleta = Button(context).apply {
            text = "Pago Mesa Completa"
        }

        layout.addView(btnIndividual)
        layout.addView(btnMesaCompleta)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Selecciona tipo de pago")
            .setView(layout)
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .create()

        btnIndividual.setOnClickListener {
            onSeleccionado("individual")
            dialog.dismiss()
        }

        btnMesaCompleta.setOnClickListener {
            onSeleccionado("mesa_completa")
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun cargarOrdenesDesdeFirestore() {
        adaptadorOrdenes = AdaptadorOrdenes(this, ArrayList())
        listOrdenes.adapter = adaptadorOrdenes

        val db = FirebaseFirestore.getInstance()
        db.collection("ordenes")
            .whereEqualTo("pagado", false)
            .get()
            .addOnSuccessListener { result ->
                val resultado = result.documents.mapNotNull { doc ->
                    try {
                        val id = doc.getString("id") ?: return@mapNotNull null
                        val identificador = doc.getString("identificador") ?: return@mapNotNull null
                        val tipoString = doc.getString("tipo") ?: return@mapNotNull null
                        val tipo = when (tipoString) {
                            "MESA" -> TipoOrden.MESA
                            "PERSONA" -> TipoOrden.PERSONA
                            else -> TipoOrden.PERSONA
                        }
                        val fecha = (doc.get("fecha") as? com.google.firebase.Timestamp)?.toDate()?.toString() ?: "Sin fecha"
                        val costoTotal = (doc.get("costoTotal") as? Number)?.toFloat() ?: 0f
                        val pagado = doc.getBoolean("pagado") ?: false

                        val chilaquilesRaw = doc.get("chilaquiles") as? List<Map<String, Any>> ?: emptyList()
                        val chilaquiles = chilaquilesRaw.mapNotNull { ch ->
                            try {
                                val tipoSalsa = when (ch["tipo"] as? String) {
                                    "verde" -> TipoSalsa.VERDE
                                    "rojo" -> TipoSalsa.ROJA
                                    else -> TipoSalsa.VERDE
                                }
                                val proteinas = (ch["proteinas"] as? List<Long>)?.map { it.toInt() } ?: emptyList()
                                val toppings = (ch["toppings"] as? List<Long>)?.map { it.toInt() } ?: emptyList()
                                val precioBase = (ch["precioBase"] as? Number)?.toInt() ?: 0
                                val costoChilaquil = (ch["costoTotal"] as? Number)?.toInt() ?: 0

                                Chilaquil(0, tipoSalsa, precioBase, proteinas, toppings, costoChilaquil)
                            } catch (e: Exception) {
                                null
                            }
                        }

                        val bebidasRaw = doc.get("bebida") as? List<Map<String, Any>> ?: emptyList()
                        val bebidas = bebidasRaw.mapNotNull { b ->
                            try {
                                val bid = b["id"] as? String ?: return@mapNotNull null
                                val nombre = b["nombre"] as? String ?: return@mapNotNull null
                                val costo = (b["costo"] as? Number)?.toInt() ?: return@mapNotNull null
                                Bebida(id = bid, nombre = nombre, costo = costo)
                            } catch (e: Exception) {
                                null
                            }
                        }

                        Orden(id, identificador, tipo, chilaquiles, bebidas, fecha, costoTotal, pagado)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }

                adaptadorOrdenes.ordenes.clear()
                adaptadorOrdenes.ordenes.addAll(resultado)
                adaptadorOrdenes.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    }

    class AdaptadorOrdenes(
        private val contexto: Context,
        val ordenes: ArrayList<Orden>
    ) : BaseAdapter() {

        override fun getCount(): Int = ordenes.size

        override fun getItem(position: Int): Any = ordenes[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val orden = ordenes[position]
            val vista = LayoutInflater.from(contexto).inflate(R.layout.ordenes_view, null)
            val titulo = vista.findViewById<TextView>(R.id.orden_Titulo)

            titulo.text = "Orden de: ${orden.identificador}"
            return vista
        }
    }