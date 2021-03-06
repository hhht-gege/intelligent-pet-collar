package competition.fragment.Setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import competition.R;
import competition.Utils.API;
import competition.Utils.Constanst;
import competition.Utils.RetrofitManager;
import competition.fragment.Anim.AnimFragment;
import competition.fragment.Setting.Javabean.bindcollar_javabean;
import competition.fragment.Setting.Javabean.bindcollar_request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Set_bind extends AppCompatActivity {

    private static final String TAG = "Set_bind";
    private Button bindadd;
    private MaterialTextView bindedit;
    private String bind_str;
    private String ip;
    private OptionsPickerView picker;
    private List<Integer> petIds;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_set_bind);
        ip = getIntent().getStringExtra("ip");
        petIds = new ArrayList<>();
        petIds = AnimFragment.petIds;
        bind();
        InitPicker();
    }

    public void bind() {
        bindadd = (Button) findViewById(R.id.set_bindadd);
        bindedit = (MaterialTextView) findViewById(R.id.set_bindedit);
        bindedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (petIds.size() > 0)
                    picker.show(v);
                else
                    Toast.makeText(Set_bind.this, "????????????????????????", Toast.LENGTH_SHORT).show();
            }
        });
        bindadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bind_str = bindedit.getText().toString().trim();
                bindcollar();
            }
        });
    }

    public void bindcollar() {
        Gson gson = new Gson();
        API api = RetrofitManager.getRetrofit().create(API.class);
        int petid = Integer.parseInt(bind_str);
        bindcollar_request request = new bindcollar_request(ip, "", petid);
        Call<ResponseBody> task = api.bind_collar(Constanst.token, request);
        task.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse:" + response.code());
                try {
                    String json = response.body().string();
                    bindcollar_javabean body = gson.fromJson(json, bindcollar_javabean.class);
                    Log.d(TAG, json);
                    int code = body.getCode();
                    if (code == 200) {
                        Toast.makeText(Set_bind.this, body.getMsg(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(Set_bind.this, body.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Set_bind.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure:" + t.getMessage());
            }
        });
    }

    public void InitPicker() {
        picker = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                int tx = petIds.get(options1);
                System.out.println("tx->>>" + tx);
                bindedit.setText(String.valueOf(tx));
            }
        })
                .setSubmitText("??????")//??????????????????
                .setCancelText("??????")//??????????????????
                .setTitleText("PetId")//??????
                .setSubCalSize(18)//???????????????????????????
                .setTitleSize(20)//??????????????????
                .setContentTextSize(18)//??????????????????
                .setLabels(null, null, null)//???????????????????????????
                .setCyclic(false, false, false)//????????????
                .setSelectOptions(1, 1, 1)  //?????????????????????
                .setOutSideCancelable(true)//????????????dismiss default true
                .build();
        picker.setPicker(petIds);

        Dialog mDialog = picker.getDialog();
        if (mDialog != null) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            picker.getDialogContainerLayout().setLayoutParams(params);
            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//??????????????????
                dialogWindow.setGravity(Gravity.BOTTOM);//??????Bottom,????????????
                dialogWindow.setDimAmount(0.3f);
            }
        }
    }
}
