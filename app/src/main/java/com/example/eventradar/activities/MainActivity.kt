package com.example.eventradar.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.eventradar.R
import com.example.eventradar.helpers.OutOfScopeDialog
import com.example.eventradar.helpers.Preferences
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.search.SearchBar

/**
 * Hauptaktivität, die die Navigation innerhalb der App steuert.
 */
class MainActivity : BaseActivity() {
    companion object {
        /**
         * Zeigt einen Dialog für das Konto-Management, abhängig vom Anmeldestatus des Benutzers.
         */
        private fun onAccountClicked(context: Context) {
            if (Preferences.isLoggedIn(context)) {
                MaterialAlertDialogBuilder(context).setTitle(R.string.logout)
                    .setMessage(R.string.logout_summary)
                    .setPositiveButton(R.string.logout) { _, _ ->
                        Preferences.setLoggedIn(context, Preferences.NO_ACCOUNT)
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    }
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .show()
            } else {
                context.startActivity(Intent(context, LoginActivity::class.java))
            }
        }

        /**
         * Initialisiert die Suchleiste in der Ansicht und konfiguriert deren Verhalten.
         */
        fun setupSearchBar(view: View) {
            view.findViewById<SearchBar>(R.id.search_bar).let {
                it.menu.getItem(0).setIcon(
                    if (Preferences.isLoggedIn(view.context)) {
                        R.drawable.ic_account_circle
                    } else {
                        R.drawable.ic_account_circle_off
                    },
                )
                it.setOnClickListener {
                    OutOfScopeDialog.show(view.context)
                }
                it.setOnMenuItemClickListener {
                    onAccountClicked(view.context)
                    true
                }
            }
        }
    }

    /**
     * Initialisiert die Hauptaktivität und konfiguriert die Navigationselemente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<BottomNavigationView>(R.id.nav_view).setupWithNavController(
            (
                supportFragmentManager.findFragmentById(
                    R.id.nav_host_fragment_activity_main,
                ) as NavHostFragment
            ).navController,
        )
    }
}
