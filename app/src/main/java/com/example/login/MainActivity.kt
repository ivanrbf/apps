package com.example.login

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.login.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
        auth = Firebase.auth
        Log.d("ponto", "ponto0")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("608817800794-skqu6o5donokt9vkfveqq23p9bvbkria.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.botaoEntrar.setOnClickListener {
            Log.d("ponto", "ponto00")
            if (TextUtils.isEmpty(binding.editTextTextUsuario.text)) {
                binding.editTextTextUsuario.error = "Campo usuário não pode estar em branco"
            } else if (TextUtils.isEmpty(binding.editTextTextSenha.text)) {
                binding.editTextTextSenha.error = "Campo senha não pode estar em branco"

            } else {
                loginUsuarioESenha(
                    binding.editTextTextUsuario.text.toString(),
                    binding.editTextTextSenha.text.toString()
                )
            }
        }

        binding.botaoGoogle.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val intent = googleSignInClient.signInIntent
        abreActivity.launch(intent)
    }

    var abreActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
val resultado = result.toString()

        Log.d("ponto", resultado)
        if (result.resultCode == RESULT_OK) {
            Log.d("ponto", "ponto1")
            val intent = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)

            try {
                val conta = task.getResult(ApiException::class.java)
                loginComGoogle(conta.idToken!!)

            } catch (exception: ApiException) {
                Log.d("ponto", "ponto000")
            }
        }else{
            Log.d("ponto", "else")
        }
    }

    private fun loginComGoogle(token: String) {
        val credencial = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credencial)
            .addOnCompleteListener(this) { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext, "Authenticação efetuada com o Google.",
                        Toast.LENGTH_SHORT
                    ).show()
                    abrePrincipal()

                } else {
                    Toast.makeText(
                        baseContext, "Erro de authenticação com o Google.",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

    private fun loginUsuarioESenha(usuario: String, senha: String) {
        auth.signInWithEmailAndPassword(
            usuario,
            senha
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    //val user = auth.currentUser
                    Toast.makeText(
                        baseContext, "Authenticação efetuada.",
                        Toast.LENGTH_SHORT
                    ).show()
                    abrePrincipal()
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext, "Authenticação falhou.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //updateUI(null)
                }
            }
    }

    fun abrePrincipal() {

        binding.editTextTextUsuario.text.clear()
        binding.editTextTextUsuario.text.clear()

        val intent = Intent(this, PrincipalActivity::class.java)

        startActivity(intent)

        finish()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            if (currentUser.email?.isNotEmpty() == true) {
                Toast.makeText(
                    baseContext, "Usuário " + currentUser.email + " logado",
                    Toast.LENGTH_SHORT
                ).show()
                abrePrincipal()
            }
        }

        //updateUI(currentUser)
    }
}
