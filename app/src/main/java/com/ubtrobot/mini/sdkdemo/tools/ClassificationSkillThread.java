package com.ubtrobot.mini.sdkdemo.tools;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.ubtechinc.sauron.api.TakePicApi;
import com.ubtrobot.commons.Priority;
import com.ubtrobot.mini.sdkdemo.DemoApp;
import com.ubtrobot.mini.sdkdemo.MainActivity;
import com.ubtrobot.mini.voice.VoicePool;

import java.util.List;

public class ClassificationSkillThread extends Thread {
    private static final String TAG = DemoApp.DEBUG_TAG;
    private String imgPath;
    private VoicePool voicePool;
    private final MyClient client = new MyClient();

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    private String ansAnalyse(String ans) {
        JsonTool jsonTool;
        if (ans.equals("")) {
            voicePool.playTTs("未收到服务器返回信息", Priority.HIGH, null);
            return "";
        }
        try {
            jsonTool = new JsonTool(ans);
        } catch (Exception e) {
            voicePool.playTTs("解析服务器JSON时出错", Priority.HIGH, null);
            return "";
        }
        if (jsonTool.getNumbers() == 0) {
            return "没有找到垃圾哦";
        }
        List<JsonTool.DataEle> data = jsonTool.getData();
        StringBuilder re = new StringBuilder("我看到了" + jsonTool.getNumbers() + "个垃圾,他们是");
        for (JsonTool.DataEle dataEle : data) {
            re.append(dataEle.getName()).append("是").append(dataEle.getType()).append("。");
        }
        Log.i(TAG, re.toString());
        return re.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void mainActive() {
        voicePool = VoicePool.get();
        client.setImgPath(imgPath);
        new Thread(client).start();
        voicePool.playTTs("拍完了，正在分析", Priority.HIGH, null);
        String ans = client.getAns();
        String solution = ansAnalyse(ans);
        voicePool.playTTs(solution, Priority.HIGH, null);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        super.run();
        mainActive();
    }
}
