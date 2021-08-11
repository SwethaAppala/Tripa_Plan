package uk.ac.tees.aad.W9299136;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlacesActivity extends AppCompatActivity {

    MaterialSearchBar searchBar;
    FusedLocationProviderClient fusedLocationProviderClient;
    PlacesClient placesClient;
    List<AutocompletePrediction> perditionList;
    AutocompleteSessionToken token;

    double selectLongitude;
    double selectLatitude;
    String locationName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        searchBar = findViewById(R.id.searchBar);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(PlacesActivity.this);
        Places.initialize(PlacesActivity.this, "AIzaSyB8Ba8J_tPh2BVjpMaFoUvb_NQ1NNaIiqw");
        placesClient = Places.createClient(PlacesActivity.this);
        token = AutocompleteSessionToken.newInstance();


        UseMaterialSearch();
    }

    private void UseMaterialSearch() {
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
//
//                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
//
//                }
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    searchBar.closeSearch();
                }
            }
        });


        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    searchBar.clearSuggestions();
                }
                getSuggestionFromPlaces(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= perditionList.size()) {
                    return;
                } else {


                    AutocompletePrediction clickedPrediction = perditionList.get(position);
                    String suggestion = searchBar.getLastSuggestions().get(position).toString();
                    searchBar.setText(suggestion);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            searchBar.clearSuggestions();
                        }
                    }, 1000);

                    hideKeyboard();

                    String placeID = clickedPrediction.getPlaceId();
                    List<Place.Field> placeField = Arrays.asList(Place.Field.LAT_LNG);
                    FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeID, placeField).build();
                    placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @Override
                        public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {


                            Place place = fetchPlaceResponse.getPlace();
                            LatLng latLng = place.getLatLng();
                            if (latLng != null) {
//                                MarkerOptions markerOptions = new MarkerOptions();
//                                markerOptions.position(latLng);
//                                mGoogleMap.addMarker(markerOptions);
//                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                                selectLatitude = latLng.latitude;
                                selectLongitude = latLng.longitude;
                                locationName=place.getName();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PlacesActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void getSuggestionFromPlaces(String s) {

        FindAutocompletePredictionsRequest PR = FindAutocompletePredictionsRequest.builder()
                .setCountry("uk").setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(s)
                .build();
        placesClient.findAutocompletePredictions(PR).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                if (task.isSuccessful()) {
                    FindAutocompletePredictionsResponse perdictionRespose = task.getResult();
                    if (PR != null) {
                        perditionList = perdictionRespose.getAutocompletePredictions();
                        List<String> suggestionList = new ArrayList<>();

                        //assign places search bar
                        for (int i = 0; i < perditionList.size(); i++) {
                            AutocompletePrediction perdiction = perditionList.get(i);
                            suggestionList.add(perdiction.getFullText(null).toString());
                        }
                        searchBar.updateLastSuggestions(suggestionList);
                        if (!searchBar.isSuggestionsVisible()) {
                            searchBar.showSuggestionsList();
                        }
                    }

                } else {
                    Log.d("places", "onComplete: " + task.getException());
                    Toast.makeText(PlacesActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}