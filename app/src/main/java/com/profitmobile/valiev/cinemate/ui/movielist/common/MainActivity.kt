package com.profitmobile.valiev.cinemate.ui.movielist.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation.*
import androidx.navigation.ui.setupWithNavController
import com.profitmobile.valiev.cinemate.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector()= dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        // При использовании Dagger  инъекции
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val toolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        // BottomNavigationView NavController.
        val navController: NavController = findNavController(this, R.id.movies_nav_host_fragment)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.main_bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)
    }
}
