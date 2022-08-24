package com.samuel.vikitechnicaltest.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.samuel.vikitechnicaltest.BuildConfig
import com.samuel.vikitechnicaltest.R
import com.samuel.vikitechnicaltest.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor()
    : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}