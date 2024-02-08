package com.example.cadeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cadeapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map_Fragment extends Fragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_,container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                LatLng mapRedonda = new LatLng(20.639977, -99.907046);
                googleMap.addMarker(new MarkerOptions().position(mapRedonda).title("Viñedo laRedonda"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mapRedonda));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapRedonda,12));

                LatLng mapFreixenet = new LatLng(20.697373, -99.877498);
                googleMap.addMarker(new MarkerOptions().position(mapFreixenet).title("Viñedo Freixenet"));

                LatLng mapLobo = new LatLng(20.700184, -100.201149);
                googleMap.addMarker(new MarkerOptions().position(mapLobo).title("Viñedo Puerta del Lobo"));

                LatLng mapCote = new LatLng(20.696159, -99.880337);
                googleMap.addMarker(new MarkerOptions().position(mapCote).title("Viñedo De Cote"));




            }
        });
        return view;
    }
}