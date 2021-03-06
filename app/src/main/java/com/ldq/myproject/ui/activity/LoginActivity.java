package com.ldq.myproject.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldq.myproject.R;
import com.ldq.myproject.bean.UserInfo;
import com.ldq.myproject.common.Constant;
import com.ldq.myproject.common.PreferencesManager;
import com.ldq.myproject.util.LogUtils;
import com.ldq.myproject.util.ToastUtils;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.OtherLoginListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.friends.Wechat;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.login_photo)
    ImageView loginPhoto;
    @BindView(R.id.login_name)
    EditText loginName;
    @BindView(R.id.login_password)
    EditText loginPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.iv_qq)
    ImageView ivQq;
    @BindView(R.id.iv_wechat)
    ImageView ivWechat;
    @BindView(R.id.iv_tencentweibo)
    ImageView ivTencentweibo;
    @BindView(R.id.iv_sinaweibo)
    ImageView ivSinaweibo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Bmob.initialize(this, Constant.BMOB_ID);
    }

    @OnClick({R.id.btn_login, R.id.tv_forget_password, R.id.tv_register, R.id.iv_qq, R.id.iv_wechat, R.id.iv_tencentweibo, R.id.iv_sinaweibo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_forget_password:
                break;
            case R.id.tv_register:
                register();
                break;
            case R.id.iv_qq:
                loginByQQ();
                break;
            case R.id.iv_wechat:
                loginByWeChat();
                break;
            case R.id.iv_tencentweibo:
                loginByTencentWeiBo();
                break;
            case R.id.iv_sinaweibo:
                loginBySinaWeiBo();
                break;
        }
    }

    private void login() {
        final String name = loginName.getText().toString();
        final String pwd = loginPassword.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
            ToastUtils.shortToast(LoginActivity.this, getString(R.string.name_or_pwd_is_null));
            return;
        }

        UserInfo user = new UserInfo();
        user.setUsername(name);
        user.setPassword(pwd);
        user.login(this, new SaveListener() {
            @Override
            public void onSuccess() {
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
                ToastUtils.shortToast(LoginActivity.this, getString(R.string.login_succes));
                saveUserInfo(Constant.LOGIN_TYPE_NORMAL, null);
            }

            @Override
            public void onFailure(int i, String s) {
                ToastUtils.shortToast(LoginActivity.this, getString(R.string.name_or_pwd_error));
                clearInput();
            }
        });
    }

    private void register() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void loginByQQ() {
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        qq.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                arg2.printStackTrace();
            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                // TODO Auto-generated method stub
                //输出所有授权信息
                PlatformDb data = arg0.getDb();
                BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth(getString(R.string.qq), data.getToken(), String.valueOf(data.getExpiresIn()), data.getUserId());
                loginWithAuth(authInfo, data);
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                // TODO Auto-generated method stub
            }
        });
        //authorize与showUser单独调用一个即可
        //qq.authorize();//单独授权,OnComplete返回的hashmap是空的
        qq.showUser(null);//授权并获取用户信息
        //移除授权
        //qq.removeAccount(true);
    }

    private void loginByWeChat() {
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        wechat.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                arg2.printStackTrace();
            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                // TODO Auto-generated method stub
                //输出所有授权信息
                PlatformDb data = arg0.getDb();
                BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth("weixin", data.getToken(), String.valueOf(data.getExpiresIn()), data.getUserId());
                loginWithAuth(authInfo, data);
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                // TODO Auto-generated method stub
            }
        });
        //authorize与showUser单独调用一个即可
        //wechat.authorize();//单独授权,OnComplete返回的hashmap是空的
        wechat.showUser(null);//授权并获取用户信息
        //移除授权
        //wechat.removeAccount(true);
    }

    private void loginByTencentWeiBo() {
        Platform tencentweibo = ShareSDK.getPlatform(TencentWeibo.NAME);
        //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        tencentweibo.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                arg2.printStackTrace();
            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                // TODO Auto-generated method stub
                //输出所有授权信息
                PlatformDb data = arg0.getDb();
                BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth("weibo", data.getToken(), String.valueOf(data.getExpiresIn()), data.getUserId());
                loginWithAuth(authInfo, data);
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                // TODO Auto-generated method stub
            }
        });
        //authorize与showUser单独调用一个即可
        //tencentweibo.authorize();//单独授权,OnComplete返回的hashmap是空的
        tencentweibo.showUser(null);//授权并获取用户信息
        //移除授权
        //tencentweibo.removeAccount(true);
    }

    private void loginBySinaWeiBo() {
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        weibo.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                arg2.printStackTrace();
            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                // TODO Auto-generated method stub
                //输出所有授权信息
                PlatformDb data = arg0.getDb();
                BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth("weibo", data.getToken(), String.valueOf(data.getExpiresIn()), data.getUserId());
                loginWithAuth(authInfo, data);
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                // TODO Auto-generated method stub
            }
        });
        //authorize与showUser单独调用一个即可
        //weibo.authorize();//单独授权,OnComplete返回的hashmap是空的
        weibo.showUser(null);//授权并获取用户信息
        //移除授权
        //weibo.removeAccount(true);
    }

    public void loginWithAuth(final BmobUser.BmobThirdUserAuth authInfo, final PlatformDb data) {
        BmobUser.loginWithAuthData(LoginActivity.this, authInfo, new OtherLoginListener() {

            @Override
            public void onSuccess(JSONObject userAuth) {
                // TODO Auto-generated method stub
                LogUtils.i(authInfo.getSnsType() + getString(R.string.login_success_return) + userAuth);
                UserInfo user = BmobUser.getCurrentUser(LoginActivity.this, UserInfo.class);
                //更新登录的账户信息
                updateUserInfo(user, data, authInfo);
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                ToastUtils.shortToast(LoginActivity.this, getString(R.string.third_login_failure) + msg);
            }
        });
    }

    private void updateUserInfo(UserInfo user, PlatformDb data, final BmobUser.BmobThirdUserAuth authInfo) {
        UserInfo newUser = new UserInfo();
        newUser.setPhoto(data.getUserIcon());
        newUser.setSex("男".equals(data.getUserGender()) ? true : false);
        newUser.setUsername(data.getUserName());
        UserInfo bmobUser = BmobUser.getCurrentUser(LoginActivity.this, UserInfo.class);
        newUser.update(LoginActivity.this, bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                ToastUtils.shortToast(LoginActivity.this, getString(R.string.update_userinfo_success));
                //保存登录信息到本地
                saveUserInfo(Constant.LOGIN_TYPE_THIRD, authInfo);
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                ToastUtils.shortToast(LoginActivity.this, getString(R.string.update_userinfo_failed) + msg);
            }
        });
    }

    private void saveUserInfo(int loginType, BmobUser.BmobThirdUserAuth authInfo) {
        /*
         * TODO 把用户的登录信息保存到本地：sp\sqlite：（登录状态，登录类别，登录账户信息）
         * 注意:为了保证数据安全，一般对数据进行加密
         * 通过BmobUser user = BmobUser.getCurrentUser(context)获取登录成功后的本地用户信息
         * 如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(context,MyUser.class)获取自定义用户信息
         * */
        UserInfo user = BmobUser.getCurrentUser(LoginActivity.this, UserInfo.class);
        PreferencesManager preferences = PreferencesManager.getInstance(LoginActivity.this);
        preferences.put(Constant.IS_LOGIN, true);
        preferences.put(Constant.LOGINTYPE, loginType);
        preferences.put(Constant.USER_NAME, user.getUsername());
        preferences.put(Constant.USER_PHOTO, user.getPhoto());
        preferences.put(Constant.USER_PWD, loginPassword.getText().toString());
        if (authInfo != null) {
            preferences.put(authInfo);
        }
        LoginActivity.this.finish();
    }

    private void clearInput() {
        loginName.setText("");
        loginPassword.setText("");
    }
}
