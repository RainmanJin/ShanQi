package com.hztuen.shanqi.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hztuen.contacts.AppUrl;
import com.hztuen.contacts.Constants;
import com.hztuen.shanqi.R;
import com.hztuen.util.MyLogUtil;
import com.hztuen.util.StringUtil;
import com.hztuen.zxing.CaptureActivity;
import com.upyun.library.common.Params;
import com.upyun.library.common.UploadManager;
import com.upyun.library.listener.SignatureListener;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;
import com.upyun.library.utils.UpYunUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.valuesfeng.picker.Picker;
import io.valuesfeng.picker.engine.GlideEngine;
import io.valuesfeng.picker.utils.PicturePickerUtils;
import okhttp3.Call;

/**
 * 问题反馈界面
 */

public class ProblemActivity extends BaseActivity implements View.OnClickListener {
    //图片选择器的返回码
    public static final int REQUEST_CODE_CHOOSE = 1;

    //upyun数据
    private final String PATH = Constants.UPYUN_PATH;

    //6个问题类型
    private TextView tv01, tv02, tv03, tv04, tv05, tv06, select_pic_num_tip;
    //2个输入框 车辆编号问题详情
    private EditText etBikeNum, etQuestion;
    //缩略图显示
    private GridView myGridView;
    private GridViewAdapter mAdapter;
    private ArrayList<Uri> mSelected;//数据源

