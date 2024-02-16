package com.example.perdpaslenor

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Connexion : AppCompatActivity() {
    companion object {
        const val PERMISSION_REQUEST_READ_PHONE_STATE = 1
        const val PERMISSION_REQUEST_SEND_SMS = 101
    }

    private lateinit var number: EditText
    private lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.connexion)

        button = findViewById(R.id.Envoie)
        number = findViewById(R.id.Number)

        button.setOnClickListener {
            val phone = number.text.toString()
            if (phone.isNotEmpty()) {
                val randomNumber = (10000..99999).random()

                val phoneNumber = checkPermission()

                val obj = SmsManager.getDefault()
                obj.sendTextMessage(
                    phone, null, "$randomNumber",
                    null, null
                )
                authentification(phone, randomNumber, phoneNumber)
            }
        }
    }

    private fun checkPermission(): String? {
        var phoneNumber: String? = null // Initialisation avec null

        var permissionSMS = false
        var permissionNUM = false

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Demander la permission d'envoyer des SMS
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS),
                PERMISSION_REQUEST_SEND_SMS
            )
        } else {
            permissionSMS = true
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Demander la permission d'accéder au numéro de téléphone
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                PERMISSION_REQUEST_READ_PHONE_STATE
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission d'envoi de SMS accordée
                } else {
                    // La permission d'envoi de SMS a été refusée
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Veuillez accepter la permission d'envoi sms",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            PERMISSION_REQUEST_READ_PHONE_STATE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission d'accès au numéro de téléphone accordée
                } else {
                    // La permission d'accès au numéro de téléphone a été refusée
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Veuillez accepter la permission information sur le numéro de téléphone",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun getPhoneNumber(): String? {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            telephonyManager.line1Number?.takeIf { it.isNotBlank() }
        } else
        // La permission n'est pas accordée
        // Gérer le cas où l'application n'a pas la permission d'accéder au numéro de téléphone
            null
    }

    private fun authentification(phone: String, randomNumber: Int, phoneNumber: String?) {
        val intent = Intent(this, Authentification::class.java)
        val chaine: String = randomNumber.toString()
        intent.putExtra("phoneTO", phone)
        intent.putExtra("codeAUTH", chaine)
        intent.putExtra("ME", phoneNumber)
        startActivity(intent)
    }
}