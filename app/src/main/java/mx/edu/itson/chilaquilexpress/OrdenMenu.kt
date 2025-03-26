package mx.edu.itson.chilaquilexpress

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class OrdenMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_orden_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnFinalizar: Button = findViewById(R.id.btnFinalizar)
        val btnAgregar: Button = findViewById(R.id.btnAgregarProducto)

        btnFinalizar.setOnClickListener{
            var intent: Intent = Intent(this, TipoOrdenActivity::class.java)
            startActivity(intent)
        }

        btnAgregar.setOnClickListener{
            var intent: Intent = Intent(this, ProductosActivity::class.java)
            startActivity(intent)
        }

    }
}