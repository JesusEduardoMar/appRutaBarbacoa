package com.example.cadeapp;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cadeapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class Map_Fragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private static final float DEFAULT_ZOOM = 18f;
    private GoogleMap mMap;
    private FirebaseFirestore db;
    private Spinner placesSpinner;
    private List<String> placesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);
        supportMapFragment.getMapAsync(this);
        placesSpinner = view.findViewById(R.id.spinner);
        db = FirebaseFirestore.getInstance(); // Firestore
        placesList = new ArrayList<>(); // Inicialización de la lista

        // Configuramos el listener del Spinner
        placesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position != 0) { // Verificamo si no se ha seleccionado el título
                    // Obtenemos las coordenadas del lugar seleccionado desde la bd
                    db.collection("barbacoas")
                            .whereEqualTo("nombre_barbacoa", placesList.get(position))
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    GeoPoint location = documentSnapshot.getGeoPoint("marcador");
                                    if (location != null) {
                                        moveCameraToLocation(location.getLatitude(), location.getLongitude());
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Por si hay algún error
                            });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Obtenemos y mostramos la lista completa de lugares de Firestore
        getAndShowAllPlaces();

        return view;
    }
    private void moveCameraToLocation(double latitude, double longitude) {
        LatLng location = new LatLng(latitude, longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
    }
    private void getAndShowAllPlaces() {
        // Obtenemos la lista completa de lugares de barbacoa en Firestore y actualizamos el Spinner
        db.collection("barbacoas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        placesList.clear(); // Limpiamos la lista antes de actualizarla
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String placeName = document.getString("nombre_barbacoa");
                            placesList.add(placeName);
                        }
                        // Actualizamos el adaptador del Spinner con la lista completa de lugares obtenidos de Firestore
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, placesList);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        placesSpinner.setAdapter(spinnerAdapter);
                    } else {
                        Log.e(TAG, "Error al obtener la lista de lugares desde Firestore", task.getException());
                    }
                });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Se llama cuando el mapa está listo para ser utilizado

        // Asignamos la instancia del mapa recibida a la variable mMap
        mMap = googleMap;

        // Obtenemos la latitud y la longitud desde Firestore
        db.collection("barbacoas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                // Obtener datos de Firestore
                                GeoPoint location = document.getGeoPoint("marcador");
                                if (location != null) { // Verificamos si el GeoPoint de firestore no es nulo
                                    String title = document.getString("nombre_barbacoa");

                                    // Agregamos un market (marcador) al mapa
                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.addMarker(new MarkerOptions().position(latLng).title(title).snippet("¿Cómo llegar?"));
                                } else {
                                    // Manejamos el caso cuando el GeoPoint es nulo
                                    Log.e(TAG, "El campo 'ubicacion' es nulo para el documento: " + document.getId());
                                }
                            } catch (Exception e) {
                                // Manejamos cualquier excepción al obtener los datos del documento
                                Log.e(TAG, "Error al obtener datos del documento: " + document.getId(), e);
                            }
                        }
                    } else {
                        // Manejamos errores al obtener la lista de documentos
                        Log.e(TAG, "Error al obtener marcadores desde Firestore", task.getException());
                    }
                });

        // Configuramos el listener para los clicks en las markets (marcadores)
        mMap.setOnMarkerClickListener(this);

        // Configuramos el listener para los clicks en las ventanas de info de los marcadores
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    // Llamamos a este método cuando se hace click en un marcador
    public boolean onMarkerClick(Marker marker) {

        // Mostramos la ventana de información del marcador
        marker.showInfoWindow();

        // Verificamos si el zoom actual del mapa es menor que el zoom predeterminado
        if (mMap.getCameraPosition().zoom < DEFAULT_ZOOM) {
            // Animamos la cámara para hacer zoom en la posición del marcador con el zoom predeterminado
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), DEFAULT_ZOOM));
        } else {
            // Animamos la cámara para moverse a la posición del marcador sin cambiar el zoom
            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        }

        return true;
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        // Verificamos si la ubicación está activada
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isLocationEnabled) {
            // Si la ubicación no está activada, se muestra un diálogo y pide al usuario que active la ubicación
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Para obtener direcciones, necesitamos tu ubicación para mejorar la precisión de los resultados.")
                    .setCancelable(false)
                    .setPositiveButton("Activar ubicación", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Configuración de ubicación
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            // Si la ubicación está activada, se abre Google Maps
            LatLng location = marker.getPosition();
            String title = marker.getTitle();
            Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=mi+ubicacion&daddr=" + title);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
    }
}

