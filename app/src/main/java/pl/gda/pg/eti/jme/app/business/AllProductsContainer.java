package pl.gda.pg.eti.jme.app.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

import pl.gda.pg.eti.jme.app.model.Product;

public class AllProductsContainer {

    HashSet <String> productNames;
    String fileDir;

    public AllProductsContainer(String fileDir) {
        productNames = new HashSet<String>();
        this.fileDir = fileDir;
    }

    public void clear() {
        productNames.clear();
    }

    private void writeToFile() {
        FileOutputStream fos;
        ObjectOutputStream os = null;
        File file;

        try {
            file = new File(fileDir);
            if (!file.exists()) {
                file.createNewFile();
            }

            fos = new FileOutputStream(file);
            os = new ObjectOutputStream(fos);
            os.writeObject(productNames);
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

    private void readFromFile() {
        ArrayList<Product> products = new ArrayList<Product>();
        FileInputStream fis;
        ObjectInputStream is = null;
        File file;

        try {

            file = new File(fileDir);

            if (file.exists()) {
                fis = new FileInputStream(file);
                is = new ObjectInputStream(fis);
                productNames = (HashSet<String>) is.readObject();
            }
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
    }

    public boolean containsProduct(String name) {
        readFromFile();
        return productNames.contains(name);
    }

    public void addProduct(String name) {
        readFromFile();
        if (!containsProduct(name)) {
            productNames.add(name);
        }
        writeToFile();
    }

    public void removeProduct(String name) {
        readFromFile();
        if (containsProduct(name)) {
            productNames.remove(name);
        }
        writeToFile();
    }
}
