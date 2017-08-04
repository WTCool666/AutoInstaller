package com.example.autoinstaller;

import java.util.HashMap;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class ApkService extends AccessibilityService {

	HashMap<Integer, Boolean> hashMap=new HashMap<>();
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		AccessibilityNodeInfo nodeInfo=event.getSource();
		if (nodeInfo!=null) {
			if (hashMap.get(event.getWindowId())==null) {
				boolean handler=interNodeInfo(nodeInfo);
				if (handler) {
					hashMap.put(event.getWindowId(), true);
				}
			}
		}
	}

	/**
	 * 模拟安装点击
	 * @param nodeInfo
	 * @return
	 */
	private boolean interNodeInfo(AccessibilityNodeInfo nodeInfo) {
		int childCount=nodeInfo.getChildCount();
		if (nodeInfo.getClassName().equals("android.widget.Button")) {
			String nodeContent=nodeInfo.getText().toString();
			if (nodeContent.equals("安装")||nodeContent.equals("Done")||nodeContent.equals("完成")||nodeContent.equals("确定")||nodeContent.equals("Install")) {
				nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				return true;
			}
		}else if (nodeInfo.getClassName().equals("android.widget.ScrollView")) {
			nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
		}
		for(int i=0;i<childCount;i++){
			AccessibilityNodeInfo nodeInfo2=nodeInfo.getChild(i);
			if (interNodeInfo(nodeInfo2)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub

	}

}
