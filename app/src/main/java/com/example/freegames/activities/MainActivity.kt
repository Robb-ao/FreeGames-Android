package com.example.freegames.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
    var filteredGameList: List<Game> = emptyList()

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
            val game = filteredGameList[position]

            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_GAME_ID, game.id)
            startActivity(intent)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        //Obtener los juegos del API
        val api = FreeToGameApi.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            gameList = api.getGamesList()
            filteredGameList = gameList

            CoroutineScope(Dispatchers.Main).launch {
                showData()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)

        val searchMenuItem = menu.findItem(R.id.menu_search)
        val searchView = searchMenuItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filteredGameList = gameList.filter {
                    it.title.contains(newText, true)
                }
                showData()
                return true
            }
        })

        return true
    }

    fun showData(){
        adapter.updateData(filteredGameList)
        supportActionBar?.subtitle = "Found ${filteredGameList.size} games"
    }
}

