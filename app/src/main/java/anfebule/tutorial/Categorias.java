package anfebule.tutorial;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

/**
 * Actividad que muestra obtiene las aplicaciones y las muestra en una lista
 * Created by Andres on 20/06/2016.
 */
public class Categorias extends AppCompatActivity {

    //Dialogo de carga
    ProgressDialog prgDialog;

    public static final String PREFS_NAME = "CategoryPrefsFile";

    //Listas de info que se mostrara en la app
    List<String> listApps = new ArrayList<>();
    List<String> listImages = new ArrayList<>();
    List<String> listSummary = new ArrayList<>();

    //Sets para almacenar la info cuando la app se cierre
    Set<String> setApps = new LinkedHashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorias);

        prgDialog = new ProgressDialog(this);

        final ListView lv = (ListView) findViewById(R.id.listView);

        //Se verifica conexion a internet
        InternetConnection ic = new InternetConnection();
        if(ic.checkConn(Categorias.this)) {

            //Dialogo de carga
            prgDialog.setMessage("Cargando");
            prgDialog.show();

            //Se obtiene la info del JSON
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("https://itunes.apple.com/us/rss/topfreeapplications/limit=20/json", new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {

                    // Hide Progress Dialog
                    prgDialog.hide();

                    try {
                        JSONObject feed = responseBody.getJSONObject("feed");
                        JSONArray entry = feed.getJSONArray("entry");
                        Integer[] imageId = new Integer[20];

                        for (int i = 0; i < entry.length(); i++) {
                            JSONObject entries = entry.getJSONObject(i);
                            JSONObject appNombreJson = entries.getJSONObject("im:name");
                            JSONArray appImageJsonArray = entries.getJSONArray("im:image");
                            JSONObject appImageJson = appImageJsonArray.getJSONObject(1);
                            JSONObject appSummaryJson = entries.getJSONObject("summary");

                            String appNombre = appNombreJson.getString("label");
                            String appImage = appImageJson.getString("label");
                            String appSummary = appSummaryJson.getString("label");

                            //Se llena la info en cada uno de los arrays para luego asignar a los ListView
                            listApps.add(appNombre);
                            listImages.add(appImage);
                            listSummary.add(appSummary);
                        }

                        //Adaptador para asignar ArrayList a ListView
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Categorias.this, android.R.layout.simple_list_item_1, listApps);
                        lv.setAdapter(adapter);

                        //Listener del ListView
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                Intent intent = new Intent(Categorias.this, Resumen.class);
                                intent.putExtra("app", listApps.get(position));
                                intent.putExtra("image", listImages.get(position));
                                intent.putExtra("summary", listSummary.get(position));
                                startActivity(intent);
                            }
                        });

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Error inesperado:" + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

            });
        } else {
            //Si no hay conexin se notifica y se trae la info de SharedPreferences
            Toast.makeText(getApplicationContext(), "No hay conexion a internet!", Toast.LENGTH_LONG).show();
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

            LinkedHashSet<String> setApps1 = new LinkedHashSet<>(settings.getStringSet("setApps", setApps));

            for (String savedApp: setApps1){
                String[] split = savedApp.split(";");
                listApps.add(split[0]);
                listImages.add(split[1]);
                listSummary.add(split[2]);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(Categorias.this, android.R.layout.simple_list_item_1, listApps);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(Categorias.this, Resumen.class);
                    intent.putExtra("app", listApps.get(position));
                    intent.putExtra("image", listImages.get(position));
                    intent.putExtra("summary", listSummary.get(position));
                    overridePendingTransition (1,2);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Se guardan los datos en Shared Preferences al cerrar la app
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        String saveArray;

        for(int i=0; i<listApps.size(); i++){
            saveArray = listApps.get(i)+ ";" + listImages.get(i) + ";" + listSummary.get(i);

            setApps.add(saveArray);
        }

        editor.putStringSet("setApps", setApps);
        editor.apply();
    }


}