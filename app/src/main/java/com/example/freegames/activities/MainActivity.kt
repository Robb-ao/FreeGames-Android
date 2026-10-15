package com.example.freegames.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freegames.R
import com.example.freegames.adapters.GameAdapter
import com.example.freegames.data.FreeToGameApi
import com.example.freegames.data.Game
import com.example.freegames.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var adapter: GameAdapter
    var gameList: List<Game> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Configuramos el recyclerView
        adapter = GameAdapter(gameList) { position ->
            val game = gameList[position]
            Toast.makeText(this, game.title, Toast.LENGTH_SHORT).show()
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        //Obtener los juegos del API
        val api = FreeToGameApi.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            gameList = api.getGamesList()

            CoroutineScope(Dispatchers.Main).launch {
                adapter.updateData(gameList)
            }
        }
    }
}
