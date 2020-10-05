package com.example.energydream.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.example.energydream.R;

public class StandyPowerBlock extends Dialog {

    private static final int LAYOUT = R.layout.dialog_standy_power_block;
    private Context context;
    private Button fin_Btn;

    public StandyPowerBlock(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        fin_Btn = (Button)findViewById(R.id.btn_standy_block);
        fin_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }


}
