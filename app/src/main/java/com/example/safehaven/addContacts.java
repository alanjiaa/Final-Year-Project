package com.example.safehaven;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.widget.Button;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.textfield.TextInputLayout;

public class addContacts extends AppCompatActivity {
    TextInputLayout edit_name , edit_num;
    Button add , contacts ;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //sets the page to the xml layout for adding new contacts
        setContentView(R.layout.activity_addcontact);

        edit_name = findViewById(R.id.et8);
        edit_num = findViewById(R.id.et9);
        add = findViewById(R.id.b3);
        contacts = findViewById(R.id.b4);

        //Creates new database handler class
        db = new DatabaseHandler(this);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edit_name.getEditText().getText().toString().trim();
                String number = edit_num.getEditText().getText().toString().trim();

                //Inserts name and phone number inputs into the database
                if(edit_name.getEditText().length() != 0 && !checkPhoneNumber() && !toCheckContactExist(number)){
                    boolean isInserted = db.insertData(name,number);
                    if(isInserted){
                        edit_name.getEditText().setText(null);
                        edit_num.getEditText().setText(null);
                        Toast.makeText(addContacts.this, "New contact has been added!", Toast.LENGTH_SHORT).show();
                        edit_name.requestFocus();
                    }
                    else
                        Toast.makeText(addContacts.this, "Contact not added!", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(addContacts.this, "Contact already exists", Toast.LENGTH_SHORT).show();
            }
        });

        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            //Method will output message if no contacts have been assigned
            public void onClick(View v) {
                Cursor cursor = db.getAllContacts();
                if(cursor.getCount() == 0){
                    Toast.makeText(addContacts.this, "No existing contacts", Toast.LENGTH_SHORT).show();
                }
                //If there are existing contacts, the contacts list will be shown
                else {
                    startActivity(new Intent(addContacts.this,ViewContacts.class));
                }
            }
        });
    }

    //Checks if phone number is equal to 10
    public boolean checkPhoneNumber(){
        String number = edit_num.getEditText().getText().toString().trim();
        if(number.length() != 10){
            edit_num.setError("Invalid Number");
            edit_num.requestFocus();
            return true;
        } else {
            return false;
        }
    }

    //Checks if input phone number already exists in database
    private boolean toCheckContactExist(String phoneNo){
        if(db.getContact(phoneNo) != 0) {
            edit_num.setError("Contact Number Already Exists");
            edit_num.requestFocus();
            return true;
        }
        return false;
    }
}
