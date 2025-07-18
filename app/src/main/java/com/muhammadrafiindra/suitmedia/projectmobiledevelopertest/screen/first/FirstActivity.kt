package com.muhammadrafiindra.suitmedia.projectmobiledevelopertest.screen.first

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.muhammadrafiindra.suitmedia.projectmobiledevelopertest.R
import com.muhammadrafiindra.suitmedia.projectmobiledevelopertest.screen.second.SecondActivity

class FirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        window.exitTransition = Slide(Gravity.LEFT).apply {
            duration = 300
        }

        val etName = findViewById<EditText>(R.id.etName)
        val etSentence = findViewById<EditText>(R.id.etSentence)
        val btnCheck = findViewById<Button>(R.id.btnCheck)
        val btnNext = findViewById<Button>(R.id.btnNext)

        btnCheck.setOnClickListener {
            val sentence = etSentence.text.toString()
            if (sentence.isBlank()) {
                showDialog("Please enter a sentence")
            } else {
                val isPalindrome = checkPalindrome(sentence)
                showDialog(if (isPalindrome) "isPalindrome" else "not palindrome")
            }
        }

        btnNext.setOnClickListener {
            val name = etName.text.toString()
            if (name.isBlank()) {
                showDialog("Please enter your name")
            } else {
                val intent = Intent(this, SecondActivity::class.java)
                intent.putExtra("name", name)

                val options = ActivityOptions.makeSceneTransitionAnimation(this)
                startActivity(intent, options.toBundle())
            }
        }
    }

    private fun checkPalindrome(text: String): Boolean {
        val filtered = text.filter { it.isLetterOrDigit() }.lowercase()
        return filtered == filtered.reversed()
    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create()
            .show()
    }
}

