package mx.edu.itson.chilaquilexpress


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await


class FirebaseService {

    private val db = FirebaseFirestore.getInstance()

    suspend fun obtenerProteinas(): List<Proteina> {
        return try {
            val snapshot = db.collection("proteinas").get().await()
            snapshot.documents.mapNotNull { it.toObject<Proteina>() }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error obteniendo proteínas", e)
            emptyList()
        }
    }

    suspend fun obtenerToppings(): List<Topping> {
        return try {
            val snapshot = db.collection("toppings").get().await()
            snapshot.documents.mapNotNull { it.toObject<Topping>() }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error obteniendo toppings", e)
            emptyList()
        }
    }

    suspend fun obtenerBebidas(): List<Bebida> {
        return try {
            val snapshot = db.collection("bebidas").get().await()
            snapshot.documents.mapNotNull { it.toObject<Bebida>() }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error obteniendo bebidas", e)
            emptyList()
        }
    }

    suspend fun obtenerOrdenes(): List<Orden> {
        return try {
            val snapshot = db.collection("ordenes").get().await()
            snapshot.documents.mapNotNull { it.toObject<Orden>() }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error obteniendo órdenes", e)
            emptyList()
        }
    }

    suspend fun obtenerChilaquilesDeOrden(ordenId: String): List<Chilaquil> {
        return try {
            val snapshot = db.collection("ordenes").document(ordenId)
                .collection("chilaquiles").get().await()
            snapshot.documents.mapNotNull { it.toObject<Chilaquil>() }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error obteniendo chilaquiles", e)
            emptyList()
        }
    }
}
