package ru.mooncalendar.data.subscriptionStatement

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.mooncalendar.data.subscriptionStatement.model.SubscriptionStatement
import ru.mooncalendar.data.subscriptionStatement.model.mapSubscriptionStatement
import java.util.*

class SubscriptionStatementRepository {

    private val db = Firebase.database
    private val auth = Firebase.auth

    fun getAll(
        onSuccess: (List<SubscriptionStatement>) -> Unit,
        onFailure: (String) -> Unit
    ){
        val withdrawalRequests = mutableListOf<SubscriptionStatement>()

        db.reference.child("subscription_statement").get()
            .addOnSuccessListener {
                for (i in it.children){
                    val item = i.mapSubscriptionStatement()
                    if(item != null)
                        withdrawalRequests.add(item)
                }

                onSuccess(withdrawalRequests)

            }
            .addOnFailureListener {
                onFailure(it.message ?: "Error")
            }
    }

    fun getByUserId(
        onSuccess: (SubscriptionStatement) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return

        db.reference.child("subscription_statement")
            .child(userId).get()
            .addOnSuccessListener {
                val item = it.mapSubscriptionStatement()

                if(item != null)
                    onSuccess(item)
            }
            .addOnFailureListener { onFailure(it.message ?: "Error") }
    }

    fun update(
        subscriptionStatement: SubscriptionStatement,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ){

        db.reference.child("subscription_statement").child(subscriptionStatement.userId)
            .setValue(subscriptionStatement)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Error") }
    }

    fun create(
        subscriptionStatement: SubscriptionStatement,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ){
        val id = UUID.randomUUID().toString()
        val userId = auth.currentUser?.uid ?: return

        subscriptionStatement.id = id
        subscriptionStatement.userId = userId

        db.reference.child("subscription_statement").child(userId)
            .setValue(subscriptionStatement)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Error") }
    }
}