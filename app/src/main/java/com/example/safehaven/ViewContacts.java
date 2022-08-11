package com.example.safehaven;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.widget.Button;


import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class ViewContacts extends AppCompatActivity implements contactListEdit.ContactItemInterface {

    ListView listView;
    DatabaseHandler db;
    ArrayList<Pair<String, String>> contactList;
    contactListEdit contactlistedit;
    SearchView searchView;
    TextInputLayout ed_name, ed_number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);

        searchView = findViewById(R.id.search_bar);
        contactList = new ArrayList<>();
        db = new DatabaseHandler(ViewContacts.this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });

        updateListView();
    }

    //Method to edit existing contact details
    @Override
    public void editOptionSelected(final String phoneNumber, String name) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ViewContacts.this);

        LayoutInflater inflater = ViewContacts.this.getLayoutInflater();

        final ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.edit_contact, null);
        ed_number = viewGroup.findViewById(R.id.et_edit_number);
        ed_name = viewGroup.findViewById(R.id.et_edit_name);
        ed_number.getEditText().setText(phoneNumber);
        ed_name.getEditText().setText(name);

        builder.setView(viewGroup)
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validation checks for new input phone number and name for contact, updates database if these validations pass
                if (checkPhone() & checkName() && toCheckContactExist(alertDialog)) {
                    db.updateContact(phoneNumber, ed_name.getEditText().getText().toString(), ed_number.getEditText().getText().toString());
                    Toast.makeText(ViewContacts.this, "Contact Updated", Toast.LENGTH_SHORT).show();
                    updateListView();
                    alertDialog.dismiss();
                }
            }
        });
    }

    //Checks if name is at least 1 letter
    public boolean checkName() {
        String name = ed_name.getEditText().getText().toString().trim();
        if (name.length() == 0) {
            ed_name.setError("Invalid Name");
            ed_name.requestFocus();
            return false;
        }
        ed_name.setErrorEnabled(false);
        return true;
    }

    //Checks if phone number is the valid length
    public boolean checkPhone() {
        String number = ed_number.getEditText().getText().toString().trim();
        if (number.length() != 10) {
            ed_number.setError("Invalid Number");
            ed_number.requestFocus();
            return false;
        } else {
            ed_number.setErrorEnabled(false);
            return true;
        }
    }
    //Checks if contact already exists, updates contact if no matches found with existing contacts
    private boolean toCheckContactExist(AlertDialog alertDialog) {
        String name = ed_name.getEditText().getText().toString().trim();
        String number = ed_number.getEditText().getText().toString().trim();

        if (db.getContact(number) == 0 || db.getName(name) == 0) {
            return true;
        }

        Toast.makeText(ViewContacts.this, "Contact Updated", Toast.LENGTH_SHORT).show();
        alertDialog.dismiss();
        return false;
    }

    //Method to delete selected contact
    @Override
    public void deleteOptionSelected(final String phoneNumber) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete?")
                .setIcon(R.drawable.ic_delete_24)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ViewContacts.this, "Contact Deleted", Toast.LENGTH_SHORT).show();
                        db = new DatabaseHandler(ViewContacts.this);
                        db.deleteContact(phoneNumber);
                        updateListView();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    //Updates list view
    public void updateListView() {
        listView = findViewById(R.id.list_view);
        contactList = new ArrayList<>();
        Cursor cursor = db.getAllContacts();

        if (cursor.getCount() == 0) {
            contactlistedit.clear();
            contactlistedit.notifyDataSetChanged();
            Toast.makeText(this, "No contacts added!", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                Pair<String, String> contact = new Pair<>(cursor.getString(1), cursor.getString(2));
                contactList.add(contact);
            }
            contactlistedit = new contactListEdit(this, contactList, this);
            listView.setAdapter(contactlistedit);
        }
    }

    //Method to search for contacts in database
    public void search(String keyword) {
        Cursor cursor = db.searchContacts(keyword);
        if (cursor.getCount() == 0) {
            Pair<String, String> contact = new Pair<>("No Existing Contact Found", null);
            contactlistedit.clear();
            contactList.add(contact);
            contactlistedit = new contactListEdit(ViewContacts.this, contactList, null);
            listView.setAdapter(contactlistedit);
        } else {
            contactlistedit.clear();
            while (cursor.moveToNext()) {
                Pair<String, String> contact = new Pair<>(cursor.getString(1), cursor.getString(2));
                contactList.add(contact);
            }
            contactlistedit = new contactListEdit(ViewContacts.this, contactList, ViewContacts.this);
            listView.setAdapter(contactlistedit);
        }
    }
}
