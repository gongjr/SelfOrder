package kxlive.gjrlibrary.utils;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import kxlive.gjrlibrary.R;
import kxlive.gjrlibrary.config.Constants;

public class DownloadService extends IntentService {
	private static final int BUFFER_SIZE = 10 * 1024; // 8k ~ 32K
	private static final String TAG = "DownloadService";
	private NotificationManager mNotifyManager;
	private Builder mBuilder;

	public DownloadService() {
		super("DownloadService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent ");
		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(this);

		String appName = getString(getApplicationInfo().labelRes);
		int icon = getApplicationInfo().icon;

		mBuilder.setContentTitle(appName).setSmallIcon(icon);
		String urlStr = intent.getStringExtra(Constants.APK_DOWNLOAD_URL);
		Log.d(TAG, "urlStr: " + urlStr);

		InputStream in=null;
		FileOutputStream out = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(false);
			urlConnection.setConnectTimeout(10 * 1000);
			urlConnection.setReadTimeout(10 * 1000);
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("Charset", "UTF-8");
			urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

			urlConnection.connect();
			long bytetotal = urlConnection.getContentLength();
			long bytesum = 0;
			int byteread = 0;
			in = urlConnection.getInputStream();
			File dir = StorageUtils.getCacheDirectory(this);
			String apkName = urlStr.substring(urlStr.lastIndexOf("/")+1, urlStr.length());
			File apkFile = new File(dir, apkName);
			out = new FileOutputStream(apkFile);
			byte[] buffer = new byte[BUFFER_SIZE];

			int oldProgress = 0;

			while ((byteread = in.read(buffer)) != -1) {
				bytesum += byteread;
				out.write(buffer, 0, byteread);

				int progress = (int) (bytesum * 100L / bytetotal);
				if (progress != oldProgress) {
					updateProgress(progress);
				}
				oldProgress = progress;
			}
			mBuilder.setContentText(getString(R.string.download_success)).setProgress(0, 0, false);

			Intent installAPKIntent = new Intent(Intent.ACTION_VIEW);
			String[] command = {"chmod","777",apkFile.toString()};
			ProcessBuilder builder = new ProcessBuilder(command);
			builder.start();

			installAPKIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
			//installAPKIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//installAPKIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			//installAPKIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, installAPKIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			mBuilder.setContentIntent(pendingIntent);
			Notification noti = mBuilder.build();
			noti.flags = Notification.FLAG_AUTO_CANCEL;
			mNotifyManager.notify(0, noti);

			installAPK(apkFile);
			
            Toast.makeText(getApplicationContext(), "下载完成", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Log.e(TAG, "download apk file error", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void updateProgress(int progress) {
		Log.d(TAG, "progress2: " + progress);
		mBuilder.setContentText(this.getString(R.string.download_progress, progress)).setProgress(100, progress, false);
		PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContentIntent(pendingintent);
		mNotifyManager.notify(0, mBuilder.build());
	}

	private void installAPK(File apkFile) {
		//File apkfile = new File(apkFile);
		if (!apkFile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
//				"application/vnd.android.package-archive");
		
		i.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
		startActivity(i);
	}
	
//	private void deleteAPKFile(final String apkFile) {
//		// 删除文件
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
////				String apkFile = saveFileName;
//				File file = new File(apkFile);
//				if (file.exists()) {
//					final File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
//					file.renameTo(to);
//					to.delete();
////					file.delete();
//				}
//				File fileDir = new File(savePath);
//				if (fileDir.exists()) {
//					final File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
//					fileDir.renameTo(to);
//					to.delete();
////					fileDir.delete();
//				}
//			}
//		}).start();
//	}
}
