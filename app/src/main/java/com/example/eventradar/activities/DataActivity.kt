package com.example.eventradar.activities

import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventradar.R
import com.example.eventradar.adapters.SimpleListAdapter
import com.example.eventradar.data.SimpleListItem
import com.example.eventradar.helpers.OutOfScopeDialog
import com.example.eventradar.interfaces.RecyclerViewHelperInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Aktivität für die Nutzerdaten.
 */
class DataActivity : BaseActivity(), RecyclerViewHelperInterface {
    /**
     * Initialisiert die Nutzerdaten-Aktivität und konfiguriert Event-Handler für die Löschoption.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)

        val recyclerView = findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter =
            SimpleListAdapter(
                listOf(
                    SimpleListItem(
                        resources.getString(R.string.scope),
                        resources.getString(R.string.name),
                        R.drawable.ic_circle_person,
                    ),
                    SimpleListItem(
                        resources.getString(R.string.scope),
                        resources.getString(R.string.surname),
                        R.drawable.ic_circle_person,
                    ),
                    SimpleListItem(
                        resources.getString(R.string.scope),
                        resources.getString(R.string.birthdate),
                        R.drawable.ic_circle_calendar_today,
                    ),
                    SimpleListItem(
                        resources.getString(R.string.scope),
                        resources.getString(R.string.mail),
                        R.drawable.ic_circle_mail,
                    ),
                    SimpleListItem(
                        resources.getString(R.string.scope),
                        resources.getString(R.string.phone),
                        R.drawable.ic_circle_call,
                    ),
                    SimpleListItem(
                        resources.getString(R.string.password_placeholder),
                        resources.getString(R.string.password),
                        R.drawable.ic_circle_key,
                    ),
                    SimpleListItem(
                        "",
                        resources.getString(R.string.edit_data_hint),
                    ),
                ),
                this,
            )

        findViewById<Button>(R.id.delete).setOnClickListener {
            onDeleteClicked()
        }
    }

    private fun onDeleteClicked() {
        MaterialAlertDialogBuilder(this).setTitle(R.string.delete_account)
            .setMessage(R.string.delete_account_summary)
            .setView(
                layoutInflater.inflate(
                    R.layout.dialog_delete_account,
                    findViewById(R.id.root),
                    false,
                ),
            )
            .setPositiveButton(R.string.delete) { _, _ ->
                OutOfScopeDialog.show(this)
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .show()
    }

    /**
     * Reagiert auf Klickereignisse in der Optionsliste.
     */
    override fun onItemClicked(position: Int) {
        OutOfScopeDialog.show(this)
    }
}
