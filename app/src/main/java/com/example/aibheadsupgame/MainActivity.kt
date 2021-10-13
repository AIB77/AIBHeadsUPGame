package com.example.aibheadsupgame

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Surface
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class MainActivity : AppCompatActivity()
{


    private lateinit var TextViewTime: TextView
    private lateinit var LinearLayoutTop: LinearLayout
    private lateinit var LinearLayoutCelebrity: LinearLayout
    private lateinit var TextViewName: TextView
    private lateinit var TextViewTaboo1: TextView
    private lateinit var TextViewTaboo2: TextView
    private lateinit var TextViewTaboo3: TextView
    private lateinit var LinearLayoutMain: LinearLayout
    private lateinit var TextViewMain: TextView
    private lateinit var BTNStart: Button

    private var gameActive = false
    private lateinit var celebrities: ArrayList<JSONObject>
    private var celeb = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        LinearLayoutTop = findViewById(R.id.LinearLayout1)
        LinearLayoutMain = findViewById(R.id.LinearLayoutMain)
        LinearLayoutCelebrity = findViewById(R.id.LinearLayoutCelebrity)
        TextViewTime = findViewById(R.id.TextViewTime)
        TextViewName = findViewById(R.id.TextViewCName)
        TextViewTaboo1 = findViewById(R.id.TextViewTaboo1)
        TextViewTaboo2 = findViewById(R.id.TextViewTaboo2)
        TextViewTaboo3 = findViewById(R.id.TextViewTaboo3)
        TextViewMain = findViewById(R.id.TextViewMain)
        BTNStart = findViewById(R.id.ButtonStart)


        BTNStart.setOnClickListener{
            RequestAPI()
        }

        celebrities = arrayListOf()
    }

    override fun onConfigurationChanged(newConfig: Configuration)
    {
        super.onConfigurationChanged(newConfig)
        val rotation = windowManager.defaultDisplay.rotation
        if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
        {
            if(gameActive)
            {
                celeb++
                NewCelebrity(celeb)
                updateStatus(false)
            }else
            {
                updateStatus(false)
            }
        }else
        {
            if(gameActive)
            {
                updateStatus(true)
            }else
            {
                updateStatus(false)
            }
        }
    }

    private fun newTimer()
    {
        if(!gameActive)
        {
            gameActive = true
            TextViewMain.text = "Rotate Device"
            BTNStart.isVisible = false
            val rotation = windowManager.defaultDisplay.rotation
            if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
            {
                updateStatus(false)
            }else
            {
                updateStatus(true)
            }

            object : CountDownTimer(60000, 1000)
            {
                override fun onTick(millisUntilFinished: Long)
                {
                    TextViewTime.text = "Time: ${millisUntilFinished / 1000}"
                }

                override fun onFinish()
                {
                    gameActive = false
                    TextViewTime.text = "Time: --"
                    TextViewMain.text = "Heads Up!"
                    BTNStart.isVisible = true
                    updateStatus(false)
                }
            }.start()
        }
    }

    private fun NewCelebrity(id: Int)
    {
        if(id < celebrities.size)
        {
            TextViewName.text = celebrities[id].getString("name")
            TextViewTaboo1.text = celebrities[id].getString("taboo1")
            TextViewTaboo2.text = celebrities[id].getString("taboo2")
            TextViewTaboo3.text = celebrities[id].getString("taboo3")
        }
    }

    private fun RequestAPI()
    {
        CoroutineScope(Dispatchers.IO).launch{
            val data = async{
                getCelebrities()
            }.await()
            if(data.isNotEmpty())
            {
                withContext(Dispatchers.Main)
                {
                    parseJSON(data)
                    celebrities.shuffle()
                    NewCelebrity(0)
                    newTimer()
                }
            }
        }
    }

    private suspend fun parseJSON(result: String)
    {
        withContext(Dispatchers.Main)
        {
            celebrities.clear()
            val jsonArray = JSONArray(result)
            for(i in 0 until jsonArray.length())
            {
                celebrities.add(jsonArray.getJSONObject(i))
            }
        }
    }

    private fun getCelebrities(): String
    {
        var response = ""
        try {
            response = URL("https://dojo-recipes.herokuapp.com/celebrities/")
                .readText(Charsets.UTF_8)
        }catch (e: Exception)
        {
            println("Error: $e")
        }
        return response
    }

    private fun updateStatus(showCelebrity: Boolean)
    {
        if(showCelebrity)
        {
            LinearLayoutCelebrity.isVisible = true
            LinearLayoutMain.isVisible = false
        }else
        {
            LinearLayoutCelebrity.isVisible = false
            LinearLayoutMain.isVisible = true
        }
    }
}
/////**** I Did See The Solution Before I did my Assignment so my code is similar to original*****/////