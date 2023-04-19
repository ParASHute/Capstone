package com.example.myapplication;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;


import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Mapbox Core Library 사용해 기기 위치가 변경될 때마다 업데이트
 */

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private MainActivityLocationCallback callback = new MainActivityLocationCallback(this);
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;

    public static double Lat; // 위도
    public static double Lon; // 경도

    // 37.58232, 127.01039
    public static double HSULat = 37.58232;
    public static double HsULon = 127.01039;

    private Marker destinationMarker;
    private Point destinationPosition;
    private Point originPosition;
    private Button startnavbtn;
    private MapboxDirections client;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private SymbolManager symbolManager;
    private Symbol symbol;
    private SymbolOptions symbolOptions;

    private static final String RED_MARKER = "marker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::onMapReady);

        Button mylocBtn = findViewById(R.id.btnMyLoc);
        mylocBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(Lat, Lon))
                        .zoom(17)
                        .bearing(0)
                        .tilt(0)
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);
                Toast.makeText(getApplicationContext(), String.format("내 위치로 이동합니다."), Toast.LENGTH_LONG).show();
            }
        });

        Button HSUlocBtn = findViewById(R.id.btnHSU);
        HSUlocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(HSULat, HsULon))
                        .zoom(17)
                        .bearing(0)
                        .tilt(0)
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);
                Toast.makeText(getApplicationContext(), String.format("학교 위치로 이동합니다."), Toast.LENGTH_LONG).show();
            }
        });
    }
    // onCreate end
   /* private void addMarkerToStyle(Style style) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_marker);
        style.addImage(RED_MARKER, bitmap, true);
    }*/

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.addOnMapClickListener(this::onMapClick);
        /*mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                addMarkerImageToStyle(style);
            }
        });*/
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/youngrockchoi/clgib5i6q000101o637a7nha5"), new Style.OnStyleLoaded() { // Mapbox Studio에서 편집한 내용은 여기서 다 저장됨
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                //addMarkerToStyle(style);
            }
        });
    }
    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        //Toast.makeText(MainActivity.this, String.format("Lat " + point.getLatitude() + " Lon " + point.getLongitude()), Toast.LENGTH_SHORT).show();
        //Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.red_marker);
        //mapboxMap.getStyle(this::addMarkerToStyle);

        if(symbol != null) {
            symbolManager.deleteAll();
        }

        symbolManager = new SymbolManager(mapView, mapboxMap, mapboxMap.getStyle());

        symbolManager.setIconAllowOverlap(true);
        symbolManager.setTextAllowOverlap(true);

        symbolOptions = new SymbolOptions()
                .withLatLng(point)
                .withIconImage(RED_MARKER)
                .withIconSize(2.0f);
        symbol = symbolManager.create(symbolOptions);
        return true;
    }

    // bitmap을 string으로 변환 메소드
   /* private String bitmapToString(Bitmap bitmap) {
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
       byte[] bytes = baos.toByteArray();
       String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
       return temp;

    }*/

    /*
    *  LocationComponent 초기화
    */
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // check if permissions are enabled and if not request
        // 권한이 활성화되어있는지 체크
        if(PermissionsManager.areLocationPermissionsGranted(this)) {
            // Get an instance of the component
            // Get LocationComponent 객체
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            //Set the LocationComponent activation options
            // LocationComponent 설정
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

            // Activate with the LocationComponentActivationOptions object
            // LocationComponentActivationOptions 오브젝트 활성화
            locationComponent.activateLocationComponent(locationComponentActivationOptions);
            //locationComponent.activateLocationComponent(this, style);

            // Enable to make component visible
            // 컴포넌트가 보일 수 있게 함.
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            // 컴포넌트의 카메라 모드를 설정
            locationComponent.setCameraMode(CameraMode.TRACKING); // 트랙킹 모드

            // Set the rendermode
            // 위치 표시기 설정. (COMPASS, GPS, NORMAL)
            locationComponent.setRenderMode(RenderMode.COMPASS);

            initLocationEngine();
        }else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /*
    * Set up the LocationEngine and the parameters for querying the device's location
    * 기기 위치를 query 하기 위한 파라미터와 LocationEngine 설정    .
    */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();
        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override   //위치권한
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onExplanationNeeded(List<String> list) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean b) {
        if(b) {
            enableLocationComponent(mapboxMap.getStyle());
        }else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }


   /* @Override
    public boolean onMapClick(@NonNull LatLng point) {
       *//* IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
        Icon red_marker = iconFactory.fromResource(R.drawable.red_marker);
        if (destinationMarker != null) {
            mapboxMap.removeMarker(destinationMarker);
        }
        destinationMarker = mapboxMap.addMarker(new MarkerOptions()
                .position(point)
                .icon(red_marker)
                .title("Marker")
        );*//*
        *//*
        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
        Icon red_marker = iconFactory.fromResource(R.drawable.red_marker);
        if(symbol != null) {
            symbolManager.deleteAll();
        }
        symbolManager = new SymbolManager(mapView, mapboxMap, mapboxMap.getStyle());

        symbolManager.setIconAllowOverlap(true);
        symbolManager.setTextAllowOverlap(true);

        symbolOptions = new SymbolOptions()
                .withLatLng(new LatLng(point.getLatitude(), point.getLongitude()))
                .withIconImage(RED_MARKER)
                .withIconSize(1.3f);
        symbol = symbolManager.create(symbolOptions);*//*

        destinationPosition = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        originPosition = Point.fromLngLat(Lon, Lat);

        getRoute_walking(originPosition, destinationPosition);

        startnavbtn.setEnabled(true);
        startnavbtn.setBackgroundResource(com.mapbox.mapboxsdk.R.color.mapbox_blue);

        return true;
    }*/

    private void getRoute_walking(Point originPosition, Point destinationPosition) {
        client = MapboxDirections.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .routeOptions(
                        RouteOptions.builder()
                                .coordinatesList(Arrays.asList(originPosition, destinationPosition))
                                .profile(DirectionsCriteria.PROFILE_WALKING)
                                .overview(DirectionsCriteria.OVERVIEW_FULL)
                                .build()
                ).build();
        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if(response.body() == null) {
                    return;
                }else if (response.body().routes().size() < 1) {
                    return;
                }
                // Print some info about the route
                currentRoute = response.body().routes().get(0);

                int time = (int) (currentRoute.duration()/60);
                double distances = (currentRoute.distance()/1000);
                distances = Math.round(distances*100)/100.0;

                Toast.makeText(getApplicationContext(), String.format("예상 시간 : " + String.valueOf(time)+" 분 \n" +
                        "목적지 거리 : " +distances+ " km"), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private static class MainActivityLocationCallback implements LocationEngineCallback<LocationEngineResult> {
        private final WeakReference<MainActivity> activityWeakReference;
        MainActivityLocationCallback(MainActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }
        @Override
        public void onSuccess(LocationEngineResult locationEngineResult) {
            MainActivity activity = activityWeakReference.get();
            if(activity != null) {
                Location location = locationEngineResult.getLastLocation();
                if(location == null) {
                    return;
                }

                Lat = locationEngineResult.getLastLocation().getLatitude();
                Lon = locationEngineResult.getLastLocation().getLongitude();

                if(activity.mapboxMap != null && locationEngineResult.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(locationEngineResult.getLastLocation());
                }
            }
        }

        @Override
        public void onFailure(@NonNull Exception e) {
            Log.e("LocationChangeActivity", e.getLocalizedMessage());
            MainActivity activity = activityWeakReference.get();
            if(activity != null) {
                Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Prevent leaks
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}