package com.example.pushmessage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth // 1. FirebaseAuth 객체
    private lateinit var loginEmail: EditText // 2. 이메일 입력 필드
    private lateinit var loginPassword: EditText // 3. 비밀번호 입력 필드
    private lateinit var loginButton: Button // 4. 로그인 버튼
    private lateinit var signUpRedirectText: TextView // 5. 회원가입 리디렉션 텍스트

    override fun onCreate(savedInstanceState: Bundle?) { // 5. 액티비티가 생성될 때 호출되는 메서드
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // 6. 레이아웃 설정

        auth = FirebaseAuth.getInstance() // 7. FirebaseAuth 인스턴스 초기화
        loginEmail = findViewById(R.id.login_email) // 8. 이메일 입력 필드와 연결
        loginPassword = findViewById(R.id.login_password) // 9. 비밀번호 입력 필드와 연결
        loginButton = findViewById(R.id.login_button) // 10. 로그인 버튼과 연결
        signUpRedirectText = findViewById(R.id.signUpRedirectText) // 회원가입 리디렉션 텍스트와 연결

        loginButton.setOnClickListener { // 11. 로그인 버튼 클릭 이벤트 설정
            val email = loginEmail.text.toString().trim() // 12. 입력된 이메일 값 가져오기
            val password = loginPassword.text.toString().trim() // 13. 입력된 비밀번호 값 가져오기

            if (email.isEmpty()) { // 14. 이메일 값이 비어있는지 확인
                loginEmail.error = "Email cannot be empty" // 15. 이메일이 비어있다면 에러 메시지 표시
                return@setOnClickListener // 16. 클릭 이벤트 종료
            }
            if (password.isEmpty()) { // 17. 비밀번호 값이 비어있는지 확인
                loginPassword.error = "Password cannot be empty" // 18. 비밀번호가 비어있다면 에러 메시지 표시
                return@setOnClickListener // 19. 클릭 이벤트 종료
            }

            auth.signInWithEmailAndPassword(email, password) // 20. 이메일과 비밀번호로 로그인 시도
                .addOnCompleteListener(this) { task -> // 21. 비동기 작업 완료 리스너
                    if (task.isSuccessful) { // 22. 로그인 성공
//                        val user = auth.currentUser // 23. 현재 로그인된 사용자 가져오기
                        startActivity(Intent(this, logout::class.java)) // 24. 메인 액티비티로 이동
                        finish() // 25. 현재 액티비티 종료
                    } else { // 26. 로그인 실패
                        Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show() // 27. 실패 메시지 표시
                    }
                }
        }

        signUpRedirectText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
