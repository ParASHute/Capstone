package com.example.capston;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
// 메인화면
public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private NavigationActivityLocationCallback callback = new NavigationActivityLocationCallback(this);
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;

    public static double Lat; // 위도
    public static double Lon; // 경도

    // 37.58232, 127.01039 학교 중앙
    public static double HSULat = 37.58232;
    public static double HSULon = 127.01039;
    public static double HSULib_Lat = 37.58252;
    public static double HSULib_Lon = 127.01073;

    private MapboxDirections client;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    /*private SymbolManager symbolManager;
    private Symbol symbol;*/
    private Point destinationPosition;
    private Point originPosition;
    private ImageButton mylocBtn;
    private ImageButton HSUlocBtn;

    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private static final String MARKER = "marker";

    private ViewPager viewPager;
    private CardViewAdapter cardViewAdapter;
    private List<Model> models;
    private TextView search_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_navigation);

        // Setup the MapView
        mapView = findViewById(R.id.nav_mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::onMapReady);

        mylocBtn = findViewById(R.id.btnMyLoc);
        HSUlocBtn = findViewById(R.id.btnHSU);

        mylocBtn.setOnClickListener(view -> {
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(Lat, Lon))
                    .zoom(16)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);
            Toast.makeText(getApplicationContext(), String.format("내 위치로 이동합니다."), Toast.LENGTH_LONG).show();
            // Toast.makeText(getApplicationContext(),dest_Lat + " " + dest_Log, Toast.LENGTH_SHORT).show();
        });

        HSUlocBtn.setOnClickListener(view -> {
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(HSULat, HSULon))
                    .zoom(16)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);
            Toast.makeText(getApplicationContext(), String.format("학교 위치로 이동합니다."), Toast.LENGTH_LONG).show();
        });

        models = new ArrayList<>();
        models.add(new Model(R.drawable.changui, "창의관"));
        models.add(new Model(R.drawable.gonghaka, "공학관"));
        models.add(new Model(R.drawable.sangsang, "상상관"));
        models.add(new Model(R.drawable.mirae, "미래관"));
        models.add(new Model(R.drawable.tamgu, "탐구관"));
        
        cardViewAdapter = new CardViewAdapter(models, this);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setPadding(40, 0, 40, 0);
        viewPager.setPageMargin(getResources().getDisplayMetrics().widthPixels / -9);
        viewPager.setAdapter(cardViewAdapter);

        search_text = findViewById(R.id.search_text);
        search_text.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SearchingKey.class);
            startActivity(intent);
        });

    }
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.addOnMapClickListener(this::onMapClick);
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/youngrockchoi/clgib5i6q000101o637a7nha5"),
                style -> enableLocationComponent(style));
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        destinationPosition = Point.fromLngLat(point.getLongitude(), point.getLatitude()); // 클릭한 위치
        originPosition = Point.fromLngLat(Lon, Lat); // 현 위치

        if(mapboxMap != null) {
            mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/youngrockchoi/clgib5i6q000101o637a7nha5"), new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                    initSource(style);
                    initLayers(style);
                }
            });
        }
        getRoute_walking(originPosition, destinationPosition);
        return true;
    }

    private void initSource(@NonNull Style style) {
        style.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[] {
                Feature.fromGeometry(Point.fromLngLat(originPosition.longitude(), originPosition.latitude())),
                Feature.fromGeometry(Point.fromLngLat(destinationPosition.longitude(), destinationPosition.latitude()))}));
        style.addSource(iconGeoJsonSource);
    }

    /**
     * Add the route and marker icon layers to the map
     */
    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

// Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#009688"))
        );
        loadedMapStyle.addLayer(routeLayer);

// Add the red marker icon image to the map
        loadedMapStyle.addImage(RED_PIN_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.red_marker)));

// Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(RED_PIN_ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[] {0f, -9f})));
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // check if permissions are enabled and if not request
        // 권한이 활성화되어있는지 체크
        if(PermissionsManager.areLocationPermissionsGranted(this)) {
            // Get an instance of the component
            // Get LocationComponent 객체
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            LocationComponentOptions locationComponentOptions = LocationComponentOptions.builder(this)
                    .build();

            //Set the LocationComponent activation options
            // LocationComponent 설정
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .locationComponentOptions(locationComponentOptions)
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

                if(mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);
                            if(source != null) {
                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Toast.makeText(NavigationActivity.this, "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class NavigationActivityLocationCallback implements LocationEngineCallback<LocationEngineResult> {
        private final WeakReference<NavigationActivity> activityWeakReference;

        NavigationActivityLocationCallback(NavigationActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult locationEngineResult) {
            NavigationActivity activity = activityWeakReference.get();
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
            Log.e("NavigationActivity", e.getLocalizedMessage());
            NavigationActivity activity = activityWeakReference.get();
            if(activity != null) {
                Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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