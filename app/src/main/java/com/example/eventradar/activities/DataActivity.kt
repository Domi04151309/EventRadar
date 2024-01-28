package com.example.eventradar.activities

import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventradar.R
import com.example.eventradar.adapters.ErrorAdapter
import com.example.eventradar.adapters.LoadingAdapter
import com.example.eventradar.adapters.SimpleListAdapter
import com.example.eventradar.data.AppDatabase
import com.example.eventradar.data.SimpleListItem
import com.example.eventradar.data.entities.UserWithAccount
import com.example.eventradar.helpers.OutOfScopeDialog
import com.example.eventradar.helpers.Preferences
import com.example.eventradar.interfaces.RecyclerViewHelperInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        recyclerView.adapter = LoadingAdapter()

        CoroutineScope(Dispatchers.Main).launch {
            val account =
                AppDatabase.getInstance(this@DataActivity)
                    .userDao()
                    .get(Preferences.getUserId(this@DataActivity))

            if (account != null) {
                onAccountLoaded(recyclerView, account)
            } else {
                recyclerView.adapter = ErrorAdapter()
            }
        }

        findViewById<Button>(R.id.delete).setOnClickListener {
            onDeleteClicked()
        }
    }

    private fun onAccountLoaded(
        recyclerView: RecyclerView,
        account: UserWithAccount,
    ) {
        recyclerView.adapter =
            SimpleListAdapter(
                listOf(
                    SimpleListItem(
                        account.user.name,
                        resources.getString(R.string.name),
                        R.drawable.ic_circle_person,
                    ),
                    SimpleListItem(
                        account.user.surname,
                        resources.getString(R.string.surname),
                        R.drawable.ic_circle_person,
                    ),
                    SimpleListItem(
                        DateFormat.getDateFormat(this).format(account.user.birthdate),
                        resources.getString(R.string.birthdate),
                        R.drawable.ic_circle_calendar_today,
                    ),
                    SimpleListItem(
                        account.account.eMail,
                        resources.getString(R.string.mail),
                        R.drawable.ic_circle_mail,
                    ),
                    SimpleListItem(
                        account.account.phone,
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
