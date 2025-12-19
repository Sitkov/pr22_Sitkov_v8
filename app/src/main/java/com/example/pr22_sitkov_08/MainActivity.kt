package com.example.pr22_sitkov_08

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private var currentMovie: MovieDTO? = null

    // ВАШ ТОКЕН ПРИМЕНЕН
    private val API_KEY = "MMGFQQ8-0FR4N69-MQR472G-AN7TWBD"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация БД
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "movie_db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        val name = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("user", "Пользователь")
        findViewById<TextView>(R.id.tvWelcome).text = "Привет, $name!"

        val etSearch = findViewById<EditText>(R.id.etSearch)
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val tvInfo = findViewById<TextView>(R.id.tvMovieInfo)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                tvInfo.text = "Ищем..."

                // URL для поиска (v1.4)
                val url = "https://api.kinopoisk.dev/v1.4/movie/search?page=1&limit=1&query=$query"
                val queue = Volley.newRequestQueue(this)

                val request = object : JsonObjectRequest(Request.Method.GET, url, null,
                    { response ->
                        Log.d("API_DEBUG", "Ответ: $response")
                        try {
                            val res = Gson().fromJson(response.toString(), MovieResponse::class.java)
                            val movie = res.docs.firstOrNull()

                            if (movie != null) {
                                currentMovie = movie
                                val rating = movie.rating?.kp ?: 0.0
                                tvInfo.text = "🎬 ${movie.name}\n📅 Год: ${movie.year}\n⭐ Рейтинг: $rating"
                                btnSave.visibility = View.VISIBLE
                            } else {
                                tvInfo.text = "Ничего не найдено. Попробуйте другое название."
                                btnSave.visibility = View.GONE
                            }
                        } catch (e: Exception) {
                            tvInfo.text = "Ошибка обработки данных"
                            Log.e("API_DEBUG", "Ошибка Gson: ${e.message}")
                        }
                    },
                    { error ->
                        val code = error.networkResponse?.statusCode
                        Log.e("API_DEBUG", "Ошибка API: $code, ${error.message}")
                        tvInfo.text = "Ошибка API. Проверьте интернет или токен (Код: $code)"
                        Toast.makeText(this, "Ошибка сети", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = mutableMapOf<String, String>()
                        headers["X-API-KEY"] = API_KEY
                        headers["accept"] = "application/json" // ОБЯЗАТЕЛЬНО
                        return headers
                    }
                }
                queue.add(request)
            } else {
                Toast.makeText(this, "Введите название фильма", Toast.LENGTH_SHORT).show()
            }
        }

        btnSave.setOnClickListener {
            currentMovie?.let {
                db.movieDao().insert(MovieEntity(it.id, it.name ?: "Кино", it.year ?: 0, "В планах"))
                Toast.makeText(this, "Сохранено в Room!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnGoHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }
}