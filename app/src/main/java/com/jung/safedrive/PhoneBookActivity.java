package com.jung.safedrive;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PhoneBookActivity extends AppCompatActivity {

    private ArrayList<PhoneBook> list=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook);
        getContacts(getApplicationContext());
        RecyclerView recyclerView=findViewById(R.id.recycler_view);
        PhoneBookAdapter adapter=new PhoneBookAdapter(list,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    private void getContacts(Context context){
        if(ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},200);
            return;
        }
        ContentResolver resolver=context.getContentResolver();
        Uri phoneUri= ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] columns= new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        Cursor cursor=resolver.query(phoneUri,columns,null,null,null);
        if(cursor != null){
            while (cursor.moveToNext()){
                int idIndex = cursor.getColumnIndex(columns[0]);
                int nameIndex = cursor.getColumnIndex(columns[1]);
                int numberIndex = cursor.getColumnIndex(columns[2]);

                String id = cursor.getString(idIndex);
                String name = cursor.getString(nameIndex);
                String number = cursor.getString(numberIndex);
                list.add(new PhoneBook(id,number,name));
            }
        }
        Log.d("log","list size : "+list.size());
        cursor.close();
    }

    @Override
    public void onBackPressed() {
        setResult(400);
        finish();
    }
}
