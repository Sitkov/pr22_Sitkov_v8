package com.example.pr22_sitkov_08

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etName = findViewById<EditText>(R.id.etName)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Если уже логинились, идем на главный экран
        if (prefs.getString("user", null) != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnLogin.setOnClickListener {
            val name = etName.text.toString()
            if (name.isNotEmpty()) {
                prefs.edit().putString("user", name).apply()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show()
            }
        }
    }
}