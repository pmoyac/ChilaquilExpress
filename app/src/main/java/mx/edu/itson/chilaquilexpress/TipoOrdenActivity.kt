package mx.edu.itson.chilaquilexpress

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
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

        buttonMesa.setOnClickListener(){
            var intent: Intent = Intent(this, MenuChilaquil::class.java)
            startActivity(intent)

        }

        buttonPersona.setOnClickListener(){
            var intent: Intent = Intent(this, MenuChilaquil::class.java)
            startActivity(intent)

        }
    }
}