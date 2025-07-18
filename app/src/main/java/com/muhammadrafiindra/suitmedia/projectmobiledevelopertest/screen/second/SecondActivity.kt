package com.muhammadrafiindra.suitmedia.projectmobiledevelopertest.screen.second


import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.Window
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.muhammadrafiindra.suitmedia.projectmobiledevelopertest.R
import com.muhammadrafiindra.suitmedia.projectmobiledevelopertest.screen.third.ThirdFragment
import com.muhammadrafiindra.suitmedia.projectmobiledevelopertest.screen.third.User

class SecondActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvNameFromFirst: TextView
    private lateinit var tvSelectedUser: TextView
    private lateinit var btnChooseUser: Button
    private lateinit var fragmentContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        window.enterTransition = Slide(Gravity.END).apply { duration = 300 }
        window.returnTransition = Slide(Gravity.START).apply { duration = 300 }

        val nameFromFirstScreen = intent.getStringExtra("name") ?: "Unknown"

        tvWelcome = findViewById(R.id.tvWelcome)
        tvNameFromFirst = findViewById(R.id.tvNameFromFirst)
        tvSelectedUser = findViewById(R.id.tvSelectedUser)
        btnChooseUser = findViewById(R.id.btnChooseUser)
        fragmentContainer = findViewById(R.id.fragmentContainer)

        tvNameFromFirst.text = nameFromFirstScreen

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val selectedUser = prefs.getString("selected_user_name", null)
        tvSelectedUser.text = selectedUser ?: "Selected User Name"

        btnChooseUser.setOnClickListener {
            showThirdFragment()
        }

        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentDetached(fm: FragmentManager, fragment: Fragment) {
                    if (fragment is ThirdFragment) {
                        fragmentContainer.visibility = View.GONE
                    }
                }
            },
            true
        )

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun showThirdFragment() {
        val fragment = ThirdFragment()
        fragment.setUserSelectionListener(object : ThirdFragment.UserSelectionListener {
            override fun onUserSelected(user: User) {
                val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                prefs.edit()
                    .putString("selected_user_name", "${user.first_name} ${user.last_name}")
                    .apply()

                tvSelectedUser.text = "${user.first_name} ${user.last_name}"
            }
        })

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()

        fragmentContainer.visibility = View.VISIBLE
    }
}