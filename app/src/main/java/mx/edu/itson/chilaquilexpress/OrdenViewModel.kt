package mx.edu.itson.chilaquilexpress

import androidx.lifecycle.ViewModel

class OrdenViewModel: ViewModel() {
    val menu = Menu(
        toppings = listOf(
            Topping(1, "queso"),
            Topping(2, "cebolla"),
            Topping(3, "cilantro"),
            Topping(4, "crema")
        ),
        proteinas = listOf(
            Proteina(1, "pollo", 10.00),
            Proteina(2, "frijoles", 10.00),
            Proteina(3, "chicharron", 15.00),
            Proteina(4, "chorizo", 15.00),
            Proteina(5, "chilorio", 15.00),
            Proteina(6, "cochinita", 15.00)
        ),
        bebidas = listOf(
            Bebida("_agua_piña", "Piña", 20.00),
            Bebida("_agua_jamaica", "Jamaica", 20.00),
            Bebida("_agua_cebada", "Cebada", 20.00),
            Bebida("_agua_horchata", "Horchata", 20.00),
            Bebida("_bebida_cocacola", "Coca Cola", 20.00)
        )
    )
}