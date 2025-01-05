package com.example.rain.utils;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.rain.Rain;
import com.example.rain.items.DailyWeatherItem;
import com.example.rain.items.FillingPredictionItem;
import com.example.rain.items.HourlyFillingPredictionItem;
import com.example.rain.items.HourlyWeatherItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherUtils {

    // ORDINE: baseUrl + forecast/current + key + city + days + other

    private final static String baseUrl = "https://api.weatherapi.com/v1";
    private final static String key = "4d9c1c64ddaa4009926173617241911"; // ?key=
    private final static String other = "&aqi=no&alerts=no";
    // per la location va anche bene scrivere 43.22686,81.4542 dove la prima è latitudine e la seconda longitudine

    // I dettagli del meteo vengono passati al WeatherCallback
    public static void getWeatherDetails(String location, WeatherCallback callback) {

        String tempUrl = baseUrl + "/forecast.json" + "?key=" + key + "&q=" + location + "&days=3" + other;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    List<HourlyWeatherItem> hourlyWeatherItemsToday;
                    List<HourlyWeatherItem> hourlyWeatherItemsTomorrow;
                    List<HourlyWeatherItem> hourlyWeatherItemsAfterTomorrow;
                    DailyWeatherItem todayWeather, tomorrowWeather, afterTomorrowWeather;

                    // arrivo fino alla lista dei 3 giorni
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject jsonObjectForecast = jsonResponse.getJSONObject("forecast");
                    JSONArray jsonArrayForecastday = jsonObjectForecast.getJSONArray("forecastday");

                    // riempio la lista delle ore di oggi
                    JSONObject jsonObjectForecastDaily = jsonArrayForecastday.getJSONObject(0);
                    JSONArray jsonArrayHours = jsonObjectForecastDaily.getJSONArray("hour");
                    hourlyWeatherItemsToday = getHourlyWeather(jsonArrayHours);
                    // meteo complessivo di oggi
                    JSONObject jsonObjectDay = jsonObjectForecastDaily.getJSONObject("day");
                    todayWeather = getDailyWeather(jsonObjectDay);

                    // riempio la lista delle ore di domani
                    jsonObjectForecastDaily = jsonArrayForecastday.getJSONObject(1);
                    jsonArrayHours = jsonObjectForecastDaily.getJSONArray("hour");
                    hourlyWeatherItemsTomorrow = getHourlyWeather(jsonArrayHours);
                    // meteo complessivo di domani
                    jsonObjectDay = jsonObjectForecastDaily.getJSONObject("day");
                    tomorrowWeather = getDailyWeather(jsonObjectDay);

                    // riempio la lista delle ore di dopodomani
                    jsonObjectForecastDaily = jsonArrayForecastday.getJSONObject(2);
                    jsonArrayHours = jsonObjectForecastDaily.getJSONArray("hour");
                    hourlyWeatherItemsAfterTomorrow = getHourlyWeather(jsonArrayHours);
                    // meteo complessivo di dopodomani
                    jsonObjectDay = jsonObjectForecastDaily.getJSONObject("day");
                    afterTomorrowWeather = getDailyWeather(jsonObjectDay);

                    callback.onSuccess(hourlyWeatherItemsToday,
                            hourlyWeatherItemsTomorrow,
                            hourlyWeatherItemsAfterTomorrow,
                            todayWeather,
                            tomorrowWeather,
                            afterTomorrowWeather);

                } catch (JSONException e) {
                    callback.onError("Errore nel parsing del JSON: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError("Errore nella richiesta: " + error.getMessage());
            }
        });

        Rain.getRequestQueue().add(stringRequest);
    }

    private static List<HourlyWeatherItem> getHourlyWeather(JSONArray jsonArrayHours) throws JSONException {

        List<HourlyWeatherItem> hourlyWeatherItems = new ArrayList<>();

        // scorro le ore e aggiungo i dati all'ArrayList
        for (int i = 0; i < 24; ++i) {
            JSONObject jsonObjectHour = jsonArrayHours.getJSONObject(i);

            String time = jsonObjectHour.getString("time");
            String hour = time.substring(time.length() - 5);

            JSONObject jsonObjectCondition = jsonObjectHour.getJSONObject("condition");
            String condition = jsonObjectCondition.getString("text");
            String iconUrl = jsonObjectCondition.getString("icon");

            double temp = jsonObjectHour.getDouble("temp_c");
            int chanceOfRain = jsonObjectHour.getInt("chance_of_rain");
            double precip = jsonObjectHour.getDouble("precip_mm");

            hourlyWeatherItems.add(new HourlyWeatherItem(hour, condition, iconUrl, temp, chanceOfRain, precip));
        }

        return hourlyWeatherItems;
    }

    private static DailyWeatherItem getDailyWeather(JSONObject jsonObjectDay) throws JSONException {

        JSONObject jsonObjectCondition = jsonObjectDay.getJSONObject("condition");
        String condition = jsonObjectCondition.getString("text");
        String iconUrl = jsonObjectCondition.getString("icon");

        double maxTemp = jsonObjectDay.getDouble("maxtemp_c");
        double minTemp = jsonObjectDay.getDouble("mintemp_c");
        double avgTemp = jsonObjectDay.getDouble("avgtemp_c");

        int chanceOfRain = jsonObjectDay.getInt("daily_chance_of_rain");
        double precip = jsonObjectDay.getDouble("totalprecip_mm");

        return new DailyWeatherItem(condition, iconUrl, maxTemp, minTemp, avgTemp, chanceOfRain, precip);
    }

    // La location viene passata al OneElementCallback<String>
    public static void getLocation(FirebaseFirestore db, FirebaseUser user, OneElementCallback<String> callback) {

        String userId = user.getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> location = (Map<String, Object>) documentSnapshot.get("location");
                            if (location.containsKey("latitude") && location.containsKey("longitude")) {
                                double lat = (double) location.get("latitude");
                                double lon = (double) location.get("longitude");
                                callback.onSuccess(lat + "," + lon);
                            }
                            else if (location.containsKey("city")) {
                                callback.onSuccess((String) location.get("city") + ",Italy");
                            }
                            else if (location.containsKey("province")) {
                                callback.onSuccess((String) location.get("province") + ",Italy");
                            }
                            else {
                                callback.onError("Località non trovata");
                            }
                        }
                        else {
                            callback.onError("L'utente non esiste");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onError("Errore nella connessione al server");
                    }
                });
    }

    public static void getFillingPredictions (FirebaseFirestore db, FirebaseUser user, List<HourlyWeatherItem> hourlyWeatherItems, OneElementCallback< List<FillingPredictionItem> > callback) {

        String userId = user.getUid();
        List<FillingPredictionItem> fillingPredictionItems = new ArrayList<>();

        db.collection("users").document(userId)
                .collection("containers").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                addFillingPredictionItem(fillingPredictionItems, hourlyWeatherItems, document);
                            }
                            callback.onSuccess(fillingPredictionItems);
                        }
                        else {
                            callback.onError("Nessun contenitore trovato");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onError("Errore nella connessione al server: " + e.getMessage());
                    }
                });
    }

    private static void addFillingPredictionItem (List<FillingPredictionItem> fillingPredictionItems, List<HourlyWeatherItem> hourlyWeatherItems, QueryDocumentSnapshot document) {

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        double area;
        if (document.get("roofArea") != null) {
            area = document.getDouble("roofArea") * 10000;
        }
        else {
            area = document.getDouble("baseArea");
        }

        String containerName = document.getString("name");
        String containerShape = document.getString("shape");
        double containerTotalVolume = document.getDouble("totalVolume") / 1000;
        double containerCurrentVolume = document.getDouble("currentVolume") / 1000;

        double containerPredictionVolume = containerCurrentVolume;
        for (HourlyWeatherItem item : hourlyWeatherItems) {
            if (Integer.parseInt(item.getTime().substring(0, 2)) >= hour ) {
                containerPredictionVolume += ( (item.getPrecip() / 10) * area ) / 1000;
            }
        }

        // rendo i valori reali in base alla capienza del contenitore
        containerPredictionVolume = Math.min(containerPredictionVolume, containerTotalVolume);
        double containerVolumeIncrease = containerPredictionVolume - containerCurrentVolume;

        fillingPredictionItems.add(new FillingPredictionItem(containerName, containerTotalVolume, containerCurrentVolume, containerVolumeIncrease, containerPredictionVolume, containerShape));
    }

    public static void autoFillContainers (FirebaseFirestore db, FirebaseUser user, DailyWeatherItem todayWeather) {

        // TODO: magari manda una notifica se qualcosa non va a buon fine

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // I mesi partono da 0
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate = day + "/" + month + "/" + year;

        String userId = user.getUid();

        CollectionReference containersRef = db.collection("users").document(userId).collection("containers");
        CollectionReference collection_historyRef = db.collection("users").document(userId).collection("collection_history");

        containersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        Map<String, Object> collectedVolume = new HashMap<>();
                        collectedVolume.put("date", currentDate);
                        collectedVolume.put("collectedVolume", 0);
                        collection_historyRef.add(collectedVolume).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference collectedVolumeDocRef) {
                                double totalCollectedVolume = 0;

                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                        double area;
                                        if (document.get("roofArea") != null) {
                                            area = document.getDouble("roofArea") * 10000;
                                        }
                                        else {
                                            area = document.getDouble("baseArea");
                                        }

                                        double containerTotalVolume = document.getDouble("totalVolume") / 1000;
                                        double containerCurrentVolume = document.getDouble("currentVolume") / 1000;
                                        double containerVolumeIncrease = ( area * (todayWeather.getPrecip() / 10) ) / 1000;
                                        double containerPredictionVolume = containerCurrentVolume + containerVolumeIncrease;

                                        // rendo i valori reali in base alla capienza del contenitore
                                        containerPredictionVolume = Math.min(containerPredictionVolume, containerTotalVolume);
                                        containerVolumeIncrease = containerPredictionVolume - containerCurrentVolume;
                                        totalCollectedVolume += containerVolumeIncrease;

                                        DocumentReference containerDocRef = containersRef.document(document.getId());
                                        containerDocRef.update("currentVolume", containerPredictionVolume);

                                        collectedVolumeDocRef.update("collectedVolume", totalCollectedVolume);
                                    }
                                }
                            }
                        });
                    }
                });
    }

    public static void getArea(FirebaseFirestore db, FirebaseUser user, String containerName, OneElementCallback<Double> callback) {

        String userId = user.getUid();

        db.collection("users").document(userId)
                .collection("containers")
                .whereEqualTo("name", containerName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            double area;
                            if (documentSnapshot.get("roofArea") != null) {
                                area = documentSnapshot.getDouble("roofArea") * 10000;
                            }
                            else {
                                area = documentSnapshot.getDouble("baseArea");
                            }
                            callback.onSuccess(area);
                        }
                        else {
                            callback.onError("Errore nella ricerca del container");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onError("Container non trovato");
                    }
                });
    }
}
