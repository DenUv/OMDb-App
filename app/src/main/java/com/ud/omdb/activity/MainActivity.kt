package com.ud.omdb.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.textfield.TextInputLayout
import com.ud.omdb.BuildConfig
import com.ud.omdb.R
import com.ud.omdb.databinding.ActivityMainBinding
import com.ud.omdb.model.Response
import com.ud.omdb.network.NetworkClient
import com.ud.omdb.network.service.SearchService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var searchService: SearchService

    private lateinit var binding: ActivityMainBinding

    private lateinit var titleInput: TextInputLayout
    private lateinit var titleEditText: EditText
    private lateinit var message: TextView

    private lateinit var searchButton: Button

    private lateinit var response: Response


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDataBinding()
        initServices()
    }

    private fun initServices() {
        searchService = NetworkClient(this).createService(SearchService::class.java)
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        titleInput = binding.tilSearchField
        titleEditText = binding.etSearchField
        searchButton = binding.btnSearch
        message = binding.tvMessage

        binding.activity = this
    }

    fun submit() {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                response = searchService.findMovieByTitle(
                    BuildConfig.API_KEY,
                    titleEditText.text.toString()
                )
            }
            binding.response = response
        }
    }
}

