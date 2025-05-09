package mx.edu.itson.chilaquilexpress

import android.content.Context
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

    }

    private fun cargarOrdenesDesdeFirestore() {
        adaptadorOrdenes = AdaptadorOrdenes(this, ArrayList())
        listOrdenes.adapter = adaptadorOrdenes

        var fireService: FirebaseService = FirebaseService()
        val db = FirebaseFirestore.getInstance()
       // var ordenesFirestore = fireService.obtenerOrdenesNoPagadas()
       var ordenesFirebase = db.collection("ordenes")
            .whereEqualTo("pagado", false).get().addOnSuccessListener { result ->
            val resultado = result.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: return@mapNotNull null
                val identificador = doc.getString("identificador") ?: return@mapNotNull null
                val tipoBd = doc.getString("tipo") ?: return@mapNotNull null
                val chilaquiles = doc.toObject<Orden>()?.chilaquiles ?: emptyList()
                val bebidas = doc.toObject<Orden>()?.bebidas ?: emptyList()
                val costoTotal = (doc.get("costoTotal") as? Number)?.toFloat() ?: 0f
                val pagado = doc.getBoolean("pagado")?: return@mapNotNull null
                val fecha = doc.getString("fecha")?: return@mapNotNull null

                var tipo: TipoOrden = when(tipoBd){
                    "MESA" ->{ TipoOrden.MESA }
                    "PERSONA" ->{ TipoOrden.PERSONA }
                    else -> throw IllegalArgumentException("TipoOrden desconocido: $tipoBd")
                }

                Orden(
                    id = id,
                    identificador = identificador,
                    tipo = tipo,
                    chilaquiles = chilaquiles,
                    bebidas = bebidas,
                    fecha = fecha,
                    costoTotal = costoTotal,
                    pagado = pagado
                )
            }

            // Actualizar datos del adaptador
            adaptadorOrdenes.ordenes.clear()
            adaptadorOrdenes.ordenes.addAll(resultado)
            adaptadorOrdenes.notifyDataSetChanged()
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