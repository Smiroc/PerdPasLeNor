package com.example.perdpaslenor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.math.BigInteger
import java.security.MessageDigest


private var boutonParent : Button? = null


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.home)

        val phoneNumber = CheckPermission()
        BDDBASE(phoneNumber)

        boutonParent = findViewById<View>(R.id.button2) as Button?
        boutonParent!!.setOnClickListener { Connexion() }
    }

    private fun Connexion() {
        val intent = Intent(this, Connexion::class.java)
        startActivity(intent)
        finish()
    }

    fun encryptMD5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val messageDigest = md.digest(input.toByteArray())
        val no = BigInteger(1, messageDigest)
        var hashtext: String = no.toString(16)
        while (hashtext.length < 32) {
            hashtext = "0$hashtext"
        }
        return hashtext
    }

    private fun BDDBASE(phoneNumber: String?) {
        val internetPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.INTERNET
        ) == PackageManager.PERMISSION_GRANTED

        if (internetPermission) {
            // Établis la connexion avec la base de données
            val db = FirebaseFirestore.getInstance()
            db.collection("user")
                .whereEqualTo("numeroParent", phoneNumber?.let { encryptMD5(it) })
                .get()
                .addOnSuccessListener { parentdocuments ->
                    if (parentdocuments.isEmpty) {
                        // Aucun document trouvé pour le numéro de téléphone dans le champ "numeroEnfant"
                        // Essayez de chercher dans le champ "numeroParent"
                        db.collection("user")
                            .whereEqualTo("numeroEnfant", phoneNumber?.let { encryptMD5(it) })
                            .get()
                            .addOnSuccessListener { enfantDocuments ->
                                if (enfantDocuments.isEmpty) {
                                    // Aucun document trouvé pour le numéro de téléphone dans le champ "numeroParent"
                                    println("Aucun document trouvé.")
                                } else {
                                    val Genre : String = "enfant"
                                    IHMAUTH(Genre)
                                }
                            }
                            .addOnFailureListener { exception ->
                                println("Erreur lors de la récupération des données : $exception")
                            }
                    } else {
                        // Des documents ont été trouvés pour le numéro de téléphone dans le champ "numeroEnfant"
                        val Genre : String = "parent"
                        IHMAUTH(Genre)
                    }
                }
                .addOnFailureListener { exception ->
                    println("Erreur lors de la récupération des données : $exception")
                }


        } else {
            Toast.makeText(
                this,
                "Veuillez autoriser la connexion à internet",
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    private fun CheckPermission(): String? {
        var phoneNumber: String? = null // Initialisation avec null

        var permissionSMS = false
        var permissionNUM = false

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Demander la permission d'envoyer des SMS
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS),
                Connexion.PERMISSION_REQUEST_SEND_SMS
            )
        } else {
            permissionSMS = true
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // Demander la permission d'accéder au numéro de téléphone
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE),
                Connexion.PERMISSION_REQUEST_READ_PHONE_STATE
            )
        } else {
            permissionNUM = true
        }

        if (permissionNUM && permissionSMS) {
            // Les deux permissions sont accordées
            // Vous pouvez maintenant accéder au numéro de téléphone
            phoneNumber = getPhoneNumber()
        } else {
            // Vous devrez attendre la réponse de l'utilisateur dans onRequestPermissionsResult()
        }

        return phoneNumber
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Connexion.PERMISSION_REQUEST_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission d'envoi de SMS accordée
                    // Vous pouvez effectuer des actions nécessitant cette permission
                } else {
                    // La permission d'envoi de SMS a été refusée
                    Toast.makeText(this, "Veuillez accepter la permission d'envoie sms", Toast.LENGTH_SHORT).show();
                }
            }
            Connexion.PERMISSION_REQUEST_READ_PHONE_STATE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission d'accès au numéro de téléphone accordée
                    // Vous pouvez effectuer des actions nécessitant cette permission
                } else {
                    // La permission d'accès au numéro de téléphone a été refusée
                    Toast.makeText(this, "Veuillez accepter la permission information sur le numéro de téléphone", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private fun getPhoneNumber(): String? {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            telephonyManager.line1Number?.takeIf { it.isNotBlank() }
        } else {
            // La permission n'est pas accordée
            // Gérer le cas où l'application n'a pas la permission d'accéder au numéro de téléphone
            null
        }
    }

        setContentView(R.layout.home)

        val phoneNumber = checkPermission()
        BDDBASE(phoneNumber)

        boutonParent = findViewById<View>(R.id.button2) as Button?
        boutonParent!!.setOnClickListener { Connexion() }
    }

    private fun Connexion() {
        val intent = Intent(this, Connexion::class.java)
        startActivity(intent)
        finish()
    }

    fun encryptMD5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val messageDigest = md.digest(input.toByteArray())
        val no = BigInteger(1, messageDigest)
        var hashtext: String = no.toString(16)
        while (hashtext.length < 32) {
            hashtext = "0$hashtext"
        }
        return hashtext
    }

    private fun BDDBASE(phoneNumber: String?) {
        val internetPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.INTERNET
        ) == PackageManager.PERMISSION_GRANTED

        if (internetPermission) {
            // Établis la connexion avec la base de données
            val db = FirebaseFirestore.getInstance()
            db.collection("user")
                .whereEqualTo("numeroParent", phoneNumber?.let { encryptMD5(it) })
                .get()
                .addOnSuccessListener { parentdocuments ->
                    if (parentdocuments.isEmpty) {
                        // Aucun document trouvé pour le numéro de téléphone dans le champ "numeroEnfant"
                        // Essayez de chercher dans le champ "numeroParent"
                        db.collection("user")
                            .whereEqualTo("numeroEnfant", phoneNumber?.let { encryptMD5(it) })
                            .get()
                            .addOnSuccessListener { enfantDocuments ->
                                if (enfantDocuments.isEmpty) {
                                    // Aucun document trouvé pour le numéro de téléphone dans le champ "numeroParent"
                                    println("Aucun document trouvé.")
                                } else {
                                    val Genre : String = "enfant"
                                    phoneNumber?.let { IHMAUTH(Genre, it) }
                                }
                            }
                            .addOnFailureListener { exception ->
                                println("Erreur lors de la récupération des données : $exception")
                            }
                    } else {
                        // Des documents ont été trouvés pour le numéro de téléphone dans le champ "numeroEnfant"
                        val Genre : String = "parent"
                        phoneNumber?.let { IHMAUTH(Genre, it) }
                    }
                }
                .addOnFailureListener { exception ->
                    println("Erreur lors de la récupération des données : $exception")
                }


        } else {
            Toast.makeText(
                this,
                "Veuillez autoriser la connexion à internet",
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123 // Remplacez 123 par un code de demande de permission unique
    }


    private fun checkPermission(): String? {
        var phoneNumber: String? = null // Initialisation avec null
        val permissionSMS = ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
        val permissionNUM = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED

        if (!permissionSMS || !permissionNUM) {
            // Demander les permissions manquantes
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // Les deux permissions sont accordées
            // Vous pouvez maintenant accéder au numéro de téléphone
            phoneNumber = getPhoneNumber()

            // Faites quelque chose avec le numéro de téléphone
        }
        return phoneNumber
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Vérifiez si toutes les autorisations ont été accordées
            val allPermissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allPermissionsGranted) {
                // Toutes les permissions ont été accordées
                // Vous pouvez maintenant accéder au numéro de téléphone
                val phoneNumber = getPhoneNumber()
                // Faites quelque chose avec le numéro de téléphone
            } else {
                // Au moins une permission a été refusée
                // Gérez ce cas ici
                Toast.makeText(this, "Permissions refusées", Toast.LENGTH_SHORT).show()

                // Redemander les permissions refusées
                checkPermission()
            }
        }
    }

    private fun getPhoneNumber(): String? {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            telephonyManager.line1Number?.takeIf { it.isNotBlank() }
        } else {
            // La permission n'est pas accordée
            // Gérer le cas où l'application n'a pas la permission d'accéder au numéro de téléphone
            null
        }
    }

    private fun IHMAUTH(Genre : String,phoneNumber : String){
        val intent = Intent(this, Authentification::class.java)
        intent.putExtra("Genre", Genre);
        intent.putExtra("phoneNumber", phoneNumber);

        startActivity(intent)
    }
}