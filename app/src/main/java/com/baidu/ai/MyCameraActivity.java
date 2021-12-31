package com.baidu.ai;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyCameraActivity extends CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private String  front_path="";
    private Mat rgbaMat=null;
    public static final int ResultCode = 1;
    private String side_path="";
    private String front_name="front.jpg";
    private String side_name="side.jpg";
    private int flag=0;
    private int save_flag=0;
    private final int on=1;
    private  Button Button_save;
    private  String TAT="frame";
    private CameraBridgeViewBase mOpencvCameraView;
    private  static String TAG=MainActivity.class.getName();
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully!");
                    mOpencvCameraView.enableView();
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
        OpenCVLoader.initDebug();
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Bundle bundle=getIntent().getExtras();
        flag =bundle.getInt("flag");

        Button_save=(Button)findViewById(R.id.button_save);
        System.out.println("first");
        //按钮触发事件
        View.OnClickListener buttonListener=new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch(v.getId())
                {

                    case R.id.button_save:
                        System.out.println("flag"+flag);
                        savePicture();
                        break;
                    default:break;

                }
            }
        };

        Button_save.setOnClickListener(buttonListener);
        //拍照
        mOpencvCameraView=(CameraBridgeViewBase)findViewById(R.id.CameraView);
        mOpencvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpencvCameraView.setCvCameraViewListener(this);
        //mOpencvCameraView.enableView();
    }

    public void savePicture(){
        save_flag=1;
        //savetoAseet();
    }

    //保存图像并返回图像地址
    public String savetoAseet(Mat frame,String file,Context context){
        // AssetManager assetManager=context.getAssets();
        String filePath=context.getFilesDir().toString()+file;
        System.out.println("file:"+filePath);
        // new File()
        Imgcodecs.imwrite(filePath,frame);



        return filePath;
    }
    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpencvCameraView);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        try{

        Mat frame=inputFrame.rgba();
        if(getResources().getConfiguration().orientation== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        Core.rotate(frame, frame, Core.ROTATE_90_CLOCKWISE);
        Size size = new Size(600, 800);
        Imgproc.resize(frame, frame, size);
        if(save_flag==on){
            save_flag=0;
            System.out.println("mat");
            Intent intent=new Intent();

        if(flag==0){
            front_path=savetoAseet(frame,front_name,this);
            intent.putExtra("path",front_path);
            System.out.println(front_name+"  "+front_path);
        }
        else{
            side_path=savetoAseet(frame,front_name,this);
            intent.putExtra("path",side_path);
        }

        setResult(ResultCode,intent);
        //String dd="size:"+frame.size().toString();
        //Log.i(TAG,dd);
        //mOpencvCameraView.disableView();
        finish();

        }

        return frame;}
        catch (Exception e){
            Log.e(TAT,e.toString());
        }

        return inputFrame.rgba();
    }
}
