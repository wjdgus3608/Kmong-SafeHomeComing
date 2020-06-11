package com.jung.safedrive;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class FragmentInfo extends Fragment {
    private Bundle mData;
    private View mView;
    private boolean isGetResult=false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info,container,false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView=view;
        Button searchBtn=mView.findViewById(R.id.info_search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("log","search clicked!");
                Intent intent = new Intent(getContext(),PhoneBookActivity.class);
                startActivityForResult(intent,200);
            }
        });
        Button driveBtn=mView.findViewById(R.id.info_drive_btn);
        driveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText carNumberView = mView.findViewById(R.id.info_carNumber);
                EditText phoneNumberView = mView.findViewById(R.id.info_phone_number);
                if(phoneNumberView.getText().length()<=0) {
                    Toast.makeText(getContext(),"연락처를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getContext(),DriveActivity.class);
                Bundle bundle = new Bundle();
                if(carNumberView.getText().length()>0)
                bundle.putString("carNumber",carNumberView.getText().toString());
                bundle.putString("phoneNumber",phoneNumberView.getText().toString());
                bundle.putInt("Alarminterval",mData.getInt("Alarminterval"));
                bundle.putBoolean("Shakealarmcheck",mData.getBoolean("Shakealarmcheck"));
                intent.putExtras(bundle);
                startActivityForResult(intent,200);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==200){
            isGetResult=true;
            mData.putString("To_number",data.getStringExtra("result"));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("log","OnResume");
        TextView phoneNumberView=mView.findViewById(R.id.info_phone_number);
        if(!isGetResult){
            readSetting();
        }
        else{
            isGetResult=false;
        }
        String number=mData.getString("To_number");
        if(!number.equals("null"))
            phoneNumberView.setText(number);
    }

    private void readSetting(){
        SharedPreferences sh=getContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
        String number=sh.getString("To_number","null");
        int interval=sh.getInt("Alarminterval",2);
        boolean isUse=sh.getBoolean("Shakealarmcheck",false);
        Log.d("log","number : "+number);
        Log.d("log","interval : "+interval);
        Log.d("log","isUse : "+isUse);
        mData=new Bundle();
        mData.putString("To_number",number);
        mData.putInt("Alarminterval",interval);
        mData.putBoolean("Shakealarmcheck",isUse);
    }


}
