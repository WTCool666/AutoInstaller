package com.example.autoinstaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.StaticLayout;
import android.util.Log;

public class AutoInstaller {
	private Context context;
	private File file;
	private static String TAG="wangtao";
	public AutoInstaller(Context context, File file) {
		this.context = context;
		this.file = file;
	}
	
	public void install(){
		new Thread(){
			@Override
			public void run() {
				boolean isSuccess=installByRoot();
				if (!isSuccess) {
					Log.e(TAG, "has not root");
					//智能点击安装
					//判断是否开启服务
					if (isAccessibilitySettingsOn()) {
						Log.e(TAG, "has start service");
						handler.sendEmptyMessage(0);
					}else{
						Intent intent=new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
						context.startActivity(intent);
						Log.e(TAG, "has not start service");
					}
				}
			}

			
		}.start();
	}
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				Uri uri=Uri.fromFile(file);
				Intent intent=new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(uri, "application/vnd.android.package-archive");
				context.startActivity(intent);
				break;

			default:
				break;
			}
		};
	};
	private boolean installByRoot() {
		boolean result=false;
		Process process=null;
		OutputStream outputStream=null;
		BufferedReader bufferedReader=null;
		try {
			process=Runtime.getRuntime().exec("su");
			outputStream=process.getOutputStream();
			String cmd="pm install -r"+file.getAbsolutePath()+"\n";
			outputStream.write(cmd.getBytes());
			outputStream.flush();
			outputStream.write("exit\n".getBytes());
			process.waitFor();
			bufferedReader=new BufferedReader(new InputStreamReader(process.getErrorStream()));
			StringBuilder sb=new StringBuilder();
			String line;
			while((line=bufferedReader.readLine())!=null){
				sb.append(line);
			}
			if (!sb.toString().contains("Failure")) {
				result=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result=false;
		}finally {
			if (outputStream!=null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (bufferedReader!=null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (process!=null) {
				process.destroy();
				outputStream=null;
				bufferedReader=null;
			}
		}
		return result;
	}
	
	public boolean isAccessibilitySettingsOn(){
		int accessibility=0;
		String service=context.getPackageName()+"/"+ApkService.class.getCanonicalName();
		try {
			accessibility=Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
		} catch (Exception e) {
			Log.e(TAG, service);
		}
		if (accessibility==1) {
			String settingValue=Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
			Log.i(TAG, settingValue+" ====");
			String [] arr=settingValue.split(":");
			if (arr!=null&&arr.length>0) {
				for (String name : arr) {
					if (service.equalsIgnoreCase(name)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
