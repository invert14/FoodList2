package pl.gda.pg.eti.jme.app.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

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

import pl.gda.pg.eti.jme.app.helpers.SimpleHttpHandler;
import pl.gda.pg.eti.jme.app.R;
import pl.gda.pg.eti.jme.app.business.ProductsController;
import pl.gda.pg.eti.jme.app.model.Product;
import pl.gda.pg.eti.jme.app.model.dummy.DummyModel;
import pl.gda.pg.eti.jme.app.view.adapters.ProductListItemAdapter;

public class MainActivity extends ActionBarActivity {

    private static final String FILE_NAME = "FoodList_";
    public static final String SERVER_URL = "http://10.0.2.2:5000";
    public static final String PRODUCTS_URL = SERVER_URL + "/products";
    public static final String USER_ID_URL = SERVER_URL + "/user";
    public static final String LISTS_URL = SERVER_URL + "/lists";
    public static final String DEFAULT_LIST_NAME = "Default list";

    private int userId;
    private int deviceId;
    private String listName;

    ListView listView;
    ProductsController productsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.product_list_view);

        Intent intent = getIntent();
        userId = intent.getIntExtra(LoginActivity.USER_ID_MESSAGE, 1);
        deviceId = intent.getIntExtra(LoginActivity.DEVICE_ID_MESSAGE, 1);
        listName = intent.getStringExtra(LoginActivity.LIST_NAME_MESSAGE);

        productsController = new ProductsController();

        updateListView();

        Toast.makeText(getApplicationContext(), listName, Toast.LENGTH_SHORT).show();
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
            os.writeObject(productsController.getProducts());

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

    private String getFileDir() {
        String dir = getApplicationContext().getFilesDir().getPath() + "/"
                + FILE_NAME + String.valueOf(userId) + "_" + String.valueOf(deviceId);

        if (!listName.equals(DEFAULT_LIST_NAME))
            dir += "_" + listName;

        return dir;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String FILE_DIR = getFileDir();
        ArrayList<Product> products = new ArrayList<Product>();
        FileInputStream fis;
        ObjectInputStream is = null;
        File file;

        try {

            file = new File(FILE_DIR);

            // if file doesnt exists, then create it
            if (file.exists()) {
                fis = new FileInputStream(file);
                is = new ObjectInputStream(fis);
                products = (ArrayList<Product>) is.readObject();
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

        productsController.setProducts(products);
        productsController.addProductsThatShouldBeAdded();
        updateListView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            new JSONParseTask().execute();
            return true;
        } else if (id == R.id.sample_data) {
            productsController.clearAndAddProducts(DummyModel.getProducts());
            updateListView();
            return true;
        } else if (id == R.id.clear_local) {
            clearLocalMemory();
            productsController.clear();
            updateListView();
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearLocalMemory() {
        String fileDir = getFileDir();
        File file = new File(fileDir);
        if (file.exists()) {
            file.delete();
        }
    }

    private void EditProductShopDialog(final Product product) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Shop for product");
        alert.setMessage("Name:");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                product.setShop(value);
                product.setShopModified(true);
                updateListView();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    private void EditProductPriceDialog(final Product product) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Price for product");
        alert.setMessage("Price:");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                float val = Float.parseFloat(value);
                product.setPrice(val);
                product.setPriceModified(true);
                updateListView();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    private void updateListView() {
        ProductListItemAdapter adapter = new ProductListItemAdapter(
                getApplicationContext(), R.layout.product_list_item, productsController.getProducts()) {

            @Override
            public void onModifyAmountClick(View view, Product product, int amount) {
                addAmount(product, amount);
            }

            @Override
            public void onDeleteClick(View view, Product product) {
                productsController.deleteProduct(product.getName());
                updateListView();
                Toast.makeText(getApplicationContext(), "delete  " + product.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onModifyProductShop(Product product) {
                EditProductShopDialog(product);
            }

            @Override
            public void onModifyProductPrice(Product product) {
                EditProductPriceDialog(product);
            }
        };
        listView.setAdapter(adapter);
    }


    public void addAmount(Product product, int amount) {
        String text = product.getName() + " -->  " + String.valueOf(amount);
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        productsController.addProductAmount(product.getName(), amount);
        updateListView();
    }

    public void addProduct(View view) {
        Intent i = new Intent(this, AddProductActivity.class);
        startActivityForResult(i, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                String newProductName = data.getStringExtra("result");
                Product newProduct = new Product(newProductName, 0, 0, "Some shop", 0.0f, listName);
                productsController.addProductToBeAdded(newProduct);
                updateListView();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private class JSONParseTask extends AsyncTask<String, String, String> {

        ArrayList<Product> products;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products = new ArrayList<Product>();
        }

        @Override
        protected String doInBackground(String... args) {
            SimpleHttpHandler shh = new SimpleHttpHandler(PRODUCTS_URL);
            shh.addParam("user_id", String.valueOf(userId));
            shh.addParam("device_id", String.valueOf(deviceId));
            shh.addParam("list_name", listName);
            JSONArray products = new JSONArray();
            for (Product p : productsController.getProducts()) {
                products.put(new Gson().toJson(p));
            }
            shh.addParam("products", products.toString());

            //TODO: pass products to be deleted on server
            JSONArray productsToBeDeleted = new JSONArray();
            for (Product p : productsController.getProductsToBeDeleted()) {
                productsToBeDeleted.put(new Gson().toJson(p.getName()));
            }
            shh.addParam("productsToBeDeleted", productsToBeDeleted.toString());
            productsController.clearProductsToBeDeleted();

            String jsonString = shh.getStringFromUrl();

            return jsonString;
        }
        @Override
        protected void onPostExecute(String jsonString) {
            if (jsonString.equals("0")) {
                Toast.makeText(getApplicationContext(), "Synchronization failed", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONArray json_products;
            try {
                json_products = new JSONArray(jsonString);
                // Getting JSON Array from URL
                for(int i = 0; i < json_products.length(); i++){
                    JSONObject c = json_products.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    String name = c.getString("name");
                    int amount = c.getInt("amount");
                    int localAmount = productsController.getLocalAmountByName(name);
                    String shop = c.getString("shop");
                    float price = (float)c.getDouble("price");
                    String list = c.getString("list");

                    //FIXME: user id
                    Product product = new Product(name, amount, localAmount, shop, price, list);

                    products.add(product);
                }
                productsController.clearAndAddProducts(products);
                updateListView();
                Toast.makeText(getApplicationContext(), "Synchronization succeeded", Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
