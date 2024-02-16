package com.example.perdpaslenor
import android.Manifest
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.math.BigInteger
import java.security.MessageDigest
import java.util.Timer
import java.util.TimerTask

/**
 * Cette classe permet de gérer l'authentification
 */
class Authentification : AppCompatActivity() {
    private lateinit var boutonconf: Button
    private lateinit var editcode: EditText
    private var nbr: Int = 3
    private lateinit var phoneNumber: String
    private val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authentification)

        val phoneTO = intent.getStringExtra("phoneTO")
        val codeAUTH = intent.getStringExtra("codeAUTH")

        val me = intent.getStringExtra("ME")
        val genre = intent.getStringExtra("Genre")
        this.phoneNumber = intent.getStringExtra("phoneNumber").toString()


        if (genre == "parent") {
            viewParent()
            return
        } else if (genre == "enfant") {
            bgEnfant()
            return
        }

        boutonconf = (findViewById<View>(R.id.buttonconfirmation) as Button?)!!
        runOnUiThread {
            Toast.makeText(this, "Un code d'authentification a été envoyer", Toast.LENGTH_SHORT).show()
        }

        checkup(me, phoneTO, codeAUTH)

        timer.schedule(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        this@Authentification,
                        "Il vous reste 1 minute pour rentrer le code reçu",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, 10000)

        timer.schedule(object : TimerTask() {
            override fun run() {
                tempsFIN()
            }
        }, 120000) // 120000 millisecondes = 2 minutes

        boutonconf.setOnClickListener { parents(me, phoneTO, codeAUTH) }
    }

    /**
     * Cette méthode est appelée après 2 minutes si l'utilisateur n'a pas rentré le code d'authentification
     */
    private fun tempsFIN() {
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

    /**
     * Cette méthode permet de lier un enfant à un parent
     */
    private fun parents(me: String?, phoneTO: String?, codeAUTH: String?) {
        editcode = findViewById(R.id.editionAUTH)
        val etexte: String = editcode.text.toString()
        val encryptedPhoneTO = phoneTO?.let { encryptMD5(it) }
        val encryptedME = me?.let { encryptMD5(it) }

        if (nbr > 0) {

            if (codeAUTH == etexte) {

                val internetPermission = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.INTERNET
                ) == PackageManager.PERMISSION_GRANTED

                if (internetPermission) {
                    // Établis la connexion avec la base de données
                    val db = FirebaseFirestore.getInstance()

                    // Crée un objet de type Map pour stocker vos données
                    val user = hashMapOf(
                        "numeroEnfant" to encryptedPhoneTO,
                        "numeroParent" to encryptedME,
                        "latitude" to 0,
                        "longitude" to 0,
                        "etat" to "L'enfant n'a pas lancé ou relancé l'application. Veuillez relancer l'application quand l'enfant aura initialisé la géolocalisation.",
                        "date" to Timestamp.now()
                    )

                    // Ajoutez les données à votre collection "utilisateurs"
                    db.collection("user")
                        .add(user)
                        .addOnSuccessListener {
                            // L'ajout des données a réussi
                            runOnUiThread {
                                Toast.makeText(
                                    this,
                                    "L'ajout des données a réussi",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            viewParent()
                        }
                        .addOnFailureListener {
                            // L'ajout des données a échoué
                            runOnUiThread {
                                Toast.makeText(
                                    this,
                                    "L'ajout des données a échoué",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Veuillez autoriser la connexion à internet",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Le code d'authentification ne correspond pas (il vous reste $nbr essais avant que le code ne soit plus valide)",
                        Toast.LENGTH_SHORT
                    ).show()
                    nbr -= 1
                }
            }
        } else {
            val intent = Intent(this, Connexion::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Cette méthode permet de vérifier si les données reçues sont valides
     */
    private fun checkup(me: String?, phoneTO: String?, codeAUTH: String?) {
        var error = false
        if (phoneTO == null) {
            error = true
        }
        if (codeAUTH == null) {
            error = true
        }
        if (me == null) {
            error = true
        }

        if (error) {
            runOnUiThread {
                Toast.makeText(this, "Error réception de données", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Cette méthode permet de demander la permission de la localisation
     */
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        )
    }

    /**
     * Cette méthode est automatiquement appellée par le système après qu'il ait accepté ou non la permission de la localisation
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Regarde si c'est la demande pour la permission de la localisation qui a été traitée
        if (requestCode == 1) {

            // Regarde si la permission est accordée
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée
                bgEnfant()
            } else {
                // Permission refusé, re-demande la permission
                requestLocationPermission()
            }
        }
    }

    /**
     * Cette méthode permet de passer à la vue parent
     */
    private fun viewParent() {
        val intent = Intent(this, IHMParentReception::class.java)
        intent.putExtra("phoneNumber", phoneNumber)
        startActivity(intent)
        finish()
    }


    /**
     * Cette méthode permet lancer le service en arrière-plan
     */
    private fun bgEnfant() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.FOREGROUND_SERVICE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val requiredSdkVersion = Build.VERSION_CODES.P
            if (Build.VERSION.SDK_INT >= requiredSdkVersion) {
                // Demander la permission d'envoyer des notifications
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.FOREGROUND_SERVICE),
                    1
                )
            }
        }

        // Nom du composant de l'application à désactiver
        val componentName = ComponentName(this, MainActivity::class.java)

        // Obtenir le gestionnaire de package
        val packageManager: PackageManager = applicationContext.packageManager

        // Désactiver le composant de l'application
        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )

        BootReceiver().onReceive(this, Intent())

        finish()

        // Retour sur l'écran d'accueil du téléphone
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }
}