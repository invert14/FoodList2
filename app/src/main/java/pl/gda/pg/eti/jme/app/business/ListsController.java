package pl.gda.pg.eti.jme.app.business;

import java.util.ArrayList;
import java.util.List;

public class ListsController {

    private List<String> lists;

    public ListsController() {
        lists = new ArrayList<String>();
    }

    public List<String> getLists() {
        return lists;
    }

    public void setLists(List<String> lists) {
        this.lists = lists;
    }

    public void addList(String value) {
        lists.add(value);
    }
}
