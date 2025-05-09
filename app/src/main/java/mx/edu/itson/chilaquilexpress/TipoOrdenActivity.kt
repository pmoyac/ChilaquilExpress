package mx.edu.itson.chilaquilexpress

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TipoOrdenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tipo_orden)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonMesa: Button = findViewById(R.id.btn_por_mesa)
        val buttonPersona: Button = findViewById(R.id.btn_por_persona)
        val btnOrdenes: Button = findViewById(R.id.btn_Ver_Ordenes)

        buttonMesa.setOnClickListener(){
            mostrarDialogoMesa(this){ mesa ->
                var intent: Intent = Intent(this, ProductosActivity::class.java)
                intent.putExtra("boton", 1)
                intent.putExtra("identificador", mesa)
                startActivity(intent)
            }

        }

        buttonPersona.setOnClickListener(){
            mostrarDialogoNombre(this){nombre ->
                var intent: Intent = Intent(this, ProductosActivity::class.java)
                intent.putExtra("boton", 2)
                intent.putExtra("identificador", nombre)
                startActivity(intent)
            }
        }

        btnOrdenes.setOnClickListener(){
            var intent: Intent = Intent(this, VerOrdenes::class.java)
            startActivity(intent)
        }
    }

    fun mostrarDialogoNombre(context: Context, onNombreIngresado: (String) -> Unit) {
        val editText = EditText(context)
        editText.hint = "Ingresa el nombre"

        AlertDialog.Builder(context)
            .setTitle("Nombre del comensal:")
            .setView(editText)
            .setPositiveButton("Aceptar") { dialog, _ ->
                val nombre = editText.text.toString()
                onNombreIngresado(nombre)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    fun mostrarDialogoMesa(context: Context, onMesaIngresada: (String) -> Unit) {
        val editText = EditText(context)
        editText.hint = "Ingresa la mesa"

        AlertDialog.Builder(context)
            .setTitle("Mesa de los comensales:")
            .setView(editText)
            .setPositiveButton("Aceptar") { dialog, _ ->
                val mesa = editText.text.toString()
                onMesaIngresada(mesa)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}