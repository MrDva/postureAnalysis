package com.baidu.ai;


import com.baidu.ai.api.*;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RealMainActivity extends CameraActivity  {
    //FrameLayout picture;

    Bitmap bitmap;
    //保留正在显示的图片，以便保存
    Mat frame_front;
    Mat frame_side;
    //图片路径
    private String path ;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if((Integer)msg.obj==0){

            }
            super.handleMessage(msg);
        }
    };
    private bodyPoint bodyPoints_font=new bodyPoint();
    private bodyPoint bodyPoints_side=new bodyPoint();//保留关键点信息
    private String front_path="";
    private String side_path="";
    //判断返回到的Activity
    private static final int IMAGE_REQUEST_CODE = 0;
    private final int RequestCode = 1;

    private int front_flag=0;
    private int side_flag=0;
    private boolean isfront=true;
    private int changefront=0,changeside=0;
    private  static String TAG=RealMainActivity.class.getName();
    private static String TAT="getPhotoFromphone";
    private Button Button_Analysis,Button_Cancel,Button_take,Button_save;
    TextView TextView;
    private ImageView front_imageView,side_imageView;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully!");
                    //mOpencvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // OpenCV manager initialization

        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
        } else {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
        }
        //OpenCVLoader.initDebug();
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button_Analysis=(Button)findViewById(R.id.button_analysis);
        Button_Cancel=(Button)findViewById(R.id.button_select);
        Button_take=(Button)findViewById(R.id.button_take);
        front_imageView=(ImageView)findViewById(R.id.front_imageView);
        side_imageView=(ImageView)findViewById(R.id.side_imageView);
        TextView=(TextView)findViewById(R.id.text);
        //申请小量数据网络访问
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        vioerifyPermissn(this);
        //按钮触发事件
        View.OnClickListener buttonListener=new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch(v.getId())
                {
                    case R.id.button_analysis:
                        Analysis();

                        break;
                    case R.id.button_select:
                        showTheSkeleton();
                        break;
                    case R.id.button_take:
                        save();
                        break;
                    case R.id.front_imageView:
                        isfront=true;
                        select();
                        changefront=0;
                        break;
                    case R.id.side_imageView:
                        isfront=false;
                        select();
                        changeside=0;
                        break;
                    default:break;

                }
            }
        };
        Button_Analysis.setOnClickListener(buttonListener);
        Button_Cancel.setOnClickListener(buttonListener);
        Button_take.setOnClickListener(buttonListener);
        front_imageView.setOnClickListener(buttonListener);
        side_imageView.setOnClickListener(buttonListener);
        TextView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }



    //找到关键点并返回
    public static String body_analysis(String filePath) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/body_analysis";
        try {
            // 本地文件路径
            //String filepath = "asuka.jpg";
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = "24.6588cf78c3dfb6eaab889fd5017b1eaa.2592000.1641529299.282335-25304943";

            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
        return null;
    }

    //提取返回关键点并绘制
    public void Analysis(){
        String result_text="";
        if(front_path==""){
            Toast.makeText(RealMainActivity.this, "找不到正面照，请添加", Toast.LENGTH_SHORT).show();
        }
        else{
            String result=body_analysis(front_path);
            Mat frame= Imgcodecs.imread(front_path);
            // Imgproc.cvtColor(frame,frame,Imgproc.COLOR_BGR2RGBA,4);
            bodyPoints_font=new bodyPoint();
            int people_num=bodyPoints_font.people_num(result);
            if(people_num==1){
            bodyPoints_font.myspilt(result);
            Log.i(TAG,result);
            bodyPoints_font.drawPoint(frame);
            frame_front=frame;
            changefront=1;
            bodyPoints_font.hasNoVaule=false;
            result_text=result_text+bodyPoints_font.cauculate1();
            result_text=result_text+bodyPoints_font.cauculate2();
            result_text=result_text+bodyPoints_font.cauculate3();
            Bitmap bmp=null;
            bmp=Bitmap.createBitmap(frame.width(),frame.height(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(frame,bmp);
            front_imageView.setImageBitmap(bmp);
            front_imageView.invalidate();
            front_imageView.setVisibility(View.VISIBLE);}
            else{
                Toast.makeText(RealMainActivity.this, "正面照检测到0个人体或多个人体，请重新添加图片", Toast.LENGTH_SHORT).show();

            }

        }
        if(side_path==""){
            Toast.makeText(RealMainActivity.this, "找不到侧面照，请添加", Toast.LENGTH_SHORT).show();
        }
        else{
            String result=body_analysis(side_path);
            Mat frame= Imgcodecs.imread(side_path);
            // Imgproc.cvtColor(frame,frame,Imgproc.COLOR_BGR2RGBA,4);
            bodyPoints_side=new bodyPoint();
            int people_num=bodyPoints_side.people_num(result);
            if(people_num==1){
            bodyPoints_side.myspilt(result);
            Log.i(TAG,result);
            bodyPoints_side.drawPoint(frame);
            frame_side=frame;
            bodyPoints_side.hasNoVaule=false;
            result_text=result_text+bodyPoints_side.cauculate4();
            result_text=result_text+bodyPoints_side.cauculate5();
            Bitmap bmp=null;
            bmp=Bitmap.createBitmap(frame.width(),frame.height(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(frame,bmp);
            side_imageView.setImageBitmap(bmp);
            side_imageView.invalidate();
            side_imageView.setVisibility(View.VISIBLE);
            changeside=1;}
            else{
                Toast.makeText(RealMainActivity.this, "侧面照检测到0个人体或多个人体，请重新添加图片", Toast.LENGTH_SHORT).show();

            }
        }
        if(result_text!=""){TextView.setText(result_text);}
    }

    //待改进，现为显示图片
    public void showPicture(String filePath){
        if(filePath=="asuka.jpg"){
            filePath=getPath("asuka.jpg",this);
        }
        Mat frame= Imgcodecs.imread(filePath);
        if(!frame.empty()){
            // Imgproc.cvtColor(frame,frame,Imgproc.COLOR_BGR2RGBA,4);
            Bitmap bmp=null;
            bmp=Bitmap.createBitmap(frame.width(),frame.height(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(frame,bmp);
            if(isfront){
                changefront=0;
                bodyPoints_font.hasNoVaule=false;
                frame_front=frame;
                front_imageView.setImageBitmap(bmp);
                front_imageView.invalidate();
                front_imageView.setVisibility(View.VISIBLE);
                front_path=filePath;
            }
            else {
                changeside=0;
                bodyPoints_side.hasNoVaule=false;
                frame_side=frame;
                side_imageView.setImageBitmap(bmp);
                side_imageView.invalidate();;
                side_imageView.setVisibility(View.VISIBLE);
                side_path=filePath;
            }
        }
        else{Log.i(TAG,"找不到指定文件");}
    }

    //得到文件地址
    private static String getPath(String file, Context context){
        AssetManager assetManager=context.getAssets();
        BufferedInputStream inputStream;
        try{
            //read the picture form assets
            inputStream=new BufferedInputStream(assetManager.open(file));
            byte[] data=new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            File outFile=new File(context.getFilesDir(),file);
            FileOutputStream os=new FileOutputStream(outFile);
            os.write(data);
            os.close();
            return outFile.getAbsolutePath();
        }catch (IOException e)
        {
            Log.i(TAG,"failed to upload a picture");
        }
        return "";
    }



    //拍摄正面图像
    public void TakeFrontImage(){
        if(front_flag==0){
            // front_flag=1;
            Bundle bundle=new Bundle();
            bundle.putInt("flag",0);
            Intent intent=new Intent(RealMainActivity.this,MyCameraActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent,RequestCode);

        }
    }


    //拍摄侧面图像
    public void TakeSideImage(){

        if(side_flag==0){
            //  side_flag=1;
            Bundle bundle=new Bundle();
            bundle.putInt("flag",1);
            Intent intent=new Intent(RealMainActivity.this,MyCameraActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent,RequestCode);//传递拍摄要求信息（正面或侧面），并返回图片地址信息

        }
    }


    //返回信息函数
    protected void onActivityResult(int requestCode,int resultCode
            ,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        System.out.println(requestCode);
        switch(requestCode){
            case RequestCode:
                if(resultCode==MyCameraActivity.ResultCode){
                    Bundle bundle=data.getExtras();
                    String result=bundle.getString("path");
                    showPicture(result);
                }
                break;
            case IMAGE_REQUEST_CODE:

                handleImageOmKitKat(data);
                // handleImageBeforeKitKat(data);
                chnage();
                break;
        }
    }

    //待完善
    protected void selectFromPhone(){
        if(ContextCompat.checkSelfPermission(RealMainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(RealMainActivity.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },1);
        }
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

//获取选择的本地图片路径信息
    @TargetApi(19)
    private void handleImageOmKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            //如果document类型是U日，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是普通类型 用普通方法处理
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            //如果file类型位uri直街获取图片路径即可
            imagePath = uri.getPath();
        }
        Log.i(TAT,imagePath);
        displayImage(imagePath);
    }

//延时函数，确保已从本地获取到图片
    public void chnage(){
        new Thread(){
            @Override
            public void run() {
                while ( bitmap == null ){
                   // bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(path));
                   bitmap = BitmapFactory.decodeFile(path);
                    Log.i("qwe","123");
                }
                Message message = handler.obtainMessage();
                message.obj = 0;
                handler.sendMessage(message);
            }
        }.start();
    }


   //得到本地图片路径
    private String getImagePath(Uri uri, String selection){
        String path = null;
        //通过Uri和selection来获取真实图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    //将从手机获得的图片进行处理
    private void displayImage(String imagePath){
        if (imagePath != null){
            Mat frame=Imgcodecs.imread(imagePath);
            if(frame.empty())Toast.makeText(RealMainActivity.this,"找不到文件",Toast.LENGTH_SHORT).show();
            int max;
            if(frame.rows()>frame.cols())
                max=frame.rows();
            else max=frame.cols();
            if(max>1024){
                double i=max/800;
                Imgproc.resize(frame,frame,new Size(frame.width()/i,frame.height()/i));
            }
            Imgproc.cvtColor(frame,frame,Imgproc.COLOR_BGR2RGBA,4);
            if(isfront){
                front_path=savetoAseet(frame,"front.jpg",this);
                showPicture(front_path);}
            else {
                side_path=savetoAseet(frame,"side.jpg",this);
                showPicture(side_path);
            }
        }else {
            Toast.makeText(RealMainActivity.this,"获取相片失败",Toast.LENGTH_SHORT).show();
        }
    }


//申请访问手机内存
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void vioerifyPermissn(Context context){
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    RealMainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    public String savetoAseet(Mat frame,String file,Context context){
        // AssetManager assetManager=context.getAssets();
        String filePath=context.getFilesDir().toString()+file;

        // new File()
        Imgcodecs.imwrite(filePath,frame);



        return filePath;
    }

//选择获取图片方式
    private void select(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("添加图片方式");
        alertBuilder.setItems(new CharSequence[]{
                        "拍照",
                        "从本地文件中获取"
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if(isfront)
                                    TakeFrontImage();
                                else
                                    TakeSideImage();
                                break;
                            case 1:
                                selectFromPhone();
                                break;
                        }
                    }
                }
        ).show();


    }
    private void showTheSkeleton(){
            if(bodyPoints_side.hasNoVaule||changeside==0){
                Toast.makeText(RealMainActivity.this, "找不到侧面照或未进行分析，请添加或先分析", Toast.LENGTH_SHORT).show();
            }
            else{

                bodyPoints_side.drawSkeleton(frame_side);
                Bitmap bmp=null;
                bmp=Bitmap.createBitmap(frame_side.width(),frame_side.height(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(frame_side,bmp);
                side_imageView.setImageBitmap(bmp);
                side_imageView.invalidate();
                side_imageView.setVisibility(View.VISIBLE);
            }
            if(bodyPoints_font.hasNoVaule||changefront==0){
                Toast.makeText(RealMainActivity.this, "找不到正面照或未进行分析，请添加或先分析", Toast.LENGTH_SHORT).show();

            }
            else{

                bodyPoints_font.drawSkeleton(frame_front);
                Bitmap bmp=null;
                bmp=Bitmap.createBitmap(frame_front.width(),frame_front.height(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(frame_front,bmp);
                front_imageView.setImageBitmap(bmp);
                front_imageView.invalidate();
                front_imageView.setVisibility(View.VISIBLE);
            }
    }

    public void save(){
        if(bodyPoints_font.hasNoVaule){
            Toast.makeText(RealMainActivity.this, "找不到正面照，请先添加", Toast.LENGTH_SHORT).show();

        }
        else{
        Bitmap bmp=null;
        bmp=Bitmap.createBitmap(frame_front.width(),frame_front.height(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(frame_front,bmp);
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        saveBitmap(bmp,format.format(new Date())+"_front.JPEG");
        Toast.makeText(RealMainActivity.this, "当前正面照已保存", Toast.LENGTH_SHORT).show();
        }
        if(bodyPoints_side.hasNoVaule){
            Toast.makeText(RealMainActivity.this, "找不到侧面照，请先添加", Toast.LENGTH_SHORT).show();

        }
        else{
            Bitmap bmp=null;
            bmp=Bitmap.createBitmap(frame_side.width(),frame_side.height(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(frame_side,bmp);
            DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            saveBitmap(bmp,format.format(new Date())+"_side.JPEG");
            Toast.makeText(RealMainActivity.this, "当前侧面照已保存", Toast.LENGTH_SHORT).show();

        }
    }

    //保存图片到手机
    public void saveBitmap(Bitmap bitmap, String bitName){
        String fileName ;
        File file ;
        if(Build.BRAND .equals("Xiaomi") ){ // 小米手机
            fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/"+bitName ;
        }else{ // Meizu 、Oppo
            fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/"+bitName ;
        }
        file = new File(fileName);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out))
            {
                out.flush();
                out.close();
// 插入图库
                MediaStore.Images.Media.insertImage(this.getContentResolver(), file.getAbsolutePath(), bitName, null);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        // 发送广播，通知刷新图库的显示
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
    }
}