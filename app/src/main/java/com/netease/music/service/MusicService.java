package com.netease.music.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.netease.lib_audio.app.AudioHelper;
import com.netease.lib_audio.mediaplayer.core.AudioController;
import com.netease.lib_audio.mediaplayer.events.AudioFavouriteEvent;
import com.netease.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.netease.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.netease.lib_audio.mediaplayer.events.AudioReleaseEvent;
import com.netease.lib_audio.mediaplayer.events.AudioStartEvent;
import com.netease.music.ui.page.audio.NotificationHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;



public class MusicService extends Service implements NotificationHelper.NotificationHelperListener {


	//actions
	private static String ACTION_START = "ACTION_START";

	private NotificationReceiver mReceiver;

	public static void startMusicService() {
		Intent intent = new Intent(AudioHelper.getContext(), MusicService.class);
		intent.setAction(ACTION_START);
		AudioHelper.getContext().startService(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		EventBus.getDefault().register(this);
		registerBroadcastReceiver();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			if (ACTION_START.equals(intent.getAction())) {
				//初始化前台Notification
				NotificationHelper.getInstance().init(this);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		unRegisterBroadcastReceiver();
	}

	private void registerBroadcastReceiver() {
		if (mReceiver == null) {
			mReceiver = new NotificationReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(NotificationReceiver.ACTION_STATUS_BAR);
			registerReceiver(mReceiver, filter);
		}
	}

	private void unRegisterBroadcastReceiver() {
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onAudioLoadEvent(AudioLoadEvent event) {
		//更新notifacation为load状态
		NotificationHelper.getInstance().showLoadStatus(event.mAudioBean);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onAudioPauseEvent(AudioPauseEvent event) {
		//更新notifacation为暂停状态
		NotificationHelper.getInstance().showPauseStatus();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onAudioStartEvent(AudioStartEvent event) {
		//更新notifacation为播放状态
		NotificationHelper.getInstance().showPlayStatus();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onAudioFavouriteEvent(AudioFavouriteEvent event) {
		//更新notifacation收藏状态
		NotificationHelper.getInstance().changeFavouriteStatus(event.isFavourite);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onAudioReleaseEvent(AudioReleaseEvent event) {
		//移除notifacation
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	//绑定Notifiction
	@Override
	public void onNotificationInit() {
		//startForeground(NotificationHelper.NOTIFICATION_ID, NotificationHelper.getInstance().getNotification());
	}


	//通知栏消息接收器
	public static class NotificationReceiver extends BroadcastReceiver {

		public static final String ACTION_STATUS_BAR =
				AudioHelper.getContext().getPackageName() + ".NOTIFICATION_ACTIONS";
		public static final String EXTRA = "extra";
		public static final String EXTRA_PLAY = "play_pause";
		public static final String EXTRA_NEXT = "play_next";
		public static final String EXTRA_PRE = "play_previous";
		public static final String EXTRA_FAV = "play_favourite";
		public static final String EXTRA_CANCLE = "cancle_notification";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null || TextUtils.isEmpty(intent.getAction())) {
				return;
			}
			String extra = intent.getStringExtra(EXTRA);
			if (!TextUtils.isEmpty(extra)) {
				switch (extra) {
					case EXTRA_PLAY:
						//处理播放暂停事件,可以封到AudioController中
						AudioController.getInstance().playOrPause();
						break;
					case EXTRA_PRE:
						AudioController.getInstance().previous(); //不管当前状态，直接播放
						break;
					case EXTRA_NEXT:
						AudioController.getInstance().next();
						break;
					case EXTRA_FAV:
						//AudioController.getInstance().changeFavourite();
						break;
					case EXTRA_CANCLE:
						NotificationHelper.getInstance().getNotificationManager().cancel(NotificationHelper.NOTIFICATION_ID);
					default:
						break;
				}
			}

		}
	}
}
