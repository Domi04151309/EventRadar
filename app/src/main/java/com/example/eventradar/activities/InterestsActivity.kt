package com.example.eventradar.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventradar.R
import com.example.eventradar.adapters.InterestListAdapter
import com.example.eventradar.adapters.LoadingAdapter
import com.example.eventradar.data.AppDatabase
import com.example.eventradar.data.entities.AccountInterest
import com.example.eventradar.data.entities.Interest
import com.example.eventradar.helpers.Preferences
import com.example.eventradar.interfaces.RecyclerViewHelperInterface
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Aktivität für die Auswahl und Verwaltung von Benutzerinteressen.
 */
class InterestsActivity : BaseActivity(), RecyclerViewHelperInterface {
    private var allInterests = listOf<Interest>()
    private lateinit var adapter: InterestListAdapter

    /**
     * Initialisiert die Interessenaktivität und setzt Event-Handler für Benutzerinteraktionen.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interests)

        val recyclerView = findViewById<RecyclerView>(R.id.interest_list)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = LoadingAdapter()

        CoroutineScope(Dispatchers.Main).launch {
            allInterests =
                AppDatabase.getInstance(this@InterestsActivity)
                    .interestDao()
                    .getAllInterests()
            adapter =
                InterestListAdapter(
                    allInterests,
                    this@InterestsActivity,
                )
            adapter.select(
                AppDatabase.getInstance(this@InterestsActivity)
                    .accountInterestDao()
                    .getUserInterests(Preferences.getUserId(this@InterestsActivity))
                    .map { it.interestId },
            )
            recyclerView.adapter = adapter
        }

        findViewById<FloatingActionButton>(R.id.continue_interests).setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val accountInterestDao = AppDatabase.getInstance(this@InterestsActivity).accountInterestDao()
                val userId = Preferences.getUserId(this@InterestsActivity)

                accountInterestDao.deleteUserInterests(userId)
                for (interest in adapter.getSelected()) {
                    accountInterestDao.insertAll(
                        AccountInterest(
                            userId,
                            interest.id,
                        ),
                    )
                }
            }
            startActivity(Intent(this@InterestsActivity, MainActivity::class.java))
            finish()
        }
    }

    /**
     * Überschreibt den Zurück-Button um den User-Flow zu gewährleisten.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClicked(position: Int) {
        adapter.toggle(position)
    }
}
