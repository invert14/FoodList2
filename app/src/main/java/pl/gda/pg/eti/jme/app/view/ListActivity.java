package pl.gda.pg.eti.jme.app.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import pl.gda.pg.eti.jme.app.R;
import pl.gda.pg.eti.jme.app.business.ListsController;
import pl.gda.pg.eti.jme.app.helpers.SimpleHttpHandler;
import pl.gda.pg.eti.jme.app.model.Product;
import pl.gda.pg.eti.jme.app.view.adapters.ListListItemAdapter;

public class ListActivity extends ActionBarActivity {

    private static final String FILE_NAME = "FoodList_Lists_";
    ListView listView;
    ListsController listsController;

    int userId;
    int deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        userId = intent.getIntExtra(LoginActivity.USER_ID_MESSAGE, 1);
        deviceId = intent.getIntExtra(LoginActivity.DEVICE_ID_MESSAGE, 1);

        listsController = new ListsController();

        listView = (ListView) findViewById(R.id.list_list_view);
        updateListView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String FILE_DIR = getFileDir();
        FileOutputStream fos;
        ObjectOutputStream os = null;
        File file;

        try {

            file = new File(FILE_DIR);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            fos = new FileOutputStream(file);
            os = new ObjectOutputStream(fos);
            os.writeObject(listsController.getLists());

            System.out.println("Done serializing");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String FILE_DIR = getFileDir();
        ArrayList<String> lists = new ArrayList<String>();
        FileInputStream fis;
        ObjectInputStream is = null;
        File file;

        try {

            file = new File(FILE_DIR);

            // if file doesnt exists, then create it
            if (file.exists()) {
                fis = new FileInputStream(file);
                is = new ObjectInputStream(fis);
                lists = (ArrayList<String>) is.readObject();
            }

            System.out.println("Done deserializing");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        listsController.setLists(lists);
        updateListView();
    }

    private String getFileDir() {
        String dir = getApplicationContext().getFilesDir().getPath() + "/"
                + FILE_NAME + String.valueOf(userId) + "_" + String.valueOf(deviceId);

        return dir;
    }

    public void addList(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("New list");
        alert.setMessage("Name:");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                listsController.addList(value);
                updateListView();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh_lists) {
            new JSONParseTask().execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class JSONParseTask extends AsyncTask<String, String, String> {

        ArrayList<String> lists;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lists = new ArrayList<String>();
        }

        @Override
        protected String doInBackground(String... args) {
            SimpleHttpHandler shh = new SimpleHttpHandler(MainActivity.LISTS_URL);
            shh.addParam("user_id", String.valueOf(userId));

            String jsonString = shh.getStringFromUrl();

            return jsonString;
        }
        @Override
        protected void onPostExecute(String jsonString) {
            if (jsonString.equals("0")) {
                Toast.makeText(getApplicationContext(), "Synchronization failed", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONArray json_lists;
            try {
                json_lists = new JSONArray(jsonString);
                // Getting JSON Array from URL
                for(int i = 0; i < json_lists.length(); i++){
                    JSONObject c = json_lists.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    String name = c.getString("name");

                    lists.add(name);
                }
                listsController.setLists(lists);
                updateListView();
                Toast.makeText(getApplicationContext(), "Synchronization succeeded", Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateListView() {
        ListListItemAdapter adapter = new ListListItemAdapter(
                getApplicationContext(), R.layout.list_list_item, listsController.getLists()) {

            @Override
            public void onListClicked(String list) {
                startMainActivity(list);
            }
        };
        listView.setAdapter(adapter);
    }


    private void startMainActivity(String list) {
        Intent intent = new Intent(this.getBaseContext(), MainActivity.class);
        intent.putExtra(LoginActivity.LIST_NAME_MESSAGE, list);
        intent.putExtra(LoginActivity.USER_ID_MESSAGE, userId);
        intent.putExtra(LoginActivity.DEVICE_ID_MESSAGE, deviceId);
        startActivity(intent);
    }
}
