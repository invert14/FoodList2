package pl.gda.pg.eti.jme.app.model.dummy;

import java.util.ArrayList;
import java.util.List;

import pl.gda.pg.eti.jme.app.model.Product;

public class DummyModel {
    public static List<Product> getProducts() {
        ArrayList<Product> products = new ArrayList<Product>();

        products.add(new Product("bread", 5, 0, "Aldi", 10.99f, "moja lista"));

        return products;
    }
}
