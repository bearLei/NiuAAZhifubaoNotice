package com.aa.notice.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.aa.notice.CustomApplcation;
import com.aa.notice.MainActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Base64;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class PayHelperUtils {
    //渠道
	public static String VERSION2 = "v3_673";

	public static String VERSIONV1="3";
	public static String VERSIONV2=VERSION2+"1.0.3";
	public static String VERSIONV0="v20181101";
	public static String VERSIONVKEY="8D2swf55wk45895y8QnajpYH7h4Q2BK";

	public static String WECHATSTART_ACTION = "com.notice.wechat.start";
	public static String ALIPAYSTART_ACTION = "com.notice.alipay.start";
	public static String QQSTART_ACTION = "com.notice.qq.start";
	public static String MSGRECEIVED_ACTION = "com.tools.notice.msgreceived";
	public static String TRADENORECEIVED_ACTION = "com.tools.notice.tradenoreceived";
	public static String LOGINIDRECEIVED_ACTION = "com.tools.notice.loginidreceived";
	public static String UPDATEBALANCE_ACTION = "com.tools.notice.updatebalance";
	public static String GETTRADEINFO_ACTION = "com.tools.notice.gettradeinfo";
	public static List<QrCodeBean> qrCodeBeans = new ArrayList<QrCodeBean>();
	public static List<OrderBean> orderBeans = new ArrayList<OrderBean>();
	public static boolean isFirst=true;

	/*
	 * 启动一个app
	 */
	public static void startAPP() {
		try {
			Intent intent = new Intent(CustomApplcation.getInstance().getApplicationContext(), MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			CustomApplcation.getInstance().getApplicationContext().startActivity(intent);
		} catch (Exception e) {
		}
	}

	/**
	 * 将图片转换成Base64编码的字符串
	 * 
	 * @param path
	 * @return base64编码的字符串
	 */
	public static String imageToBase64(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		InputStream is = null;
		byte[] data = null;
		String result = null;
		try {
			is = new FileInputStream(path);
			// 创建一个字符流大小的数组。
			data = new byte[is.available()];
			// 写入数组
			is.read(data);
			// 用默认的编码格式进行编码
			result = Base64.encodeToString(data, Base64.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		result = "\"data:image/gif;base64," + result + "\"";
		return result;
	}

	public static void sendAppMsg(String money, String mark, String type, Context context) {
		Intent broadCastIntent = new Intent();
		if (type.equals("alipay")) {
			broadCastIntent.setAction(ALIPAYSTART_ACTION);
		} else if (type.equals("wechat")) {
			broadCastIntent.setAction(WECHATSTART_ACTION);
		} else if (type.equals("qq")) {
			broadCastIntent.setAction(QQSTART_ACTION);
		}else if (type.equals("unionpay")){
			broadCastIntent.setAction("com.aa.union.start");
		}
		broadCastIntent.putExtra("mark", mark);
		broadCastIntent.putExtra("money", money);
		context.sendBroadcast(broadCastIntent);
	}

	/*
	 * 将时间戳转换为时间
	 */
	public static String stampToDate(String s) {
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long lt = new Long(s);
		Date date = new Date(lt * 1000);
		res = simpleDateFormat.format(date);
		return res;
	}

	/**
	 * 方法描述：判断某一应用是否正在运行
	 * 
	 * @param context
	 *            上下文
	 * @param packageName
	 *            应用的包名
	 * @return true 表示正在运行，false表示没有运行
	 */
	public static boolean isAppRunning(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
		if (list.size() <= 0) {
			return false;
		}
		for (ActivityManager.RunningTaskInfo info : list) {
			if (info.baseActivity.getPackageName().equals(packageName)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * 启动一个app
	 */
	public static void startAPP(Context context, String appPackageName) {
		try {
			Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
			context.startActivity(intent);
		} catch (Exception e) {
			sendmsg(context, "startAPP异常" + e.getMessage());
		}
	}

	//启动银联获取二维码服务
	public static void startService(Context context){
		Intent unionIntent = new Intent();
		unionIntent.setAction("com.aa.union.start");
		context.sendBroadcast(unionIntent);
	}

	public static void notify(final Context context, String type, final String no, String money, String mark,
			String dt) {
		String _notifyapi = null;
		String url = AbSharedUtil.getString(context,"notifyurl");
		_notifyapi = url;

		//截取Mark获得通知地址
		if (mark.contains("brt")){
			_notifyapi = url.replaceAll("[a-z]+\\d+",mark.substring(0,6));
			mark = mark.substring(6,mark.length()-1);
		}
		final String notifyurl=_notifyapi;
		String signkey = AbSharedUtil.getString(context, "signkey");
		sendmsg(context, "订单" + no + "重试发送异步通知("+VERSIONV2+")...");
		if (TextUtils.isEmpty(notifyurl) || TextUtils.isEmpty(signkey)) {
			sendmsg(context, "发送异步通知("+VERSIONV2+")异常，异步通知地址或密钥为空");
			update(no, "异步通知地址("+VERSIONV2+")或密钥为空");
			return;
		}
		//signkey=signkey+VERSIONVKEY;

		String account="";
		String balance=AbSharedUtil.getString(context, type+"balance");
		if(type.equals("alipay")){
			account=AbSharedUtil.getString(context, "alipay");
		}else if(type.equals("wechat")){
			account=AbSharedUtil.getString(context, "wechat");
		}else if(type.equals("qq")){
			account=AbSharedUtil.getString(context, "qq");
		}
		
		HttpUtils httpUtils = new HttpUtils(30000);

		String sign = MD5.md5(dt + mark + money + no + type + signkey+AbSharedUtil.getString(context, "userids")+VERSIONV0);
		RequestParams params = new RequestParams();
		params.addBodyParameter("type", type);
		params.addBodyParameter("no", no);
		params.addBodyParameter("version", VERSIONV0);
		params.addBodyParameter("userids", AbSharedUtil.getString(context, "userids"));


		params.addBodyParameter("money", money);
		params.addBodyParameter("mark", mark);
		params.addBodyParameter("dt", dt);
		params.addBodyParameter("balance", balance);
		if (!TextUtils.isEmpty(account)) {
			params.addBodyParameter("account", account);
		}
		if (type.equals("alipay")){
			params.addBodyParameter("is_customization","1");

		}
		params.addBodyParameter("sign", sign);
		
		sendmsg(context, "发送"+dt + mark + money + no + type + signkey+AbSharedUtil.getString(context, "userids")+VERSIONV0+"异步通知("+notifyurl+")，密钥是" + signkey);
		httpUtils.send(HttpMethod.POST, notifyurl, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				sendmsg(context, "发送异步通知("+notifyurl+")异常，服务器异常" + arg1);
				update(no, arg1);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				String result = arg0.result;
				if (result.contains("success")) {
					sendmsg(context, "发送异步通知("+notifyurl+")成功，服务器返回" + result);
				} else {
					sendmsg(context, "发送异步通知("+notifyurl+")失败，服务器返回" + result);
				}
				update(no, result);
			}
		});
	}

	private static void update(String no, String result) {
		DBManager dbManager = new DBManager(CustomApplcation.getInstance().getApplicationContext());
		dbManager.updateOrder(no, result);
	}

	public static String getCookieStr(ClassLoader appClassLoader) {
		String cookieStr = "";
		// 获得cookieStr
		XposedHelpers.callStaticMethod(XposedHelpers.findClass(
				"com.alipay.mobile.common.transportext.biz.appevent.AmnetUserInfo", appClassLoader), "getSessionid");
		Context context = (Context) XposedHelpers.callStaticMethod(XposedHelpers.findClass(
				"com.alipay.mobile.common.transportext.biz.shared.ExtTransportEnv", appClassLoader), "getAppContext");
		if (context != null) {
			Object readSettingServerUrl = XposedHelpers.callStaticMethod(
					XposedHelpers.findClass("com.alipay.mobile.common.helper.ReadSettingServerUrl", appClassLoader),
					"getInstance");
			if (readSettingServerUrl != null) {
				// String gWFURL = (String)
				// XposedHelpers.callMethod(readSettingServerUrl, "getGWFURL",
				// context);
				String gWFURL = ".alipay.com";
				cookieStr = (String) XposedHelpers.callStaticMethod(XposedHelpers
						.findClass("com.alipay.mobile.common.transport.http.GwCookieCacheHelper", appClassLoader),
						"getCookie", gWFURL);
			} else {
				sendmsg(context, "异常readSettingServerUrl为空");
			}
		} else {
			sendmsg(context, "异常context为空");
		}
		return cookieStr;
	}

	public static void sendTradeInfo(Context context) {
		Intent broadCastIntent = new Intent();
		broadCastIntent.setAction(GETTRADEINFO_ACTION);
		context.sendBroadcast(broadCastIntent);
	}
	
	public static void  getTradeInfo(final Context context,final String cookie) {
		sendmsg(context, "有新的商家服务订单进来！！！");
		String url="https://mbillexprod.alipay.com/enterprise/walletTradeList.json?lastTradeNo=&lastDate=&pageSize=1&shopId=&_input_charset=utf-8&ctoken==&_ksTS=&_callback=&t="+System.currentTimeMillis();
		HttpUtils httpUtils = new HttpUtils(30000);
		httpUtils.configResponseTextCharset("GBK");
		RequestParams params = new RequestParams();
		params.addHeader("Cookie", cookie);
		params.addHeader("Referer", "https://render.alipay.com/p/z/merchant-mgnt/simple-order.html");
		params.addHeader("User-Agent", "Mozilla/5.0 (Linux; U; Android 7.1.1; zh-cn; Redmi Note 3 Build/LRX22G) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.100 U3/0.8.0 Mobile Safari/534.30 Nebula AlipayDefined(nt:WIFI,ws:360|640|3.0) AliApp(AP/10.1.22.835) AlipayClient/10.1.22.835 Language/zh-Hans useStatusBar/true");
		httpUtils.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
//				sendmsg(context, "服务器异常" + arg1);
				sendmsg(context, "请求支付宝API失败，出现掉单，5秒后启动补单");
				sendTradeInfo(context);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				String result = arg0.result.replace("/**/(", "").replace("})", "}");
				try {
					JSONObject jsonObject = new JSONObject(result);
					if(jsonObject.has("status")){
						String status=jsonObject.getString("status");
						if(!status.equals("deny")){
							JSONObject res = jsonObject.getJSONObject("result");
							JSONArray jsonArray = res.getJSONArray("list");
							if (jsonArray != null && jsonArray.length() > 0) {
								JSONObject object = jsonArray.getJSONObject(0);
								String tradeNo = object.getString("tradeNo");
								Intent broadCastIntent = new Intent();
								broadCastIntent.putExtra("tradeno", tradeNo);
								broadCastIntent.putExtra("cookie", cookie);
								broadCastIntent.setAction(TRADENORECEIVED_ACTION);
								context.sendBroadcast(broadCastIntent);
							}
						}else{
							sendmsg(context, "getTradeInfo=>>支付宝cookie失效，出现掉单，5秒后启动补单");
							sendTradeInfo(context);
						}
					}
				} catch (Exception e) {
					sendmsg(context,e.getMessage());
					XposedBridge.log("=====>"+arg0);
					sendmsg(context, "getTradeInfo出现异常=>>"+result);
					sendmsg(context, "出现掉单，5秒后启动补单");
					sendTradeInfo(context);
				}
			}
		});
	}
	public static void getTradeInfo2(final Context context) {
		sendmsg(context, "开始补单！！！");
		final DBManager dbManager=new DBManager(context);
		final String cookie=getAlipayCookie(context);
		String url="https://mbillexprod.alipay.com/enterprise/walletTradeList.json?lastTradeNo=&lastDate=&pageSize=50&shopId=&_input_charset=utf-8&ctoken==&_ksTS=&_callback=&t="+System.currentTimeMillis();
		HttpUtils httpUtils = new HttpUtils(30000);
		httpUtils.configResponseTextCharset("GBK");
		RequestParams params = new RequestParams();
		params.addHeader("Cookie", cookie);
		params.addHeader("Referer", "https://render.alipay.com/p/z/merchant-mgnt/simple-order.html");
		params.addHeader("User-Agent", "Mozilla/5.0 (Linux; U; Android 7.1.1; zh-cn; Redmi Note 3 Build/LRX22G) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.100 U3/0.8.0 Mobile Safari/534.30 Nebula AlipayDefined(nt:WIFI,ws:360|640|3.0) AliApp(AP/10.1.22.835) AlipayClient/10.1.22.835 Language/zh-Hans useStatusBar/true");
		httpUtils.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				sendmsg(context, "服务器异常" + arg1);
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				String result = arg0.result.replace("/**/(", "").replace("})", "}");
				try {
					JSONObject jsonObject = new JSONObject(result);
					if(jsonObject.has("status")){
						String status=jsonObject.getString("status");
						if(!status.equals("deny")){
							JSONObject res = jsonObject.getJSONObject("result");
							JSONArray jsonArray = res.getJSONArray("list");
							if (jsonArray != null && jsonArray.length() > 0) {
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject object = jsonArray.getJSONObject(i);
									String tradeNo = object.getString("tradeNo");
									if(!dbManager.isExistTradeNo(tradeNo)){
										sendmsg(context, "补单:::请求完成获取到流水号,订单未处理，发送广播："+tradeNo);
										Intent broadCastIntent = new Intent();
										broadCastIntent.putExtra("tradeno", tradeNo);
										broadCastIntent.putExtra("cookie", cookie);
										broadCastIntent.setAction(TRADENORECEIVED_ACTION);
										context.sendBroadcast(broadCastIntent);
									}
								}
							}
						}else{
							sendmsg(context, "getTradeInfo2=>>补单失败，cookie失效");
						}
					}
				} catch (Exception e) {
					sendmsg(context, "getTradeInfo异常2=>>补单失败"+arg0);
					XposedBridge.log("----arg0-----:"+arg0);
				}
			}
		});
	}

	public static String getCurrentDate() {
		long l = System.currentTimeMillis();
		Date date = new Date(l);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String d = dateFormat.format(date);
		return d;
	}

	public static void sendmsg(Context context, String msg) {
		Intent broadCastIntent = new Intent();
		broadCastIntent.putExtra("msg", msg);
		broadCastIntent.setAction(MSGRECEIVED_ACTION);
		context.sendBroadcast(broadCastIntent);
	}

	/**
	 * 获取当前本地apk的版本
	 *
	 * @param mContext
	 * @return
	 */
	public static int getVersionCode(Context mContext) {
		int versionCode = 0;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			sendmsg(mContext, "getVersionCode异常" + e.getMessage());
		}
		return versionCode;
	}

	/**
	 * 获取版本号名称
	 *
	 * @param context
	 *            上下文
	 * @return
	 */
	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			sendmsg(context, "getVerName异常" + e.getMessage());
		}
		return verName;
	}

	public static boolean isreg(Activity activity, String name) {
		Intent intent = new Intent();
		intent.setAction(name);
		PackageManager pm = activity.getPackageManager();
		List<ResolveInfo> resolveInfos = pm.queryBroadcastReceivers(intent, 0);
		if (resolveInfos != null && !resolveInfos.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * 判断某activity是否处于栈顶
	 * 
	 * @return true在栈顶 false不在栈顶
	 */
	public static int isActivityTop(Context context) {
		try {
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> infos = manager.getRunningTasks(100);
			for (RunningTaskInfo runningTaskInfo : infos) {
				if (runningTaskInfo.topActivity.getClassName()
						.equals("cooperation.qwallet.plugin.QWalletPluginProxyActivity")) {
					return runningTaskInfo.numActivities;
				}
			}
			return 0;
		} catch (SecurityException e) {
			sendmsg(context, e.getMessage());
			return 0;
		}
	}

	public static String getAlipayLoginId(ClassLoader classLoader) {
		String loginId="";
		try {
			Class<?> AlipayApplication = XposedHelpers.findClass("com.alipay.mobile.framework.AlipayApplication",
					classLoader);
			Class<?> SocialSdkContactService = XposedHelpers
					.findClass("com.alipay.mobile.personalbase.service.SocialSdkContactService", classLoader);
			Object instace = XposedHelpers.callStaticMethod(AlipayApplication, "getInstance");
			Object MicroApplicationContext = XposedHelpers.callMethod(instace, "getMicroApplicationContext");
			Object service = XposedHelpers.callMethod(MicroApplicationContext, "findServiceByInterface",
					SocialSdkContactService.getName());
			Object MyAccountInfoModel = XposedHelpers.callMethod(service, "getMyAccountInfoModelByLocal");
//			String userId = XposedHelpers.getObjectField(MyAccountInfoModel, "userId").toString();
			loginId = XposedHelpers.getObjectField(MyAccountInfoModel, "loginId").toString();
		} catch (Exception e) {
		}
		return loginId;
	}
	public static String getWechatLoginId(Context context) {
		String loginId="";
		try {
			SharedPreferences sharedPreferences=context.getSharedPreferences("com.tencent.mm_preferences", 0);
			loginId = sharedPreferences.getString("login_user_name", "");
		} catch (Exception e) {
			PayHelperUtils.sendmsg(context, e.getMessage());
		}
		return loginId;
	}
	public static String getQQLoginId(Context context) {
		String loginId="";
		try {
			SharedPreferences sharedPreferences=context.getSharedPreferences("Last_Login", 0);
			loginId = sharedPreferences.getString("uin", "");
		} catch (Exception e) {
			PayHelperUtils.sendmsg(context, e.getMessage());
		}
		return loginId;
	}
	
	public static void sendLoginId(String loginId, String type, Context context) {
		Intent broadCastIntent = new Intent();
		broadCastIntent.setAction(LOGINIDRECEIVED_ACTION);
		broadCastIntent.putExtra("type", type);
		broadCastIntent.putExtra("loginid", loginId);
		context.sendBroadcast(broadCastIntent);
	}
	
	public static void updateAlipayCookie(Context context,String cookie){
		DBManager dbManager=new DBManager(context);
		if(dbManager.getConfig("cookie").equals("null")){
			dbManager.addConfig("cookie", cookie);
		}else{
			dbManager.updateConfig("cookie", cookie);
		}
	}
	
	public static String getAlipayCookie(Context context){
		DBManager dbManager=new DBManager(context);
		String cookie=dbManager.getConfig("cookie");
		return cookie;
	}

	public static void startAlipayMonitor(final Context context){

		try {
			Timer timer=new Timer();
			TimerTask timerTask=new TimerTask() {
				@Override
				public void run() {
					Calendar cc = Calendar.getInstance();//
					int nowmi = cc.get(Calendar.MINUTE);
					int nowhour = cc.get(Calendar.HOUR_OF_DAY);			
					int nowhour12 = cc.get(Calendar.HOUR);
					if (nowhour12>9){
						nowhour12=nowhour12-9;
					}
					//String nowmistr = String.valueOf(nowmi);
					String nowminute=AbSharedUtil.getString(context, "nowminute");
					int triggerTime = AbSharedUtil.getInt(context,"triggerTime") > 0 ? AbSharedUtil.getInt(context,"triggerTime") : 3;
					//if (1==2){
						//if (((nowhour>=8)&&((nowmi%10)==nowhour12)&&(nowminute.equals("0")))||((nowhour<8)&&(nowmi==nowhour12)&&(nowminute.equals("0")))){
//						if (((nowhour>=0)&&((nowmi%10)==nowhour12)&&(nowminute.equals("0")))||((nowhour<0)&&(nowmi==nowhour12)&&(nowminute.equals("0")))){
						if (nowmi%triggerTime == 0 && nowminute.equals("0")){
							AbSharedUtil.putString(context, "nowminute", "1");	

							sendmsg(context, "每间隔"+triggerTime+"分钟轮询获取订单数据...");


//							sendmsg(context, "轮询获取订单数据...");
							final DBManager dbManager=new DBManager(context);
							dbManager.saveOrUpdateConfig("time", System.currentTimeMillis()/1000+"");
							final String cookie=PayHelperUtils.getAlipayCookie(context);
							if(TextUtils.isEmpty(cookie) || cookie.equals("null")){
								return;
							}
							long current = System.currentTimeMillis();
							long s = current - 864000000;
							String c = getCurrentDate();
							String url = "https://mbillexprod.alipay.com/enterprise/simpleTradeOrderQuery.json?beginTime=" + s
									+ "&limitTime=" + current + "&pageSize=50&pageNum=1&channelType=ALL";
							HttpUtils httpUtils = new HttpUtils(30000);
							httpUtils.configResponseTextCharset("GBK");
							RequestParams params = new RequestParams();
							params.addHeader("Cookie", cookie);
							params.addHeader("Referer", "https://render.alipay.com/p/z/merchant-mgnt/simple-order.html?beginTime=" + c
									+ "&endTime=" + c + "&fromBill=true&channelType=ALL");
							httpUtils.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {

								@Override
								public void onFailure(HttpException arg0, String arg1) {
									sendmsg(context, "服务器异常" + arg1);
									sendmsg(context, "数据异常");
								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									try {
										String result = arg0.result;
										JSONObject jsonObject = new JSONObject(result);
										if(jsonObject.has("status")){
											String status=jsonObject.getString("status");
											if(!status.equals("deny")){
												JSONObject res = jsonObject.getJSONObject("result");
												JSONArray jsonArray = res.getJSONArray("list");
												if (jsonArray != null && jsonArray.length() > 0) {
													 for (int i = 0; i < jsonArray.length(); i++) {
														 JSONObject object=jsonArray.getJSONObject(i);
														 String tradeNo=object.getString("tradeNo");
														 //sendmsg(context, "tradeNo is "+tradeNo);
														 if(!dbManager.isExistTradeNo(tradeNo)){
															 //if(isFirst){
															//	 dbManager.addTradeNo(tradeNo,"1");
															 //}else{
																 if(!dbManager.isNotifyTradeNo(tradeNo)){
																	 Intent broadCastIntent = new Intent();
																	 sendmsg(context, "TRADENORECEIVED_ACTION tradeNo is "+tradeNo);
																	 broadCastIntent.putExtra("tradeno", tradeNo);
																	 broadCastIntent.putExtra("cookie", cookie);
																	 broadCastIntent.setAction(TRADENORECEIVED_ACTION);
																	 context.sendBroadcast(broadCastIntent);
																 }else{
																	 //sendmsg(context, "该订单已Notify过了。tradeNo is "+tradeNo);
																	 
																 }
															 //}
														 }else{
															 //sendmsg(context, "该订单已处理过了。tradeNo is "+tradeNo);
														 }
													 }
													 isFirst=false;
												}
											}
										}
									}catch (Exception e) {
										sendmsg(context, "startAlipayMonitor->>"+e.getMessage());
									}
									isFirst=false;
								}
							});
							
						}else{
							//if (((nowhour>=8)&&((nowmi%10)==nowhour12))||((nowhour<8)&&(nowmi==nowhour12))){
//							if ((nowhour>=0)&&((nowmi%10)==nowhour12)){
							if (nowmi%-triggerTime == 0){
							}else{
								AbSharedUtil.putString(context, "nowminute", "0");
							}
							
						}
						
					//}
					

				}
			};
			int _triggerTime=10;
			timer.schedule(timerTask, 0, _triggerTime*1000);
		} catch (Exception e) {
			sendmsg(context, "startAlipayMonitor->>"+e.getMessage());
		}
	}	
	public static void startAlipayMonitor_old(final Context context){
		try {
			Timer timer=new Timer();
			TimerTask timerTask=new TimerTask() {
				@Override
				public void run() {
//					sendmsg(context, "轮询获取订单数据...");
					final DBManager dbManager=new DBManager(context);
					dbManager.saveOrUpdateConfig("time", System.currentTimeMillis()/1000+"");
				}
			};
			int triggerTime=10;
			timer.schedule(timerTask, 0, triggerTime*1000);
		} catch (Exception e) {
			sendmsg(context, "startAlipayMonitor->>"+e.getMessage());
		}
	}
	
	public static String getcurrentTimeMillis(Context context){
		DBManager dbManager=new DBManager(context);
		return dbManager.getConfig("time");
	}
	
	public static void sendBalance(String type, String balance, Context context) {
		Intent broadCastIntent = new Intent();
		broadCastIntent.setAction(UPDATEBALANCE_ACTION);
		broadCastIntent.putExtra("type", type);
		broadCastIntent.putExtra("balance", balance);
		context.sendBroadcast(broadCastIntent);
	}
	
	public static String getBalance(String type, Context context) {
		String balance=AbSharedUtil.getString(context, type+"balance");
		return balance;
	}
	
	public static double getWechatBalance(ClassLoader classLoader) {
		//获取余额操作
		double balance=0.0;
		Class<?> p=XposedHelpers.findClass("com.tencent.mm.plugin.wallet.a.p", classLoader);
		XposedHelpers.callStaticMethod(p, "bNp");
		Object ag=XposedHelpers.callStaticMethod(p, "bNq");
		Object paw=XposedHelpers.getObjectField(ag, "paw");
		if(paw!=null){
			balance=(Double) XposedHelpers.getObjectField(paw, "plV");
		}
		return balance;
	}
	
	public static String getOrderId() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String newDate=sdf.format(new Date());
        String result="";
        Random random=new Random();
        for(int i=0;i<3;i++){
            result+=random.nextInt(10);
        }
        return newDate+result;
    }
	
	
}
