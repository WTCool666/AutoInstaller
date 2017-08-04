package com.example.autoinstaller;

import java.io.File;
import java.lang.reflect.Field;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void btn_install(View view){
		File file=new File(Environment.getExternalStorageDirectory(), "CusViewDemo.apk");
		AutoInstaller autoInstaller=new AutoInstaller(this, file);
		autoInstaller.install();
	}
}
