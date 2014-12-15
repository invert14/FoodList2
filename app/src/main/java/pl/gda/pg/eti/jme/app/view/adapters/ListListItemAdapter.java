package pl.gda.pg.eti.jme.app.view.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pl.gda.pg.eti.jme.app.R;
import pl.gda.pg.eti.jme.app.model.Product;

public abstract class ListListItemAdapter extends ArrayAdapter<String> {
    private LayoutInflater mInflater;
    List<String> lists;

    public ListListItemAdapter(Context context, int resource, List<String> lists) {
        super(context, resource, lists);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.lists = lists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = mInflater.inflate(R.layout.list_list_item, parent, false);
        }

        TextView listName = (TextView) view.findViewById(R.id.list_name);

        final String list = lists.get(position);
        listName.setText(list);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), list, Toast.LENGTH_SHORT).show();
                onListClicked(list);
            }
        });

        return view;
    }
    abstract public void onListClicked(String list);
}
