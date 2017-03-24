package com.itheima.mobilesafe.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.itheima.mobilesafe.base.MyApplication;

/**
 * ui的工具类，很多事情委托它来帮助完成。
 * Created by Administrator on 2016/10/2.
 */
public class UIUtils {

    public static Context getContext() {
        return MyApplication.getContext();
    }

    public static Handler getHandler() {
        return MyApplication.getHandler();
    }
    public static int getMainThreadId() {
        return MyApplication.getMainThreadId();
    }

    /**************加载资源文件 *****************/

    /**
     * 加载布局文件
     *
     */
    public static View inflate(int id){
        return View.inflate(getContext(),id,null);
    }
    /**
     * 获取字符串
     */
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    /**
     * 获取字符串数组
     */
    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    /**
     * 获取颜色
     */
    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    /**
     * 获取图片资源
     */
    public static Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    /**
     * 根据id获取颜色选择器
     */
    public static ColorStateList getColorStateList(int id) {
        return getContext().getResources().getColorStateList(id);
    }

    /**
     * 获取尺寸
     */
    public static int getDimen(int id) {
        return getContext().getResources().getDimensionPixelSize(id);// 返回具体像素值
    }

    /*********************dip和pix的转换*************/

    public static int dip2px(float dip){
        float desity = getContext().getResources().getDisplayMetrics().density;
        return (int) (desity*dip);
    }

    public static float px2dip(int px){
        float desity = getContext().getResources().getDisplayMetrics().density;
        return px/desity;
    }

    /***************判断是否运行在主线程************/
    public static boolean isRunOnUIThread(){
        int mTid = android.os.Process.myTid();
        if(mTid== getMainThreadId()){
            return true;
        }
        return false;
    }
    /**************在主线程中执行***********/
    public static void RunOnUIThread(Runnable r){
        if(isRunOnUIThread()){
            r.run();
        }else{
            getHandler().post(r);
        }
    }
    
    /*********弹吐司**********/
    public static void ShowToast(String text){
    	Toast.makeText(getContext(), text, 0).show();
    }
}
