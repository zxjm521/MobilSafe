package com.itheima.mobilesafe.egine;

import java.util.ArrayList;
import java.util.List;

import com.itheima.mobilesafe.domain.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * 获取应用的信息引擎类
 * @author ZXJM
 * 2016年9月12日
 */
public class AppInfoPrivider {
	/**
	 * 获取已安装的应用的信息
	 * @param context
	 */
	public static List<AppInfo> getAppInfos(Context context){
		PackageManager pm = context.getPackageManager();
		//类似程序清单文件的集合
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : packageInfos) {
			AppInfo appInfo = new AppInfo();
			String name = packageInfo.applicationInfo.loadLabel(pm).toString();
			String packageName = packageInfo.packageName;
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
			boolean isRom;
			boolean isSystem;
			int flags = packageInfo.applicationInfo.flags; //应用程序的标记，相当于提交的机读卡试卷
			//google工程师给的算法：
			if((flags&ApplicationInfo.FLAG_SYSTEM)== 0){
			  //不是系统应用
				isSystem = false;
			}else{
				//系统应用
				isSystem = true;
			}
			
			if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0){
				//没有安装在sd卡
				isRom = true;
			}else{
			    isRom = false;
			}
			
			appInfo.setAppName(name);
			appInfo.setPackageName(packageName);
			appInfo.setIcon(icon);
			appInfo.setSystemApp(isSystem);
			appInfo.setRom(isRom);
			appInfos.add(appInfo);
			
		}
		return appInfos;
	}
	
	
}
