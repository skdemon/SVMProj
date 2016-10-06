package smv.lovearthstudio.com.svmprojnew.app;

import android.app.Application;

import com.lovearthstudio.duasdk.Dua;

/**
 * Created by zhaoliang on 16/10/4.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Dua.init(this);
    }
}
