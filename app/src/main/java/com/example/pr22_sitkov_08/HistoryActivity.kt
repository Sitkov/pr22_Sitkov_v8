package com.example.pr22_sitkov_08

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        val scroll = ScrollView(this).apply { addView(container) }
        setContentView(scroll)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "movie_db")
            .allowMainThreadQueries().build()

        fun updateUI() {
            container.removeAllViews()
            val movies = db.movieDao().getAll()
            if (movies.isEmpty()) container.addView(TextView(this).apply { text = "Пусто" })
            movies.forEach { movie ->
                val tv = TextView(this).apply {
                    text = "${movie.title} (${movie.year})\nСтатус: ${movie.note}\n"
                    textSize = 18f
                }
                tv.setOnClickListener {
                    movie.note = "ПРОСМОТРЕНО"
                    db.movieDao().update(movie)
                    updateUI()
                }
                tv.setOnLongClickListener {
                    db.movieDao().delete(movie)
                    updateUI()
                    true
                }
                container.addView(tv)
            }
        }
        updateUI()
    }
}