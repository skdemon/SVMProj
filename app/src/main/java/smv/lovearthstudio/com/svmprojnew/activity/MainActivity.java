package smv.lovearthstudio.com.svmprojnew.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import libsvm.svm;
import smv.lovearthstudio.com.svmprojnew.R;
import smv.lovearthstudio.com.svmprojnew.fragment.CollectionFragment;
import smv.lovearthstudio.com.svmprojnew.fragment.PredictFragment;
import smv.lovearthstudio.com.svmprojnew.fragment.SensorFragment;
import smv.lovearthstudio.com.svmprojnew.svm.SVM;

import static java.io.File.separator;
import static smv.lovearthstudio.com.svmprojnew.svm.SVM.inputStreamToArray;
import static smv.lovearthstudio.com.svmprojnew.util.Constant.dir;
import static smv.lovearthstudio.com.svmprojnew.util.Constant.modelFileName;
import static smv.lovearthstudio.com.svmprojnew.util.Constant.rangeFileName;
import static smv.lovearthstudio.com.svmprojnew.util.Constant.train;
import static smv.lovearthstudio.com.svmprojnew.util.Constant.trainFileName;
import static smv.lovearthstudio.com.svmprojnew.util.PermissionUtil.requestWriteFilePermission;

public class MainActivity extends FragmentActivity {

    Button mBtnTest, mBtnReal, mBtnCollectionData;
    SVM mSvm;
    private MediaPlayer mp =new MediaPlayer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestWriteFilePermission(this);
        init();
    }

    /**
     * 初始化操作
     */
    private void init() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, new CollectionFragment()).commit();

        crateDataDir();

        copyFileToSd();

        findView();

        loadModelAndRange();

    }

    /**
     * copy model 和 range文件到sd卡
     */
    private void copyFileToSd() {
        try {
            copyFileToSd(getAssets().open("model"), dir + separator + modelFileName);
            copyFileToSd(getAssets().open("range"), dir + separator + rangeFileName);
            copyFileToSd(getAssets().open("train"), dir + separator + trainFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * copy文件到sd卡
     */
    private void copyFileToSd(InputStream in, String targetFilePath) {
        FileOutputStream fileOutputStream = null;
        File file = new File(targetFilePath);
        if (file.exists()) {        // 如果文件已经存在就结束
            return;
        }
        try {
            fileOutputStream = new FileOutputStream(targetFilePath);
            int len = 0;
            byte[] b = new byte[1024];
            while ((len = in.read(b)) != -1) {
                fileOutputStream.write(b, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建数据目录
     */
    private void crateDataDir() {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        File trainFile = new File(dir + separator + train);
        if (!trainFile.exists()) {
            trainFile.mkdirs();
        }
    }

    /**
     * 加载model和range
     */
    private void loadModelAndRange() {
        try {
            mSvm = new SVM(svm.svm_load_model(
                    new BufferedReader(new InputStreamReader(new FileInputStream(dir + separator + modelFileName)))),
                    inputStreamToArray(new FileInputStream(dir + separator + rangeFileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id找到view
     */
    private void findView() {
        mBtnTest = (Button) findViewById(R.id.btn_test);
        mBtnReal = (Button) findViewById(R.id.btn_real);
        mBtnCollectionData = (Button) findViewById(R.id.btn_collection_data);
        mBtnCollectionData.setEnabled(false);
    }

    public void btnClick(View view) {
        final  MediaPlayer mp = MediaPlayer.create(this,R.raw.cai);
        switch (view.getId()) {
            case R.id.btn_collection_data:
                mBtnCollectionData.setEnabled(false);
                mBtnTest.setEnabled(true);
                mBtnReal.setEnabled(true);
                mp.start();
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, new CollectionFragment()).commit();
                break;
            case R.id.btn_test:
                mBtnCollectionData.setEnabled(true);
                mBtnTest.setEnabled(false);
                mBtnReal.setEnabled(true);
                mp.start();
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, new PredictFragment()).commit();
                break;
            case R.id.btn_real:
                mBtnCollectionData.setEnabled(true);
                mBtnTest.setEnabled(true);
                mBtnReal.setEnabled(false);
                mp.start();
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, new SensorFragment()).commit();
                break;
        }
    }

    public boolean predictUnscaledTrain(String[] unScaleData) {
        return mSvm.predictUnscaledTrain(unScaleData);
    }

    public double predictUnscaled(String[] unScaleData) {
        return mSvm.predictUnscaled(unScaleData, false);
    }

}
