package com.netease.music.ui.state;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.netease.music.domain.request.AccountRequest;

public class PhoneLoginViewModel extends ViewModel {

    //输入手机号界面
    public final ObservableBoolean showInputPhoneView = new ObservableBoolean();
    //输入密码界面
    public final ObservableBoolean showInputPasswordView = new ObservableBoolean();
    //忘记密码界面
    public final ObservableBoolean showForgetPasswordView = new ObservableBoolean();
    //显示验证码界面
    public final ObservableBoolean showCaptureView = new ObservableBoolean();
    //使能重新获取验证码按钮
    public final ObservableBoolean enableCaptureButton = new ObservableBoolean();
    //密码hint
    public final ObservableField<String> passwordHint = new ObservableField<>();
    //界面标题
    public final ObservableField<String> title = new ObservableField<>();
    //1min 倒计时输入验证码
    public final ObservableField<String> countDownText = new ObservableField<>();
    //手机号
    public final ObservableField<String> phone = new ObservableField<>();
    //密码
    public final ObservableField<String> password = new ObservableField<>();

    public final AccountRequest accountRequest = new AccountRequest();

    {
        showInputPhoneView.set(true);
        showInputPasswordView.set(false);
        title.set("手机号登录");
        phone.set("");
        enableCaptureButton.set(false);
    }
}
