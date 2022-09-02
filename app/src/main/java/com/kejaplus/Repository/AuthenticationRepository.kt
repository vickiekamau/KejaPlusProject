package com.kejaplus.Repository

import android.content.Context
import com.facebook.AccessTokenTracker
import com.facebook.CallbackManager
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.kejaplus.application.Model.Users
import kotlinx.coroutines.tasks.await
import java.util.*

class AuthenticationRepository(context: Context) {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var mCallbackManager: CallbackManager
    private var mFirebaseAuth: FirebaseAuth= FirebaseAuth.getInstance()
    private lateinit var tokenTracker: AccessTokenTracker
    private lateinit var auth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserId: String = ""
    private val mContext = context



    suspend fun  create(email: String, password: String) =
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).await()


    suspend fun  login(email: String, password: String): AuthResult =
        mFirebaseAuth.signInWithEmailAndPassword(email, password).await()

    suspend fun  signInWithGoogle(credentials: AuthCredential): AuthResult {
        auth = Firebase.auth
       // val credentials = GoogleAuthProvider.getCredential(idToken,null)
        return auth.signInWithCredential(credentials).await()
    }



    fun createUser(users: Users): String {

        firebaseUserId = mFirebaseAuth.currentUser!!.uid
        refUsers = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUserId)

        val userMap = mapOf(
            "uid" to firebaseUserId,
            "username" to users.name,
            "email" to users.email,
            "password" to users.password
        )

        refUsers.updateChildren(userMap).addOnCompleteListener{ task ->

        }


        /** mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    // val user = mFirebaseAuth.currentUser
                    // updateUI(user)


                } else {
                    // If sign in fails, display a message to the user.

                }

            }*/
        return firebaseUserId
    }
}




