package com.example.freegames.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.freegames.R
import com.example.freegames.adapters.ScreenshotAdapter
import com.example.freegames.data.FreeToGameApi
import com.example.freegames.data.Game
import com.example.freegames.databinding.ActivityDetailBinding
import com.example.freegames.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_GAME_ID = "GAME_ID"
    }

    lateinit var binding: ActivityDetailBinding

    lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getIntExtra(EXTRA_GAME_ID, -1)

        //Obtener el juego del API en el id
        val api = FreeToGameApi.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            game = api.getGameById(id)
            CoroutineScope(Dispatchers.Main).launch {
                loadData()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }
    fun loadData() {
        supportActionBar?.title = game.title
        supportActionBar?.subtitle = game.developer

        //Hero
        Picasso.get().load(game.thumbnail).into(binding.thumbnailImageView)

        //Basic info
        binding.titleTextView.text = game.title
        binding.genreChip.text = game.genre

        val platformResId = if (game.platform.contains("Windows")){
            R.drawable.ic_platform_windows_desktop
        } else {
            R.drawable.ic_platform_web
        }
        binding.platformImageView.setImageResource(platformResId)

        //Description
        binding.descriptionTextView.text = game.description

        // Screenshots
        val adapter = ScreenshotAdapter(game.screenshots ?: emptyList())
        binding.screenshotsRecyclerView.adapter = adapter

        //System requirements


    }
}