    private ArrayList<String> mList;//需要传给PhotoView界面的List
    private String mPath;//上传到upyun后返回得到的数据
    private String problemType = "lock";//问题类型
    private String myBikeNum;//车辆编号
    private String myQuestion;//我的问题
    private boolean isRun;//线程监听图片是否全部上传到upyun
    public int picNum = 0;//已经上传到upyun图片的数量
    private int pic_num = 4;//设置图片选择器需要选择图片的数量

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kefu);
        initHeadUI();//初始化标题栏
        initUI();//初始化UI


        myGridView = (GridView) findViewById(R.id.myGridView);
        mAdapter = new GridViewAdapter();
        mSelected = new ArrayList<>();
        mList = new ArrayList<>();

        myGridView.setAdapter(mAdapter);

        //缩略图点击事件
        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSelected.size() < 5) {
                    if (position == mSelected.size()) {
                        MyLogUtil.i("点击添加按钮，打开相册");
                        //相册选择器
                        Picker.from(ProblemActivity.this)
                                .count(pic_num - mSelected.size())
                                .enableCamera(true)
                                .setEngine(new GlideEngine())
                                .forResult(REQUEST_CODE_CHOOSE);
                    } else {
                        mList.clear();
                        for (int i = 0; i < mSelected.size(); i++) {
                            mList.add(mSelected.get(i).toString());
                        }
                        Intent intent = new Intent(ProblemActivity.this, ImagePageActivity.class);
                        intent.putStringArrayListExtra("mList", mList);
                        intent.putExtra("pos", position);
                        startActivityForResult(intent, 888);
                    }

                }
            }
        });
    }

    /**
     * 初始化本页面布局
     */
    private void initUI() {
        isRun = true;

        //6个问题类型
        tv01 = (TextView) findViewById(R.id.tv01);
        tv02 = (TextView) findViewById(R.id.tv02);
        tv03 = (TextView) findViewById(R.id.tv03);
        tv04 = (TextView) findViewById(R.id.tv04);
        tv05 = (TextView) findViewById(R.id.tv05);
        tv06 = (TextView) findViewById(R.id.tv06);
        //照片选择数字提示
        select_pic_num_tip = (TextView) findViewById(R.id.select_pic_num_tip);

        //设置点击事件
        tv01.setOnClickListener(this);
        tv02.setOnClickListener(this);
        tv03.setOnClickListener(this);
        tv04.setOnClickListener(this);
        tv05.setOnClickListener(this);
        tv06.setOnClickListener(this);
        //
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("上传中请稍后...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
//        mProgressDialog.show();

        //车辆编号、问题详情 输入框
        etBikeNum = (EditText) findViewById(R.id.etBikeNum);
        etQuestion = (EditText) findViewById(R.id.etQuestion);
        //扫码按钮
        ImageView ivQr = (ImageView) findViewById(R.id.ivQr);
        ivQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ProblemActivity.this, CaptureActivity.class);
                startActivityForResult(mIntent, 300);
            }
        });

        //提交按钮
        Button bt = (Button) findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //获得单车编号、问题文本内容
                myBikeNum = etBikeNum.getText().toString();
                myQuestion = etQuestion.getText().toString();
                if ("".equals(myBikeNum)) {
                    Toast.makeText(ProblemActivity.this, R.string.first_input_bike_num, Toast.LENGTH_SHORT).show();
                } else if ("".equals(myQuestion)) {
                    Toast.makeText(ProblemActivity.this, R.string.first_input_question, Toast.LENGTH_SHORT).show();
                } else {
                    //上传图片到upyun
                    if (mSelected.size() == 0) {
                        Toast.makeText(ProblemActivity.this, R.string.first_chose_pic, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProblemActivity.this, "问题提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });

    }

    //把问题提交到服务器
    private void submit() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRun) {
                    if (picNum != 0 && picNum == mSelected.size()) {
                        //
                        isRun = false;
                        MyLogUtil.i("问题提交", "bikeId=" + myBikeNum + ",type=" + problemType + ",description=" + myQuestion + ",img=" + mPath);

                        String sign = "";
                        List<String> attributes = new ArrayList<>();
                        attributes.add("bikeId=" + myBikeNum);
                        attributes.add("type=" + problemType);
                        attributes.add("description=" + myQuestion);
                        attributes.add("img=" + mPath);
                        try {
                            sign = StringUtil.sign(attributes);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                        OkHttpUtils
                                .post()
                                .url(AppUrl.PRESENT)
                                .addParams("bikeId", myBikeNum)
                                .addParams("type", problemType)
                                .addParams("description", myQuestion)
                                .addParams("img", mPath)
                                .addParams("sign", sign)
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        MyLogUtil.i("问题提交2", "出问题");
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {

                                        MyLogUtil.i("问题提交2", "" + response);
                                        try {
                                            JSONObject result = new JSONObject(response);
                                            String resultCode = result.getString("resultCode");
                                            String resultMsg = result.getString("resultMsg");
                                            Toast.makeText(ProblemActivity.this, resultMsg, Toast.LENGTH_SHORT).show();

                                            if ("200".equals(resultCode)) {
                                                mProgressDialog.dismiss();
                                                etBikeNum.setText("");
                                                etQuestion.setText("");
                                                mSelected.clear();
                                                mAdapter.notifyDataSetChanged();
                                                finish();
                                            } else {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(ProblemActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });


                    }

                }


            }
        });

        t.start();

    }

    /**
     * 初始化标题栏
     */
    private void initHeadUI() {
        TextView tvHead = (TextView) findViewById(R.id.tvHeadTitle);
        tvHead.setText(R.string.problem_feedback);
        LinearLayout layoutLeft = (LinearLayout) findViewById(R.id.layoutLeft);
        layoutLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 将图片uri 转换成 file
     * 用于把图片上传到upyun
     */
    private File uri2File(Uri uri) {
        File file = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null,
                null, null);
        int actual_image_column_index = actualimagecursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor
                .getString(actual_image_column_index);
        file = new File(img_path);
        return file;
    }

    /**
     * 把图片文件上传到Upyun
     */
    private void post2Upyun(File temp) {
        final Map<String, Object> paramsMap = new HashMap<>();
        //上传空间
        paramsMap.put(Params.BUCKET, Constants.UPYUN_SPACE);
        //保存路径，任选其中一个
        paramsMap.put(Params.SAVE_KEY, Constants.UPYUN_SAVE_PATH);
        //paramsMap.put(Params.PATH, savePath);
        //可选参数（详情见api文档介绍）
        paramsMap.put(Params.RETURN_URL, "httpbin.org/post");
        //结束回调，不可为空
        UpCompleteListener completeListener = new UpCompleteListener() {
            @Override
            public void onComplete(boolean isSuccess, String result) {

                if (isSuccess) {
                    try {
                        JSONObject mObject = new JSONObject(result);
                        String url = mObject.getString("url");
                        if (mPath == null) {
                            mPath = PATH + url;
                        } else {
                            mPath = mPath + "," + PATH + url;
                        }
                        if (picNum == mSelected.size()) {
                            //上传图片
                        } else {
                            picNum++;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        SignatureListener signatureListener = new SignatureListener() {
            @Override
            public String getSignature(String raw) {
                return UpYunUtils.md5(raw + Constants.UPYUN_KEY);
            }
        };
        //进度回调，可为空
        UpProgressListener progressListener = new UpProgressListener() {
            @Override
            public void onRequestProgress(final long bytesWrite, final long contentLength) {

            }
        };
        UploadManager.getInstance().formUpload(temp, paramsMap, Constants.UPYUN_KEY, completeListener, progressListener);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv01:
                problemType = "lock";
                restBackground();
                tv01.setBackgroundResource(R.drawable.money_bg_01);
                tv01.setTextColor(getResources().getColor(R.color.colorText_red));
                break;
            case R.id.tv02:
                problemType = "battery";
                restBackground();
                tv02.setBackgroundResource(R.drawable.money_bg_01);
                tv02.setTextColor(getResources().getColor(R.color.colorText_red));
                break;

            case R.id.tv03:
                problemType = "bike";
                restBackground();
                tv03.setBackgroundResource(R.drawable.money_bg_01);
                tv03.setTextColor(getResources().getColor(R.color.colorText_red));
                break;

            case R.id.tv04:
                problemType = "illegally";
                restBackground();
                tv04.setBackgroundResource(R.drawable.money_bg_01);
                tv04.setTextColor(getResources().getColor(R.color.colorText_red));
                break;

            case R.id.tv05:
                problemType = "customer";
                restBackground();
                tv05.setBackgroundResource(R.drawable.money_bg_01);
                tv05.setTextColor(getResources().getColor(R.color.colorText_red));
                break;

            case R.id.tv06:
                problemType = "other";
                restBackground();
                tv06.setBackgroundResource(R.drawable.money_bg_01);
                tv06.setTextColor(getResources().getColor(R.color.colorText_red));
                break;
        }
    }

    /**
     * 重置背景 黑框黑字
     */
    private void restBackground() {

        tv01.setBackgroundResource(R.drawable.money_bg_02);
        tv02.setBackgroundResource(R.drawable.money_bg_02);
        tv03.setBackgroundResource(R.drawable.money_bg_02);
        tv04.setBackgroundResource(R.drawable.money_bg_02);
        tv05.setBackgroundResource(R.drawable.money_bg_02);
        tv06.setBackgroundResource(R.drawable.money_bg_02);

        tv01.setTextColor(getResources().getColor(R.color.colorText4));
        tv02.setTextColor(getResources().getColor(R.color.colorText4));
        tv03.setTextColor(getResources().getColor(R.color.colorText4));
        tv04.setTextColor(getResources().getColor(R.color.colorText4));
        tv05.setTextColor(getResources().getColor(R.color.colorText4));
        tv06.setTextColor(getResources().getColor(R.color.colorText4));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //图片选择器 返回的数据
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected.addAll(PicturePickerUtils.obtainResult(data));
            for (Uri u : mSelected) {
                Log.i("picture", u.getPath());
            }
            mAdapter.notifyDataSetChanged();

            if (null != mSelected) {
                if (mSelected.size() == 0) {
                    select_pic_num_tip.setText(R.string.chose_pic);
                } else {
                    select_pic_num_tip.setText("上传照片(" + mSelected.size() + "/4)");
                }
            }

        }
        //二维码返回的数据
        if (requestCode == 300) {
            if (data != null) {
                String result = data.getStringExtra("result");
                String a = StringUtil.parseUrlString(result);//

                if ("".equals(a)) {
                    Toast.makeText(this, "请选择正确的二维码扫描", Toast.LENGTH_SHORT).show();
                } else {
                    etBikeNum.setText(a);
                }
                MyLogUtil.i("扫码结果", "" + result);

            }
        }
        //图片输出返回
        if (resultCode == 100) {
            Log.i("zangyi", "直接 返回");
        } else if (resultCode == 101) {
            mSelected.remove(data.getIntExtra("pos", 0));
            mAdapter.notifyDataSetChanged();
            Log.i("zangyi", "带值返回");
            if (null != mSelected) {
                if (mSelected.size() == 0) {
                    select_pic_num_tip.setText(R.string.chose_pic);
                } else {
                    select_pic_num_tip.setText("上传照片(" + mSelected.size() + "/4)");
                }
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////


    /**
     * 图片缩略图区域的Adapter
     */
    class GridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mSelected.size() < 4) {
                return mSelected.size() + 1;
            } else if (mSelected.size() == 4) {
                return 4;
            } else {
                return 4;
            }

        }

        @Override
        public Object getItem(int position) {
            return mSelected.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(ProblemActivity.this).inflate(R.layout.gridview_item, null);
            ImageView iv = (ImageView) convertView.findViewById(R.id.mImageView);
            if (mSelected.size() <= 4) {
                if (position == mSelected.size()) {
                    iv.setImageResource(R.mipmap.add01);
                } else {
                    Glide.with(ProblemActivity.this)
                            .load(mSelected.get(position).toString())
                            .into(iv);
//                    iv.setImageResource(R.mipmap.ic_launcher);
                }
            }
            return convertView;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mSelected && mSelected.size() != 0) {
//            outState.putString("mSelected", mSelected.toString());
            Bundle mBundle = new Bundle();
            mBundle.putParcelableArrayList("mSelected", mSelected);
            outState.putBundle("mBundle", mBundle);
//            outState.putParcelableArrayList("mSelected1",mSelected);
        }
    }

    /**
     * 在 onResume之前恢复上次保存的信息
     *
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Bundle mBundle = new Bundle();
        mBundle = savedInstanceState.getBundle("mBundle");
        if (null != mBundle) {
            mSelected = mBundle.getParcelableArrayList("mSelected");
//            if (null != mSelected && mSelected.size() == 0){
//               if(null != select_pic_num_tip) select_pic_num_tip.setText(R.string.chose_pic);
//            }else{
//                if(null != select_pic_num_tip) select_pic_num_tip.setText("上传照片("+ mSelected.size() +"/4)");
//            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
