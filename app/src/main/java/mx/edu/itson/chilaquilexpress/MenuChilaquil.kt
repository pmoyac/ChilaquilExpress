package mx.edu.itson.chilaquilexpress

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        val radioGroupSalsa = findViewById<RadioGroup>(R.id.radioGroupSalsa)
        val layoutProteina = findViewById<LinearLayout>(R.id.layoutProteina)

        val layoutToppings = findViewById<LinearLayout>(R.id.layoutToppings)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)

        val cbCrema = findViewById<CheckBox>(R.id.cbCrema)
        val cbQueso = findViewById<CheckBox>(R.id.cbQueso)
        val cbCilantro = findViewById<CheckBox>(R.id.cbCilantro)
        val cbCebolla = findViewById<CheckBox>(R.id.cbCebolla)

        // Mostrar selección de proteína cuando eligen una salsa
        radioGroupSalsa.setOnCheckedChangeListener { _, _ ->
            layoutProteina.visibility = View.VISIBLE
        }

        // Referencias a los RadioButton de proteínas
        val rbPollo = findViewById<RadioButton>(R.id.rbPollo)
        val rcChilorio = findViewById<RadioButton>(R.id.rbChilorio)
        val rbChorizo = findViewById<RadioButton>(R.id.rbChorizo)
        val rbChicharron = findViewById<RadioButton>(R.id.rbChicharron)
        val rbCochinita = findViewById<RadioButton>(R.id.rbCochinita)
        val rbFrijoles = findViewById<RadioButton>(R.id.rbFrijoles)


        val proteinButtons = listOf(rbPollo, rbFrijoles, rbCochinita, rbChicharron, rcChilorio, rbChorizo)
        proteinButtons.forEach { button ->
            button.setOnClickListener {
                layoutToppings.visibility = View.VISIBLE
            }
        }

        fun verificarToppings() {
            if (cbCrema.isChecked || cbQueso.isChecked || cbCilantro.isChecked || cbCebolla.isChecked) {
                btnConfirmar.visibility = View.VISIBLE
            } else {
                btnConfirmar.visibility = View.GONE
            }
        }

        cbCrema.setOnCheckedChangeListener { _, _ -> verificarToppings() }
        cbQueso.setOnCheckedChangeListener { _, _ -> verificarToppings() }
        cbCilantro.setOnCheckedChangeListener { _, _ -> verificarToppings() }
        cbCebolla.setOnCheckedChangeListener { _, _ -> verificarToppings() }


        btnConfirmar.setOnClickListener {


            val mensaje = "Pedido Enviado"

            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        }
    }
}