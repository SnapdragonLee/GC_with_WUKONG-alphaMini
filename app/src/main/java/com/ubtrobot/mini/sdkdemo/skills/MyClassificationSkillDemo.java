package com.ubtrobot.mini.sdkdemo.skills;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.ubtechinc.sauron.api.TakePicApi;
import com.ubtechinc.skill.ProxySkill;
import com.ubtechinc.skill.SkillType;
import com.ubtrobot.commons.Priority;
import com.ubtrobot.commons.ResponseListener;
import com.ubtrobot.master.annotation.Call;
import com.ubtrobot.master.param.ProtoParam;
import com.ubtrobot.master.skill.SkillInfo;
import com.ubtrobot.master.skill.SkillStopCause;
import com.ubtrobot.mini.sdkdemo.DemoApp;
import com.ubtrobot.mini.sdkdemo.tools.ClassificationSkillThread;
import com.ubtrobot.mini.sdkdemo.tools.JsonTool;
import com.ubtrobot.mini.sdkdemo.tools.MyClient;
import com.ubtrobot.mini.sysevent.event.base.KeyEvent;
import com.ubtrobot.mini.voice.VoicePool;
import com.ubtrobot.speech.protos.Speech;
import com.ubtrobot.speech.protos.TvsSkill;
import com.ubtrobot.transport.message.Request;
import com.ubtrobot.transport.message.Responder;

import java.io.IOException;
import java.util.List;

public class MyClassificationSkillDemo extends ProxySkill {
    private static final String TAG = DemoApp.DEBUG_TAG;
    private final String skillName = "ClassificationSkill";
    private TakePicApi takePicApi;
    private static String picturePath = null;
    private MyClient client = new MyClient();
    private VoicePool voicePool;

    @Override
    protected String getSkillName() {
        return skillName;
    }

    @Override
    protected void onSkillStart() {
        super.onSkillStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Call(path = "/my_classification/startSkill")
    //自定义，最好唯一标识 需要和  xml/目录下的 <call path="/demo_mySkill/startSkill"> 这段一致
    //语音触发命中指定的意图后会调用这里
    public void doSameThing(Request request, Responder responder) {
        try {
            //这段是获取槽位参数和技能服务下发的透传参数，没有参数的话可不不需要这段代码
            TvsSkill.SkillParam skillParam = ProtoParam.from(request.getParam(), TvsSkill.SkillParam.class).getProtoMessage();
            List<TvsSkill.ParamPair> params = skillParam.getItemsList();
            String controlData = skillParam.getControlData();
            //params 可以获取到叮当上的槽位参数
            // controlData 可以读取自定义技能服务下发的透传参数
        } catch (ProtoParam.InvalidProtoParamException e) {
            e.printStackTrace();
        }
        responder.respondSuccess();
        mainActive();
        //处理自己的事情
        //.........

        // 处理完自己的事情后需要结束技能，调用以下方法退出对应的技能

        stopSkill();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void mainActive() {
        takePicApi = TakePicApi.get();
        voicePool = VoicePool.get();
        takePicApi.takePicImmediately(new ResponseListener<String>() {

            @Override
            public void onResponseSuccess(String string) {
                Log.i(TAG, "takePicImmediately接口调用成功！");
                // Toast.makeText(getApplicationContext(), "saving " + string, Toast.LENGTH_LONG).show();
                ClassificationSkillThread classificationSkillThread = new ClassificationSkillThread();
                classificationSkillThread.setImgPath(string);
                classificationSkillThread.start();
            }

            @Override
            public void onFailure(int errorCode, @NonNull String errorMsg) {
                Log.i(TAG, "takePicImmediately接口调用失败,errorCode======" + errorCode + ",errorMsg======" + errorMsg);
                voicePool.playTTs("拍照失败了呜呜呜", Priority.HIGH, null);
            }
        });

    }

    @Override
    public void onSkillStarted(SkillInfo skillInfo) {
        super.onSkillStarted(skillInfo);

    }

    @Override
    protected void onSkillStop(SkillStopCause skillStopCause) {
        //技能退出，包括主动退出和被打断等，需要处理业务停止和资源释放操作
    }

    @Override
    protected SkillType getSkillType() {
        return SkillType.Interruptible; //此处定义技能的类型,详情见：SkillType，目前支持：Interruptible、Uninterruptible 两种
    }

    @Override
    protected SkillType getSubSkillType() {
        return null;
    } //此处定义skill 子类别，可暂时返回null

    //拍头事件接收
    @Override
    protected boolean onHeadTapEvent(KeyEvent keyEvent) {
        return false;
    }

    //唤醒事件接收
    @Override
    protected void onWakeUpEvent(Speech.WakeupParam wakeupParam) {

    }

    //是否需要唤醒事件回调
    @Override
    protected boolean isNeedWakeUpEvent() {
        return false;
    }

    //是否需要拍头事件回调
    @Override
    protected boolean isNeedHeadTapEvent() {
        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void doSkill() {
        MyClassificationSkillDemo myClassificationSkillDemo = new MyClassificationSkillDemo();
        myClassificationSkillDemo.mainActive();
    }

    public static void setPicturePath(String _picturePath) {
        picturePath = _picturePath;
    }
}
