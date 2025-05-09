package mx.edu.itson.chilaquilexpress

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
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

        val dialog = AlertDialog.Builder(context)
            .setTitle("Nombre del comensal:")
            .setView(editText)
            .setPositiveButton("Aceptar", null) // No cerramos el diálogo automáticamente
            .setNegativeButton("Cancelar") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()

        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val nombre = editText.text.toString().trim()
                if (nombre.isEmpty()) {
                    editText.error = "No puede estar vacío"
                } else {
                    onNombreIngresado(nombre)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }


    fun mostrarDialogoMesa(context: Context, onMesaIngresada: (String) -> Unit) {
        val editText = EditText(context)
        editText.hint = "Ingresa la mesa"
        editText.inputType = InputType.TYPE_CLASS_NUMBER

        val dialog = AlertDialog.Builder(context)
            .setTitle("Mesa de los comensales:")
            .setView(editText)
            .setPositiveButton("Aceptar", null)
            .setNegativeButton("Cancelar") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()

        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val mesa = editText.text.toString().trim()
                if (mesa.isEmpty()) {
                    editText.error = "Ingresa un número de mesa válido"
                } else {
                    onMesaIngresada(mesa)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

}