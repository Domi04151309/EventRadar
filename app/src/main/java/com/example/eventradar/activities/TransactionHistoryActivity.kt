package com.example.eventradar.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventradar.R
import com.example.eventradar.adapters.EmptyAdapter
import com.example.eventradar.adapters.LoadingAdapter
import com.example.eventradar.adapters.SimpleListAdapter
import com.example.eventradar.data.AppDatabase
import com.example.eventradar.helpers.Preferences
import com.example.eventradar.interfaces.RecyclerViewHelperInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Aktivität für den Zahlungsverlauf.
 */
class TransactionHistoryActivity : BaseActivity(), RecyclerViewHelperInterface {
    /**
     * Initialisiert die Zahlungsverlaufs-Aktivität.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)

        val recyclerView = findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = LoadingAdapter()
        CoroutineScope(Dispatchers.Main).launch {
            val tickets =
                AppDatabase.getInstance(this@TransactionHistoryActivity)
                    .ticketDao()
                    .getAll(Preferences.getUserId(this@TransactionHistoryActivity))

            recyclerView.adapter =
                if (tickets.isNotEmpty()) {
                    SimpleListAdapter(
                        tickets.map { it.toTransactionHistoryItem(resources) },
                        this@TransactionHistoryActivity,
                    )
                } else {
                    EmptyAdapter()
                }
        }
    }

    /**
     * Behandelt Klickereignisse auf Listenelemente.
     */
    override fun onItemClicked(position: Int) {
        // Do nothing.
    }
}
