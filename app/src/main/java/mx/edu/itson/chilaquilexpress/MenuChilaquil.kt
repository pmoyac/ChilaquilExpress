package mx.edu.itson.chilaquilexpress

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MenuChilaquil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_chilaquil)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val nombre = intent.getStringExtra("nombre")
        val precio = intent.getDoubleExtra("precio", 0.0)
        findViewById<TextView>(R.id.tipoChilaquil).text = nombre + " $" + precio


        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)

        val cbCrema = findViewById<CheckBox>(R.id.cbCrema)
        val cbQueso = findViewById<CheckBox>(R.id.cbQueso)
        val cbCilantro = findViewById<CheckBox>(R.id.cbCilantro)
        val cbCebolla = findViewById<CheckBox>(R.id.cbCebolla)


        btnConfirmar.setOnClickListener {
            val mensaje = "Continue"
            var intent: Intent = Intent(this, OrdenActual::class.java)
            startActivity(intent)
            /*
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
            finish()*/
        }
    }
}