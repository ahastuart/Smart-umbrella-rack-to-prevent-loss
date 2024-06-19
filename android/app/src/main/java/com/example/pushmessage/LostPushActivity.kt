package com.example.pushmessage

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.firebase.database.*
import java.util.*

class LostPushActivity : AppCompatActivity() {

    private lateinit var umbrellaStandRef: DatabaseReference // Firebase Database 참조
    private var isConditionMet: Boolean = false // 조건이 충족되었는지 여부를 추적
    private var timer: Timer? = null // 10초 타이머

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // 동일한 레이아웃 파일 사용

        // Firebase Database 초기화
        val database = FirebaseDatabase.getInstance()
        umbrellaStandRef = database.getReference("umbrella_stand")

        // umbrella_stand 경로의 데이터 변화를 모니터링
        umbrellaStandRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // distance와 status 값 읽기
                    val distance = dataSnapshot.child("distance").getValue(Double::class.java)
                    val status = dataSnapshot.child("status").getValue(String::class.java)

                    // 조건 확인: 잠금 해제 상태이고 거리가 10cm 이하인지
                    if (status == "unlocked" && distance != null && distance <= 10) {
                        if (!isConditionMet) {
                            isConditionMet = true // 조건이 충족됨을 표시
                            startTimer() // 타이머 시작
                        }
                    } else {
                        isConditionMet = false // 조건이 충족되지 않음
                        stopTimer() // 타이머 중지
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LostPushActivity, "Failed to read data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startTimer() {
        timer = Timer() // 새 타이머 생성
        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (isConditionMet) {
                    runOnUiThread { sendNotification() } // 10초 후 조건이 유지되면 알림 전송
                }
            }
        }, 10000) // 10초 후에 실행
    }

    private fun stopTimer() {
        timer?.cancel() // 타이머 중지
        timer = null
    }

    private fun sendNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "umbrella_stand_channel"

        // 안드로이드 O 이상에서는 Notification Channel을 생성해야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Umbrella Stand Alerts", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 빌더 설정
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // 알림 아이콘 설정
            .setContentTitle("Alert") // 알림 제목 설정
            .setContentText("우산을 두고갔습니다!") // 알림 내용 설정
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 알림 우선순위 설정
            .setAutoCancel(true) // 알림 클릭 시 자동으로 사라지도록 설정

        // 알림 전송
        notificationManager.notify(0, builder.build())
    }
}
