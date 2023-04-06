package com.example.mp3servicemessengerpro

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.util.Log

class MyMessengerService : Service() {
    // 음악재생클래스
    lateinit var mediaPlayer: MediaPlayer

    // 메세지전달(송,수신)
    lateinit var receiveMessenger: Messenger
    lateinit var replyMessenger: Messenger

    // 1. 음악객체생성(음악을 준비)

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }
    // 2-2. IBinder 전달
    override fun onBind(intent: Intent): IBinder {
        receiveMessenger = Messenger(IncomingHandler(this))
        return receiveMessenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }
    // 3. 음악객체해제
    override fun onDestroy() {
        super.onDestroy()
        // 음악객체해제
        mediaPlayer.release()
    }

    // 2-1. receiveMessenger Handler
    inner class IncomingHandler(
        context: Context,
        private val applicationContext: Context = context.applicationContext
    ) : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                // 송신자에게 전송할 메신저를 받음, 노래의 총 시간을 보냄, 서비스의 노래를 재생
                10 -> {
                    // 송신자에게 응답을 해줄 메신저를 받음
                    replyMessenger = msg.replyTo
                    // 노래총시간을 보냄
                    if(!mediaPlayer.isPlaying){
                        mediaPlayer = MediaPlayer.create(this@MyMessengerService,R.raw.deep_free)
                    }
                    val message = Message()
                    message.what = 10
                    val bundle = Bundle()
                    bundle.putInt("duration",mediaPlayer.duration)
                    message.obj = bundle
                    replyMessenger.send(message)
                    // 노래재생
                    mediaPlayer.start()
                }
                // 노래를 종료
                20 -> {
                    if(mediaPlayer.isPlaying){
                        mediaPlayer.stop()
                    }
                }
                else -> {
                    Log.e("MyMessengerService", "약속된 프로토콜이 아닙니다. ${msg.what}")
                }
            }
        }
    }
}