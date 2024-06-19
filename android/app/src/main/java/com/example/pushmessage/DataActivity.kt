package com.example.pushmessage

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// 센서 데이터를 위한 데이터 클래스 정의
data class SensorData(
    val distance: Double? = null,
    val slot_number: Int? = null,
    val status: String? = null,
    val timestamp: String? = null
)

class DataActivity : AppCompatActivity() {

    // Firebase 데이터베이스 참조 변수
    private lateinit var database: DatabaseReference

    private val TAG = "DataActivity"

    // RecyclerView 및 어댑터 변수
    private lateinit var recyclerView: RecyclerView
    private lateinit var sensorDataList: MutableList<SensorData>
    private lateinit var adapter: SensorDataAdapter
    private lateinit var selectedDateTextView: TextView
    private lateinit var previousDateButton: Button
    private lateinit var nextDateButton: Button

    // 시간 선택 텍스트뷰 변수
    private lateinit var startTimeTextView: TextView
    private lateinit var endTimeTextView: TextView

    // 선택된 날짜 및 시간 변수 초기화
    private var selectedDate = LocalDate.now()
    private var startTime: LocalTime? = null
    private var endTime: LocalTime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)

        // 이전으로 돌아가는 텍스트뷰 클릭 이벤트 설정
        val backTextView: TextView = findViewById(R.id.backTextView)
        backTextView.setOnClickListener {
            onBackPressed()  // 뒤로가기 기능 수행
        }

        // RecyclerView 초기화 및 설정
        recyclerView = findViewById(R.id.sensorDataRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        sensorDataList = mutableListOf()
        adapter = SensorDataAdapter(sensorDataList)
        recyclerView.adapter = adapter

        // Firebase 데이터베이스 초기화
        database = Firebase.database.reference

        // UI 요소 초기화
        selectedDateTextView = findViewById(R.id.selectedDateTextView)
        previousDateButton = findViewById(R.id.previousDateButton)
        nextDateButton = findViewById(R.id.nextDateButton)

        startTimeTextView = findViewById(R.id.startTimeTextView)
        endTimeTextView = findViewById(R.id.endTimeTextView)

        // 선택된 날짜 텍스트 업데이트
        updateSelectedDateText()

        // 이전 날짜 버튼 클릭 리스너 설정
        previousDateButton.setOnClickListener {
            selectedDate = selectedDate.minusDays(1)
            updateSelectedDateText()
            fetchSensorData()
        }

        // 다음 날짜 버튼 클릭 리스너 설정
        nextDateButton.setOnClickListener {
            selectedDate = selectedDate.plusDays(1)
            updateSelectedDateText()
            fetchSensorData()
        }

        // 시작 시간 텍스트뷰 클릭 리스너 설정
        startTimeTextView.setOnClickListener {
            showTimePickerDialog(true)
        }

        // 종료 시간 텍스트뷰 클릭 리스너 설정
        endTimeTextView.setOnClickListener {
            showTimePickerDialog(false)
        }

        // 센서 데이터 가져오기
        fetchSensorData()
    }

    // 선택된 날짜 텍스트 업데이트 메서드
    private fun updateSelectedDateText() {
        selectedDateTextView.text = selectedDate.format(DateTimeFormatter.ofPattern("yy-MM-dd"))
    }

    // 시간 선택 다이얼로그 표시 메서드
    private fun showTimePickerDialog(isStartTime: Boolean) {
        val currentTime = if (isStartTime) startTime ?: LocalTime.now() else endTime ?: LocalTime.now()
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val selectedTime = LocalTime.of(hourOfDay, minute)
                if (isStartTime) {
                    startTime = selectedTime
                    startTimeTextView.text = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                } else {
                    endTime = selectedTime
                    endTimeTextView.text = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                }
                fetchSensorData()
            },
            currentTime.hour,
            currentTime.minute,
            true
        )
        timePickerDialog.show()
    }

    // Firebase에서 센서 데이터를 가져오는 메서드
    // Firebase에서 센서 데이터를 가져오는 메서드
    private fun fetchSensorData() {
        // Firebase의 umbrella_stand 참조
        val sensorDataRef = database.child("umbrella_stand")

        // 선택된 날짜와 시간 범위 계산
        val startTimestamp = selectedDate.atTime(startTime ?: LocalTime.MIN)
        val endTimestamp = selectedDate.atTime(endTime ?: LocalTime.MAX)

        // 데이터 가져오기 위한 쿼리 생성 (선택된 날짜와 시간 사이의 데이터만 가져오도록 설정)
        val query = sensorDataRef.orderByChild("timestamp")
            .startAt(startTimestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
            .endAt(endTimestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))


        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                sensorDataList.clear()
                for (childSnapshot in dataSnapshot.children) {
                    val sensorData = childSnapshot.getValue(SensorData::class.java)
                    if (sensorData != null) {
                        sensorDataList.add(sensorData)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "fetchSensorData:onCancelled", databaseError.toException())
            }
        }
        query.addListenerForSingleValueEvent(valueEventListener)
    }


    // 선택된 날짜와 시간 범위에 포함되는지 확인하는 함수
    private fun isWithinSelectedDateAndTimeRange(timestamp: LocalDateTime): Boolean {
        val selectedStartDateTime = selectedDate.atTime(startTime ?: LocalTime.MIN)
        val selectedEndDateTime = selectedDate.atTime(endTime ?: LocalTime.MAX)

        return timestamp.isAfter(selectedStartDateTime) && timestamp.isBefore(selectedEndDateTime)
    }
}

// RecyclerView 어댑터 클래스
class SensorDataAdapter(private val sensorDataList: List<SensorData>) :
    RecyclerView.Adapter<SensorDataAdapter.SensorDataViewHolder>() {

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorDataViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sensor_data_item, parent, false)
        return SensorDataViewHolder(view)
    }

    // ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: SensorDataViewHolder, position: Int) {
        val sensorData = sensorDataList[position]
        holder.bind(sensorData)
    }

    // 데이터 아이템 수 반환
    override fun getItemCount(): Int {
        return sensorDataList.size
    }

    // ViewHolder 클래스
    class SensorDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val distanceTextView: TextView = itemView.findViewById(R.id.distanceTextView)
        private val slotNumberTextView: TextView = itemView.findViewById(R.id.slotNumberTextView)
        private val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)

        // SensorData 객체를 뷰에 바인딩하는 메서드
        fun bind(sensorData: SensorData) {
            distanceTextView.text = "Distance: ${sensorData.distance}"
            slotNumberTextView.text = "Slot Number: ${sensorData.slot_number}"
            statusTextView.text = "Status: ${sensorData.status}"
            timestampTextView.text = "Timestamp: ${sensorData.timestamp}"
        }
    }
}
