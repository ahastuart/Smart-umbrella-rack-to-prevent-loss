package com.example.pushmessage
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth // 1. Firebase 인증 객체 선언
    private lateinit var signupEmail: EditText // 2. 이메일 입력 필드 선언
    private lateinit var signupPassword: EditText // 3. 비밀번호 입력 필드 선언
    private lateinit var signupButton: Button // 4. 회원가입 버튼 선언
    private lateinit var loginRedirectText: TextView // 5. 로그인 화면으로 이동 텍스트 선언

    override fun onCreate(savedInstanceState: Bundle?) { // 6. 액티비티 생성 시 호출되는 메서드
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up) // 7. 레이아웃 설정

        auth = FirebaseAuth.getInstance() // 8. Firebase 인증 인스턴스 초기화
        signupEmail = findViewById(R.id.signup_email) // 9. 이메일 입력 필드와 연결
        signupPassword = findViewById(R.id.signup_password) // 10. 비밀번호 입력 필드와 연결
        signupButton = findViewById(R.id.signup_button) // 11. 회원가입 버튼과 연결
        loginRedirectText = findViewById(R.id.loginRedirectText) // 12. 로그인 화면으로 이동 텍스트와 연결

        signupButton.setOnClickListener { // 13. 회원가입 버튼 클릭 이벤트 설정
            val user = signupEmail.text.toString().trim() // 14. 입력된 이메일 값 가져오기
            val pass = signupPassword.text.toString().trim() // 15. 입력된 비밀번호 값 가져오기

            if (user.isEmpty()) { // 16. 이메일 값이 비어있는지 확인
                signupEmail.error = "Email cannot be empty" // 17. 이메일이 비어있다면 에러 메시지 표시
            }
            if (pass.isEmpty()) { // 18. 비밀번호 값이 비어있는지 확인
                signupPassword.error = "Password cannot be empty" // 19. 비밀번호가 비어있다면 에러 메시지 표시
            } else {
                auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener { task -> // 20. Firebase를 통해 회원가입 수행
                    if (task.isSuccessful) { // 21. 회원가입이 성공하면
                        Toast.makeText(this@SignUpActivity, "SignUp Successful", Toast.LENGTH_SHORT).show() // 22. 성공 메시지 표시
                        startActivity(Intent(this@SignUpActivity, LoginActivity::class.java)) // 23. 로그인 화면으로 이동
                    } else {
                        Toast.makeText(this@SignUpActivity, "SignUp Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show() // 24. 실패 메시지 표시
                    }
                }
            }
        }

        loginRedirectText.setOnClickListener { // 25. 로그인 화면으로 이동 텍스트 클릭 이벤트 설정
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java)) // 26. 로그인 화면으로 이동
        }
    }
}
