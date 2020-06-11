package com.jung.safedrive;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentSetting extends Fragment {
    private View mView;
    private RadioGroup minGroupView;
    private RadioGroup useGroupView;
    private TextView numberView;
    private Bundle mData=null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView=view;
        minGroupView=view.findViewById(R.id.setting_min_group);
        useGroupView=view.findViewById(R.id.setting_use_group);
        numberView=view.findViewById(R.id.setting_number);
        Button submitBtn = view.findViewById(R.id.setting_submit_btn);

        readSetting();
        if(mData!=null){
            String number=mData.getString("To_number");
            if(!number.equals("null"))
                numberView.setText(number);
            if(mData.getInt("Alarminterval")!=-1)
            minGroupView.check(minGroupView.getChildAt(mData.getInt("Alarminterval")).getId());
            useGroupView.check(useGroupView.getChildAt(mData.getBoolean("Shakealarmcheck")?0:1).getId());
        }

        submitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saveSetting();
            }
        });

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

    private void saveSetting(){

        String numStr=numberView.getText().toString();
        int interval = minGroupView.indexOfChild(mView.findViewById(minGroupView.getCheckedRadioButtonId()));
        boolean isUse = useGroupView.indexOfChild(mView.findViewById(useGroupView.getCheckedRadioButtonId()))==0;

        SharedPreferences sh= this.getContext().getSharedPreferences("setting",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sh.edit();
        String number="null";
        if(numStr.length()>0)
            number=numStr;
        editor.putString("To_number",number);
        editor.putInt("Alarminterval",interval);
        editor.putBoolean("Shakealarmcheck",isUse);
        editor.commit();

        Toast.makeText(this.getContext(),"설정이 저장되었습니다.",Toast.LENGTH_SHORT).show();
    }
}
