package pl.gda.pg.eti.jme.app.model;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private int amount;
    private int localAmount;
    private String shop;
    private float price;
    private String list;
    private boolean shopModified;
    private boolean priceModified;

    private static final long serialVersionUID = -2518143671167959230L;

    public Product(String name, int amount, int localAmount, String shop, float price, String list) {
        this.name = name;
        this.amount = amount;
        this.localAmount = localAmount;
        this.shop = shop;
        this.price = price;
        this.list = list;
        this.shopModified = false;
        this.priceModified = false;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getLocalAmount() {
        return localAmount;
    }

    public void setLocalAmount(int localAmount) {
        this.localAmount = localAmount;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public boolean isShopModified() {
        return shopModified;
    }

    public void setShopModified(boolean shopModified) {
        this.shopModified = shopModified;
    }

    public boolean isPriceModified() {
        return priceModified;
    }

    public void setPriceModified(boolean priceModified) {
        this.priceModified = priceModified;
    }
}
