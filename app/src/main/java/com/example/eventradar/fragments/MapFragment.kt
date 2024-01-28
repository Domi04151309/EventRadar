package com.example.eventradar.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.eventradar.R
import com.example.eventradar.activities.MainActivity
import com.example.eventradar.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Locale

/**
 * Fragment zur Darstellung einer interaktiven Karte für Veranstaltungsorte.
 */
class MapFragment : Fragment() {
    private lateinit var map: MapView

    /**
     * Erstellt die Ansicht für das Kartenfragment und initialisiert die Suchleiste.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val root = inflater.inflate(R.layout.fragment_map, container, false)

        MainActivity.setupSearchBar(root)
        requestPermissions()

        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext()),
        )
        map = root.findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.minZoomLevel = MIN_ZOOM_LEVEL
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        map.controller.run {
            zoomTo(DEFAULT_ZOOM_LEVEL)
            animateTo(GeoPoint(START_LATITUDE, START_LONGITUDE))
        }
        map.overlays.add(
            MyLocationNewOverlay(GpsMyLocationProvider(context), map).apply {
                setPersonAnchor(PERSON_ANCHOR, PERSON_ANCHOR)
                setPersonIcon(
                    ContextCompat
                        .getDrawable(requireContext(), R.drawable.ic_map_location)
                        ?.toBitmap(PERSON_SIZE, PERSON_SIZE),
                )
                enableMyLocation()
                runOnFirstFix {
                    requireActivity().runOnUiThread {
                        map.controller.animateTo(myLocation)
                    }
                }
            },
        )

        CoroutineScope(Dispatchers.Main).launch {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            for ((event, address) in AppDatabase.getInstance(requireContext()).eventDao().getAll()) {
                geocoder.getFromLocationName(
                    address.toString(resources),
                    1,
                )?.firstOrNull()?.let { location ->
                    map.overlays.add(
                        Marker(map).apply {
                            position = GeoPoint(location.latitude, location.longitude)
                            title = event.title
                            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_circle_local_activity)
                        },
                    )
                }
            }
        }

        return root
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
                0,
            )
        }
    }

    /**
     * Aktualisiert die Kartenansicht.
     */
    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    /**
     * Pausiert die Kartenansicht.
     */
    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    companion object {
        private const val MIN_ZOOM_LEVEL = 5.0
        private const val DEFAULT_ZOOM_LEVEL = 15.0
        private const val START_LATITUDE = 49.0135165
        private const val START_LONGITUDE = 8.4018601
        private const val PERSON_SIZE = 48
        private const val PERSON_ANCHOR = 0.2f
    }
}
