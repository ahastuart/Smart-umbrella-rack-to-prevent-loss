package com.example.pushmessage


import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.example.pushmessage.R


class PhotoActivity : AppCompatActivity() {
    private lateinit var imageGallery: LinearLayout  // 이미지 갤러리 레이아웃 변수 선언
    private val database = FirebaseDatabase.getInstance()  // Firebase 데이터베이스 인스턴스 생성
    private val matchedDataRef = database.getReference("matched_data")  // Firebase 데이터베이스 참조를 가져옴

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)  // PhotoActivity 레이아웃을 설정

        imageGallery = findViewById(R.id.imageGallery)  // 이미지 갤러리 레이아웃 초기화

        loadInitialImagesFromFirebase()  // 초기 이미지 로딩 함수 호출

        matchedDataRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                addImageToGallery(snapshot)  // 새로운 자식이 추가되면 갤러리에 이미지 추가
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                updateImageInGallery(snapshot)  // 자식이 변경되면 갤러리의 이미지 업데이트
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                removeImageFromGallery(snapshot)  // 자식이 제거되면 갤러리에서 이미지 제거
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error loading images", error.toException())  // 에러가 발생하면 로그 출력
            }
        })
        // 이전으로 돌아가는 텍스트뷰 클릭 이벤트 설정
        val backTextView: TextView = findViewById(R.id.backTextView)
        backTextView.setOnClickListener {
            onBackPressed()  // 뒤로가기 기능 수행
        }
    }

    private fun loadInitialImagesFromFirebase() {
        matchedDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    addImageToGallery(dataSnapshot)  // 각 데이터 스냅샷에 대해 갤러리에 이미지 추가
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error loading initial images", error.toException())  // 초기 이미지 로딩에 에러가 발생하면 로그 출력
            }
        })
    }

    private fun addImageToGallery(dataSnapshot: DataSnapshot) {
        val personImageUrl = dataSnapshot.child("person_img_url").getValue(String::class.java)  // 개인 이미지 URL 가져옴
        val umbrellaImageUrl = dataSnapshot.child("umbrella_img_url").getValue(String::class.java)  // 우산 이미지 URL 가져옴
        if (personImageUrl != null && umbrellaImageUrl != null) {  // 이미지 URL이 null이 아니면 이미지 추가
            addImageView(personImageUrl)  // 개인 이미지 추가
            addImageView(umbrellaImageUrl)  // 우산 이미지 추가
        } else {
            Log.e("Firebase", "Image URLs are null")  // 이미지 URL이 null이면 로그 출력
        }
    }

    private fun updateImageInGallery(dataSnapshot: DataSnapshot) {
        addImageToGallery(dataSnapshot)  // 이미지 갱신 함수
    }

    private fun removeImageFromGallery(dataSnapshot: DataSnapshot) {
        val personImageUrl = dataSnapshot.child("person_img_url").getValue(String::class.java)  // 개인 이미지 URL 가져옴
        val umbrellaImageUrl = dataSnapshot.child("umbrella_img_url").getValue(String::class.java)  // 우산 이미지 URL 가져옴
        if (personImageUrl != null) {  // 개인 이미지 URL이 null이 아니면
            val personImageView = findImageViewByUrl(personImageUrl)  // 해당 URL의 이미지 뷰를 찾아서
            personImageView?.let { imageGallery.removeView(it) }  // 갤러리에서 제거
        }
        if (umbrellaImageUrl != null) {  // 우산 이미지 URL이 null이 아니면
            val umbrellaImageView = findImageViewByUrl(umbrellaImageUrl)  // 해당 URL의 이미지 뷰를 찾아서
            umbrellaImageView?.let { imageGallery.removeView(it) }  // 갤러리에서 제거
        }
    }

    private fun findImageViewByUrl(url: String): ImageView? {  // URL로 이미지 뷰를 찾는 함수
        for (i in 0 until imageGallery.childCount) {  // 이미지 갤러리의 모든 자식에 대해
            val imageView = imageGallery.getChildAt(i) as? ImageView  // 이미지 뷰를 가져오고
            if (imageView?.tag == url) {  // 태그가 URL과 같으면
                return imageView  // 해당 이미지 뷰 반환
            }
        }
        return null  // 찾지 못하면 null 반환
    }

    private fun addImageView(url: String) {  // 이미지 뷰를 추가하는 함수
        val imageView = ImageView(this)  // 새 이미지 뷰 생성
        imageView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 16, 0, 16)  // 마진 설정
        }
        imageView.tag = url  // 태그 설정
        Picasso.get()
            .load(url)  // 이미지 로드
            .resize(800, 600)  // 이미지 크기 설정
            .into(imageView, object : Callback {
                override fun onSuccess() {  // 로드 성공시
                    Log.d("Picasso", "Image loaded successfully: $url")  // 로그 출력
                }

                override fun onError(e: Exception?) {  // 로드 실패시
                    Log.e("Picasso", "Error loading image: ${e?.message}")  // 에러 메시지 출력
                }
            })
        imageGallery.addView(imageView)  // 갤러리에 이미지 뷰 추가
    }
}
