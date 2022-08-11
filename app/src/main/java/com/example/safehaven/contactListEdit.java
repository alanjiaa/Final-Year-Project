package com.example.safehaven;

import android.view.MenuItem;
import android.util.Pair;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.PopupMenu;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class contactListEdit extends ArrayAdapter<Pair<String, String>> {

    private ArrayList<Pair<String, String>> list;
    viewHold viewHold;
    ContactItemInterface itemInterface;
    Context context;

    //Creates interface to allow for editing or deleting contacts
    interface ContactItemInterface {
        void editOptionSelected(String phoneNumber, String name);

        void deleteOptionSelected(String phoneNumber);
    }

    //Lists contacts
    public contactListEdit(Context context,
                           ArrayList<Pair<String, String>> list,
                           ContactItemInterface itemInterface) {
        super(context, R.layout.item_contact_list, list);
        this.list = list;
        this.itemInterface = itemInterface;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (list.size() > 0)
            return list.size();
        else
            return 1;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) {

            //Instantiates XML contents from contact list items to a new view object
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_contact_list, parent, false);
            viewHold = new viewHold();
            viewHold.contactName = view.findViewById(R.id.contact_name);
            viewHold.contactNumber = view.findViewById(R.id.contact_number);
            viewHold.contactNameInitial = view.findViewById(R.id.contact_initial);
            viewHold.optionsIv = view.findViewById(R.id.options);
            view.setTag(viewHold);
        } else {
            viewHold = (viewHold) view.getTag();
        }
        //If no existing contacts found, contact numbers, names, and options will no longer appear
        if (list.get(position).first.equals("No Existing Contact Found")) {
            viewHold.contactName.setText("No Existing Contact Found");
            viewHold.contactNameInitial.setVisibility(View.GONE);
            viewHold.contactNumber.setVisibility(View.GONE);
            viewHold.optionsIv.setVisibility(View.GONE);
        } else {
            viewHold.contactName.setText(list.get(position).first);

            viewHold.contactNameInitial.setText(Character.toString(Character.toUpperCase(
                    list.get(position).first.charAt(0)
            )));
            viewHold.contactNumber.setText(list.get(position).second);

            viewHold.optionsIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayPopup(v, list.get(position).second, list.get(position).first);
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    displayPopup(v.findViewById(R.id.options), list.get(position).second, list.get(position).first);
                    return true;
                }
            });
        }
        return view;
    }

    public class viewHold {
        TextView contactName;
        TextView contactNumber;
        TextView contactNameInitial;
        ImageView optionsIv;
    }
    //Displays a popup which will allow users to change contact details or delete
    void displayPopup(View view, final String phoneNumber, final String name) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.contact_list_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item != null) {
                    switch (item.getItemId()) {
                        case R.id.edit: {
                            itemInterface.editOptionSelected(phoneNumber, name);
                        }
                        break;
                        case R.id.delete: {
                            itemInterface.deleteOptionSelected(phoneNumber);
                        }
                        break;
                    }
                }
                return true;
            }
        });
        popupMenu.show();
    }

}

