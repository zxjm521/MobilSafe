package com.itheima.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.base.BaseActivity;
import com.itheima.mobilesafe.domain.AppInfo;
import com.itheima.mobilesafe.egine.AppInfoPrivider;
import com.itheima.mobilesafe.utils.AppSystemUtils;
import com.itheima.mobilesafe.utils.DesityUtils;

public class AppManagerActivity extends BaseActivity implements OnClickListener {
	private TextView tvPhoneSpace;
	private TextView tvSdCardSpace;
	private ListView lvAppList;
	private List<AppInfo> appInfos;
	private List<AppInfo> userApps;
	private List<AppInfo> systemApps;
	private AppListAdapter adapter;
	private String sdAvaiSize;// sd卡可用空间
	private String phoneAvaiSize;// 手机内存可用空间
	private LinearLayout llLoading;
	private TextView tvTag;
	private PopupWindow popwindow;
	// ListView上被点击的应用
	private AppInfo appInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);

		initView();
		getAppInfo();
		registListener();
	}

	private void initView() {
		tvPhoneSpace = (TextView) findViewById(R.id.tv_phone_space);
		tvSdCardSpace = (TextView) findViewById(R.id.tv_sd_space);
		lvAppList = (ListView) findViewById(R.id.lv_app_list);
		llLoading = (LinearLayout) findViewById(R.id.ll_loading);
		tvTag = (TextView) findViewById(R.id.tv_tag);

		sdAvaiSize = AppSystemUtils.getAvaiSpace(this, Environment
				.getExternalStorageDirectory().getAbsolutePath());
		phoneAvaiSize = AppSystemUtils.getAvaiSpace(this, Environment
				.getDataDirectory().getAbsolutePath());

		tvPhoneSpace.setText(phoneAvaiSize);
		tvSdCardSpace.setText(sdAvaiSize);
	}

	/**
	 * 获取数据，刷新界面
	 */
	private void getAppInfo() {
		userApps = new ArrayList<AppInfo>();
		systemApps = new ArrayList<AppInfo>();
//		llLoading.setVisibility(View.VISIBLE);
		showProcessDialog();
		new Thread(new Runnable() {

			@Override
			public void run() {
				appInfos = AppInfoPrivider.getAppInfos(AppManagerActivity.this);
				for (AppInfo appInfo : appInfos) {
					if (appInfo.isSystemApp()) {
						systemApps.add(appInfo);
					} else {
						userApps.add(appInfo);
					}
				}
				// 在主界面更新ui
				runOnUiThread(new Runnable() {
					public void run() {
						if (adapter == null) {
							adapter = new AppListAdapter();
							lvAppList.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						// 隐藏进度对话框
//						llLoading.setVisibility(View.INVISIBLE);
						closeProcessDialog();
					}
				});
			}
		}).start();

	}

	private void registListener() {
		// 列表滑动事件
		lvAppList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			/**
			 * 
			 * view：ListView对象 firstVisibleItem： 第一个可见条目在listview集合里面的位置。
			 * 
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopwindow();
				if (systemApps != null && userApps != null) {
					if (firstVisibleItem > userApps.size()) {
						tvTag.setText("系统程序：" + systemApps.size());
					} else {
						tvTag.setText("用户程序：" + userApps.size());
					}
				}
			}
		});

		// 列表点击事件
		lvAppList.setOnItemClickListener(new OnItemClickListener() {

			private LinearLayout ll_start;
			private LinearLayout ll_uninstall;
			private LinearLayout ll_share;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (position == 0) {
					return;
				} else if (position == (userApps.size() + 1)) {
					return;
				} else if (position <= (userApps.size())) {// 用户应用
					int newPosition = position - 1;
					appInfo = userApps.get(newPosition);
				} else {// 系统应用
					int newPosition = position - userApps.size() - 1 - 1;
					appInfo = systemApps.get(newPosition);
				}
				dismissPopwindow();
				View contentView = View.inflate(getApplicationContext(),
						R.layout.item_pop_window, null);
				ll_start = (LinearLayout) contentView
						.findViewById(R.id.ll_start);
				ll_uninstall = (LinearLayout) contentView
						.findViewById(R.id.ll_uninstall);
				ll_share = (LinearLayout) contentView
						.findViewById(R.id.ll_share);

				ll_start.setOnClickListener(AppManagerActivity.this);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);
				ll_share.setOnClickListener(AppManagerActivity.this);

				popwindow = new PopupWindow(contentView,
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				popwindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				int location[] = new int[2];
				view.getLocationInWindow(location);
				int dip = 60;
				int pix = DesityUtils.dip2pix(dip, getApplicationContext());
				popwindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
						pix, location[1]);

				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				sa.setDuration(300);
				sa.setFillAfter(true);
				AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
				aa.setDuration(300);
				aa.setFillAfter(true);
				AnimationSet set = new AnimationSet(false);
				set.addAnimation(sa);
				set.addAnimation(aa);
				
				contentView.startAnimation(set);
			}
		});
	}

	private void dismissPopwindow() {
		if (popwindow != null && popwindow.isShowing()) {
			popwindow.dismiss();
			popwindow = null;
		}
	}

	class AppListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return systemApps.size() + 1 + userApps.size() + 1;
		}

		@Override
		public AppInfo getItem(int position) {
			return appInfos.get(position);
			// return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo app;
			if (position == 0) {// 用户应用标签
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				int pix = DesityUtils.dip2pix(5, getApplicationContext());
				tv.setPadding(pix, pix, pix, pix);
				tv.setTextSize(16);
				tv.setText("用户程序：" + userApps.size() + "个");
				return tv;

			} else if (position <= userApps.size()) {// 用户程序
				int newPosition = position - 1;
				app = userApps.get(newPosition);
			} else if (position == (userApps.size() + 1)) {// 系统应用标签
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				int pix = DesityUtils.dip2pix(5, getApplicationContext());
				tv.setPadding(pix, pix, pix, pix);
				tv.setTextSize(16);
				tv.setText("系统程序：" + systemApps.size() + "个");
				return tv;
			} else {// 系统应用
				int newPosition = position - 1 - userApps.size() - 1;
				System.out.println("newPosition:" + newPosition);
				System.out.println("position:" + position);
				System.out.println("systemApps:" + systemApps.size());
				System.out.println("userApps:" + userApps.size());
				app = systemApps.get(newPosition);
			}
			ViewHolder holder = null;
			if (convertView != null && (convertView instanceof RelativeLayout)) {
				holder = (ViewHolder) convertView.getTag();
			} else {
				holder = new ViewHolder();
				convertView = View.inflate(AppManagerActivity.this,
						R.layout.list_item_app_manager, null);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.appName = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.location = (TextView) convertView
						.findViewById(R.id.tv_location);
				convertView.setTag(holder);
			}

			holder.icon.setImageDrawable(app.getIcon());
			holder.appName.setText(app.getAppName());
			if (app.isRom()) {
				holder.location.setText("手机内存");
			} else {
				holder.location.setText("外部内存");
			}
			return convertView;
		}
	}

	static class ViewHolder {
		public ImageView icon;
		public TextView appName;
		public TextView location;
	}

	@Override
	protected void onDestroy() {
		// 防止关闭activity时报错
		dismissPopwindow();
		super.onDestroy();
	}

	/*
	 * popwindow条目的点击事件
	 * 
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_start:
			System.out.println("开启应用");
			startApplication();
			break;
		case R.id.ll_uninstall:
			System.out.println("卸载应用");
			uninstallApplication();
			break;
		case R.id.ll_share:
			System.out.println("分享应用");
			shareApplication();
			break;
		}
	}
	
	/**
	 * 开启一个应用:查询该应用的入口activity然后启动该activity
	 */
	private void startApplication(){
		PackageManager pm = getPackageManager();
		//获取全部具有启动属性的activity
//		Intent intent = new Intent();
//		intent.setAction("android.intent.action.MAIN");
//		intent.addCategory("android.intent.category.LAUNCHER");
//		List<ResolveInfo> intentActivities = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
//		String packageName = intentActivities.get(0).activityInfo.packageName;
		
		//获取指定包名的启动activity
		String packageName = appInfo.getPackageName();
		System.out.println("点击的包名："+packageName);
		Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
		if (launchIntent!=null) {
			startActivity(launchIntent);
		}else{
			Toast.makeText(getApplicationContext(), "不能启动当前应用", 1).show();
		}
	}
	
	/**
	 * 卸载一个应用
	 */
	private void uninstallApplication(){
//		<intent-filter>
//        <action android:name="android.intent.action.VIEW" />
//        <action android:name="android.intent.action.DELETE" />
//        <category android:name="android.intent.category.DEFAULT" />
//        <data android:scheme="package" />
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:"+appInfo.getPackageName()));
		startActivityForResult(intent, 0);
	}
	
	/**
	 * 分享一个应用shareApplication
	 */
	private void shareApplication(){
//		Intent { act=android.intent.action.SEND typ=text/plain flg=0x3000000 cmp=com.android.mms/.ui.ComposeMessageActivity (has extras) } from pid 256?
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "推荐你使用："+appInfo.getAppName());
		startActivity(intent);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//刷新
		getAppInfo();
		super.onActivityResult(requestCode, resultCode, data);
	}

}
