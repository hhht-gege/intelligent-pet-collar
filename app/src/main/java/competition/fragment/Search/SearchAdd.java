package competition.fragment.Search;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.gson.Gson;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import competition.R;
import competition.Utils.Constanst;
import competition.Utils.GetPath;
import competition.Utils.UU;
import competition.fragment.Search.Adapter.Search_GvAdapter;
import competition.fragment.Search.JavaBean.dynamic_javabean;
import competition.fragment.Setting.Setitem;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAdd extends AppCompatActivity {
    private static final String TAG = "SearchAdd";

    private GridView gridView;
    private Search_GvAdapter adapter;
    private List<String> list;
    private ImagePicker imagePicker;
    private AppCompatEditText content;
    private String content_str;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_add);
        gridView = (GridView) findViewById(R.id.grid_view);
        initView();
    }

    private void initView() {
        InitimgPicker();
        content = (AppCompatEditText) findViewById(R.id.search_add_content);
        list = new ArrayList<>();
        adapter = new Search_GvAdapter(this, list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View
                    view, int position, long id) {
                //??????????????????????????????
                if (position == parent.getChildCount() - 1) {
                    if (position == 6) {//???????????????
                    } else {
                        opnePhoto();
                    }
                } else {//???????????????????????????

                }
            }
        });
    }

    public void InitimgPicker() {
        imagePicker = new ImagePicker();
        imagePicker.setTitle("????????????");
        imagePicker.setCropImage(true);
    }

    public void InitData() {
        content_str = content.getText().toString().trim();
    }

    public void search_addback(View v) {
        finish();
    }


    public void search_report(View view) {
        InitData();
        Gson gson = new Gson();
        UploadImags uploadImags = new UploadImags();
        Log.d(TAG, content_str);
        Call<ResponseBody> task = uploadImags.uploadimags(list, "", content_str, Constanst.token);
        task.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse:" + response.code());
                try {
                    String json = response.body().string();
                    //Log.d(TAG, json);
                    dynamic_javabean body = gson.fromJson(json, dynamic_javabean.class);
                    int code = body.getCode();
                    if (code == 200) {
                        Date date=new Date(System.currentTimeMillis());
                        String time=getTime(date);
                        Search item=new Search(UU.userPortraitPath,UU.userId,UU.username,list,content_str,time,"");
                        SearFragment.searchList.add(0,item);
                        finish();
                    } else {
                        String message = body.getMsg();
                        Toast.makeText(SearchAdd.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SearchAdd.this, "???????????????...???????????????", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure:" + t.getMessage());
            }
        });

    }


    public void opnePhoto() {
        // ?????????????????????
        imagePicker.startChooser(this, new ImagePicker.Callback() {
            // ??????????????????
            @Override
            public void onPickImage(Uri imageUri) {
            }

            // ??????????????????
            @Override
            public void onCropImage(Uri imageUri) {
                if (list.size() >= 6) {
                    Toast.makeText(SearchAdd.this, "????????????????????????", Toast.LENGTH_LONG).show();
                } else {
                    path = getPath(imageUri);
                    Log.d("pic", path);
                    list.add(path);
                }
                File file = new File(path);
                if (file.exists()) {
                    Log.d("exist", "cunzaiaaa");
                }
                adapter.notifyDataSetChanged();
            }

            // ?????????????????????
            @Override
            public void cropConfig(CropImage.ActivityBuilder
                                           builder) {
                builder
                        // ????????????????????????
                        .setMultiTouchEnabled(false)
                        // ????????????????????????
                        .setGuidelines(CropImageView.Guidelines.OFF)
                        // ??????/??????
                        .setCropShape(CropImageView.CropShape
                                .RECTANGLE)
                        // ????????????????????????????????????
                        .setRequestedSize(960, 960)
                        // ?????????
                        .setAspectRatio(1, 1);
            }

            // ????????????????????????
            @Override
            public void onPermissionDenied(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int
            resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.onActivityResult(SearchAdd.this, requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantReslts) {
        super.onRequestPermissionsResult(requestCode, permissions, grantReslts);
        imagePicker.onRequestPermissionsResult(SearchAdd.this, requestCode, permissions, grantReslts);
    }


    public String getPath(Uri uri) {
        String path1 = uri.getPath();
        if (path1.charAt(0) == 'f') {
            return uri.getPath();
        } else {
            String filePath = GetPath.getPath(SearchAdd.this, uri);
            Log.d("str", "file" + filePath);
            if (filePath != null)
                return filePath;
            else
                return " ";
        }
    }
    private String getTime(Date date) {//???????????????????????????????????????
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }


}
