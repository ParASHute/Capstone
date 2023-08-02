# Capstone Project - 상상 Compass
한성대학교 신입생 또는 외부인 상대로 교내 건물 및 주요 시설의 위치를 모르는 다수를 위한 학교 길찾기 서비스 앱

# 프로젝트 소개
교내 길찾기 서비스를 제공하는데 있어 지도상으로 경로를 나타내주는 뿐만아니라 AR를 통해 경로를 보여주는 서비스.

# 유튜브 요약 영상 
<https://www.youtube.com/watch?v=zbfbi9GLVPg>


[![Video Label](http://img.youtube.com/vi/zbfbi9GLVPg/0.jpg)](https://youtu.be/zbfbi9GLVPg)

# 개발 환경
 - Java 8
 - JDK 1.8.0
 - Android Studio Electric eel 2022.1.1
 - Android Gradle Plugin Version 7.4.0
 - Gradle Version 7.5
 - Unity 2019.4.40f1
 - Mapbox SDK for Unity 2.1.1
 - Mapbox SDK for Java(Android, Legacy) 9.7.1
 - Firebase Realtime Database

# 주요 기능

 ## LocationEngine 컴포넌트를 통해 현위치 알아내기. 
 
 LocationEngine 컴포넌트를 통해 디바이스의 마지막 위치를 알아낸다. 
```java
    // 실시간 위치 업데이트
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
```
## 위치 업데이트하는 콜백함수. 
```java
// LocationEngine 콜백함수
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
```
## 길찾기 메소드
```java
// 길찾기 메소드
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
                // Directions API가 요청이 정상적으로 되었을 때.
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
                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID); // 루트를 지도에 그려줌.
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
```
 
 ## 학교 주요 시설 리스트(Realtime Database)
 List 요소 
  - 빌딩(건물) 이름 (Build)
  - 장소 이름 (Office)
  - 층 수 (floor)
  - 장소 사진 (Profile)
  - 위도, 경도값 (Lat, Lon)
  ```java
  public class List {
     private String Profile;
     private String Build;
     private String Office;
     private String Floor;
     private String Lat;
     private String Lon;
    }
  ```
  ## RecycleView를 통해 리스트 구현.
  ![listview](https://github.com/ParASHute/Capstone/assets/55376183/1e6b3375-918b-4797-bf93-977776db530c)

  ## 리스트 목록 클릭 시 경로 안내 
   ```java
    // 길찾기가 실행되도록
    public void onNavStart() {
        destinationPosition = Point.fromLngLat(dest_Lon, dest_Lat); // intent로 받은 값을 목적지로 설정.
        originPosition = Point.fromLngLat(Lon, Lat);
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
        getRoute_walking(originPosition, destinationPosition); // 길찾기 메소드 실행.
    }
  ```
 
 ## Unity를 통해 AR로 경로 안내
https://github.com/devDevyne/Unity-AR-Navigation/blob/master/README.md
 
 
# 참고 자료 
 Mapbox Java SDK - <https://docs.mapbox.com/android/java/guides/>
 
 Mapbox SDK for Unity - <https://docs.mapbox.com/unity/maps/guides/>
