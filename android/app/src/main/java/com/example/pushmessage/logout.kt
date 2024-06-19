package com.example.pushmessage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class logout : AppCompatActivity() {

    private lateinit var userName: TextView // 사용자 이름을 표시할 TextView
    private lateinit var logout: Button // 로그아웃 버튼
    private lateinit var auth: FirebaseAuth // FirebaseAuth 객체
    private lateinit var imageButton: Button // 포토 버튼
    private lateinit var dataButton: Button // 데이터 버튼

    override fun onCreate(savedInstanceState: Bundle?) { // 액티비티가 생성될 때 호출되는 메서드
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout) // 레이아웃 설정

        logout = findViewById(R.id.logout) // 로그아웃 버튼과 연결
        userName = findViewById(R.id.userName) // 사용자 이름 텍스트와 연결
        imageButton = findViewById(R.id.imageButton) // 포토 버튼과 연결
        dataButton = findViewById(R.id.dataButton) // 데이터 버튼과 연결

        auth = FirebaseAuth.getInstance() // FirebaseAuth 인스턴스 초기화

        val currentUser = auth.currentUser // 현재 로그인된 사용자 가져오기
        if (currentUser != null) { // 사용자가 존재할 경우
            updateUI(currentUser) // UI 업데이트
        } else {
            startActivity(Intent(this, LoginActivity::class.java)) // 로그인 화면으로 이동
            finish() // 현재 액티비티 종료
        }

        logout.setOnClickListener { // 로그아웃 버튼 클릭 이벤트 설정
            auth.signOut() // FirebaseAuth를 통해 로그아웃 수행
            startActivity(Intent(this, LoginActivity::class.java)) // 로그인 화면으로 이동
            finish() // 현재 액티비티 종료
        }

        imageButton.setOnClickListener { // 포토 버튼 클릭 이벤트 설정
            startActivity(Intent(this, PhotoActivity::class.java)) // 포토 액티비티로 이동
        }

        dataButton.setOnClickListener { // 데이터 버튼 클릭 이벤트 설정
            startActivity(Intent(this, DataActivity::class.java)) // 데이터 액티비티로 이동
        }
    }

    private fun updateUI(user: FirebaseUser) { // 사용자 정보로 UI 업데이트
        userName.text = user.email // 사용자 이메일 설정
        Toast.makeText(this, "Welcome, ${user.email}", Toast.LENGTH_SHORT).show() // 환영 메시지 표시
    }
}
