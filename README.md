# Capstone Project - 상상 Compass
한성대학교 신입생 또는 외부인 상대로 교내 건물 및 주요 시설의 위치를 모르는 다수를 위한 학교 길찾기 서비스 앱

# 프로젝트 소개
교내 길찾기 서비스를 제공하는데 있어 지도상으로 경로를 나타내주는 뿐만아니라 AR를 통해 경로를 보여주는 서비스.

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

 ## 지도상에 경로 표시
 
 Mapbox Java SDK에서 제공하는 **Directions API**를 통해 출발지와 목적지에 대한 최소 거리 경로를 얻고 맵상에 그려준다.
```java
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
```
 
 ## 학교 주요 시설 리스트(Realtime Database)
 
 ## Unity를 통해 AR로 경로 안내
 
 
# 참고 자료 
 Mapbox Java SDK - <https://docs.mapbox.com/android/java/guides/>
 
 Mapbox SDK for Unity - <https://docs.mapbox.com/unity/maps/guides/>
