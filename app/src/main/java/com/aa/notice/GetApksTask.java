package com.aa.notice;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;


import com.aa.notice.utils.PayHelperUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取已安装的应用的安装包
 */
public class GetApksTask {

    //获取列表，
  public static ArrayList<AppInfoMessage> getAppMessage(Context context){
      ArrayList<AppInfoMessage> appList = new ArrayList<AppInfoMessage>(); //用来存储获取的应用信息数据

      List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);

      for(int i=0;i<packages.size();i++) {
          PackageInfo packageInfo = packages.get(i);
          AppInfoMessage tmpInfo=new AppInfoMessage();
          tmpInfo.appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
          tmpInfo.packageName = packageInfo.packageName;
          tmpInfo.versionName = packageInfo.versionName;
          tmpInfo.versionCode = packageInfo.versionCode;
//          tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
          //Only display the non-system app info
          if((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0)
          {
              appList.add(tmpInfo);//如果非系统应用，则添加至appList
          }

      }
      return appList;
  }


  public static String getVersionDingDing(Context context){
      ArrayList<AppInfoMessage> appList=getAppMessage(context);
      String tempVersionali="";
      for (int i=0;i<appList.size();i++){
          AppInfoMessage tmpInfo=appList.get(i);
          if(tmpInfo.getPackageName().equals("com.eg.android.AlipayGphone")){
              System.out.println("当前版本是："+tmpInfo.getVersionName());
              PayHelperUtils.sendmsg(context,"当前版本是："+tmpInfo.getVersionName());
              tempVersionali=tmpInfo.getVersionName();
              break;
          }
      }
      return tempVersionali;

  }
}

