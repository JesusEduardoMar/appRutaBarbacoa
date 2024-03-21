package com.example.cadeapp;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
    private List<String> placesList;
    private List<String> pulqueList;
    private TextView textViewPlaces;
    private TextView textViewPulques;

    private List<Marker> markerList = new ArrayList<>(); // Lista para almacenar los marcadores
    //mantenemos una lista de los marcadores creados y buscamos el marcador correspondiente en esa lista.
    private List<Marker> markerpList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_, container, false);

        // Inicializar FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);
        supportMapFragment.getMapAsync(this);
        textViewPlaces = view.findViewById(R.id.barba); // TextView
        textViewPulques = view.findViewById(R.id.pulque); // TextView

        placesList = new ArrayList<>(); // Inicialización de la lista
        pulqueList = new ArrayList<>(); // Inicialización de la lista

        // Configuramos el OnClickListener del TextView
        textViewPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAndShowAllPlaces(); // Obtener y mostrar todos los lugares antes de mostrar el diálogo
            }
        });

        textViewPulques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAndShowAllPulques(); // Obtener y mostrar todos los lugares antes de mostrar el diálogo
            }
        });
        return view;
    }
    // Mostramos un diálogo con los lugares que hay
    private void showPlacesListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogBasicCustomStyle);

        // Crear un TextView personalizado para el título
        TextView title = new TextView(getActivity());
        title.setText("¿A dónde quieres ir?");
        title.setGravity(Gravity.CENTER); // Centrar el texto en el TextView
        title.setTextSize(20); // Tamaño del texto del título (ajusta según sea necesario)
        title.setPadding(10,55,10,5);
        title.setTextColor(Color.parseColor("#FFFFFF"));

        // Establecer el TextView personalizado como el título del AlertDialog
        builder.setCustomTitle(title);

        // Creamos un adaptador para la lista de lugares
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.simple_list_item, placesList);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            // Método para cuando se hace clicki en un lugar de la lista
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                // Obtenemos el lugar seleccionado en la lista
                String selectedPlace = placesList.get(position);
                // Obtener las coordenadas del lugar seleccionado desde la base de datos y luego mover la cámara a esa ubicación
                db.collection("barbacoas")
                        .whereEqualTo("nombre_barbacoa", selectedPlace)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                GeoPoint location = documentSnapshot.getGeoPoint("marcador");
                                if (location != null) {
                                    moveCameraToLocation(location.getLatitude(), location.getLongitude());
                                    String title = documentSnapshot.getString("nombre_barbacoa");

                                    // Buscar el marcador correspondiente en la lista
                                    // Cuando seleccionamos un elemento, iteramos sobre markerlist para encontrar el marcador con el mismo titulo
                                    // al llamar a onMarkerClick simula el comportamiento de hacer click en el marcador
                                    for (Marker marker : markerList) {
                                        if (marker.getTitle().equals(title)) {
                                            onMarkerClick(marker); // Llamar al método onMarkerClick con el marcador correspondiente
                                            break;
                                        }
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            //Por si existe algún error
                        });
            }
        });
        // Mostramos el diálogo con los lugares
        builder.show();
    }

    private void moveCameraToLocation(double latitude, double longitude) {
        LatLng location = new LatLng(latitude, longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
    }

    private void getAndShowAllPlaces() {
        // Obtenemos la lista completa de lugares de barbacoa en Firestore y actualizamos el TextView
        db.collection("barbacoas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        placesList.clear(); // Limpiamos la lista antes de actualizarla
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String placeName = document.getString("nombre_barbacoa");
                            placesList.add(placeName);
                        }
                        // Luego de obtener los lugares, mostramos el diálogo con la lista
                        showPlacesListDialog();
                    } else {
                        Log.e(TAG, "Error al obtener lugares desde Firestore", task.getException());
                    }
                });
    }

    private void getAndShowAllPulques() {
        // Obtenemos la lista completa de lugares de barbacoa en Firestore y actualizamos el TextView
        db.collection("pulques")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        placesList.clear(); // Limpiamos la lista antes de actualizarla
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String placeName = document.getString("nombre_pulque");
                            placesList.add(placeName);
                        }
                        // Luego de obtener los lugares, mostramos el diálogo con la lista
                        showPlacesListDialogpulque();
                    } else {
                        Log.e(TAG, "Error al obtener lugares desde Firestore", task.getException());
                    }
                });
    }

    private void showPlacesListDialogpulque() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogBasicCustomStyle);

            // Crear un TextView personalizado para el título
            TextView title = new TextView(getActivity());
            title.setText("¿A dónde quieres ir?");
            title.setGravity(Gravity.CENTER); // Centrar el texto en el TextView
            title.setTextSize(20); // Tamaño del texto del título (ajusta según sea necesario)
            title.setPadding(10,55,10,5);
            title.setTextColor(Color.parseColor("#FFFFFF"));

            // Establecer el TextView personalizado como el título del AlertDialog
            builder.setCustomTitle(title);

            // Creamos un adaptador para la lista de lugares
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.simple_list_item, placesList);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                // Método para cuando se hace clicki en un lugar de la lista
                @Override
                public void onClick(DialogInterface dialogInterface, int position) {
                    // Obtenemos el lugar seleccionado en la lista
                    String selectedPlace = placesList.get(position);
                    // Obtener las coordenadas del lugar seleccionado desde la base de datos y luego mover la cámara a esa ubicación
                    db.collection("pulques")
                            .whereEqualTo("nombre_pulque", selectedPlace)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    GeoPoint location = documentSnapshot.getGeoPoint("marcador");
                                    if (location != null) {
                                        moveCameraToLocation(location.getLatitude(), location.getLongitude());
                                        String title = documentSnapshot.getString("nombre_pulque");

                                        // Buscar el marcador correspondiente en la lista
                                        // Cuando seleccionamos un elemento, iteramos sobre markerlist para encontrar el marcador con el mismo titulo
                                        // al llamar a onMarkerClick simula el comportamiento de hacer click en el marcador
                                        for (Marker marker : markerpList) {
                                            if (marker.getTitle().equals(title)) {
                                                onMarkerClick(marker); // Llamar al método onMarkerClick con el marcador correspondiente
                                                break;
                                            }
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                //Por si existe algún error
                            });
                }
            });
            // Mostramos el diálogo con los lugares
            builder.show();
        }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng jardin = new LatLng(20.694695, -99.814685);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(jardin));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(jardin,13));
        mMap = googleMap;

        // Obtenemos la latitud y la longitud desde Firestore y agregamos marcadores al mapa
        db.collection("barbacoas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                // Obtener datos de Firestore
                                GeoPoint location = document.getGeoPoint("marcador");
                                if (location != null) {
                                    String title = document.getString("nombre_barbacoa");

                                    // cada que creamos un marcador tmb lo agregamos a la lista
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                            .title(title)
                                            .snippet("¿Cómo llegar?")
                                            .icon(bitmapDescriptor(getActivity().getApplicationContext(), R.drawable.oveja)));
                                    markerList.add(marker); // Agregar el marcador a la lista
                                } else {
                                    Log.e(TAG, "El campo 'ubicacion' es nulo para el documento: " + document.getId());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error al obtener datos del documento: " + document.getId(), e);
                            }
                        }
                    } else {
                        Log.e(TAG, "Error al obtener marcadores desde Firestore", task.getException());
                    }
                });

        // Obtenemos la latitud y la longitud desde Firestore y agregamos marcadores al mapa
        db.collection("pulques")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                // Obtener datos de Firestore
                                GeoPoint location = document.getGeoPoint("marcador");
                                if (location != null) {
                                    String title = document.getString("nombre_pulque");

                                    // cada que creamos un marcador tmb lo agregamos a la lista
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                            .title(title)
                                            .snippet("¿Cómo llegar?")
                                            .icon(bitmapDescriptor(getActivity().getApplicationContext(), R.drawable.pulque1)));
                                    markerpList.add(marker); // Agregar el marcador a la lista
                                } else {
                                    Log.e(TAG, "El campo 'ubicacion' es nulo para el documento: " + document.getId());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error al obtener datos del documento: " + document.getId(), e);
                            }
                        }
                    } else {
                        Log.e(TAG, "Error al obtener marcadores desde Firestore", task.getException());
                    }
                });

        // Configuramos el listener para los clicks en los marcadores
        mMap.setOnMarkerClickListener(this);

        // Configuramos el listener para los clicks en las ventanas de info de los marcadores
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();

        if (mMap.getCameraPosition().zoom < DEFAULT_ZOOM) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), DEFAULT_ZOOM));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        }
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isLocationEnabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Para obtener direcciones, necesitamos tu ubicación para mejorar la precisión de los resultados.")
                    .setCancelable(false)
                    .setPositiveButton("Activar ubicación", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
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
            LatLng location = marker.getPosition();
            String title = marker.getTitle();
            Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?&daddr=" + title);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
    }
    private BitmapDescriptor bitmapDescriptor(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}