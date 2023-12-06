package com.kavarera.iottugasakhir

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivityViewModel:ViewModel() {
    private val _logs = MutableLiveData<List<LogItem>>()
    val logs:LiveData<List<LogItem>> get() = _logs

    private val _deviceIdStatus = MutableLiveData<DeviceIdStatus>()
    val deviceIdStatus: LiveData<DeviceIdStatus> get() = _deviceIdStatus

    private val database = FirebaseDatabase.getInstance()

    fun checkDeviceId(deviceId: String) {
        val firebase = database.getReference("deviceid")
        firebase.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snap = task.result
                val ids = snap?.value as? List<String>
                if (ids?.contains(deviceId) == true) {
                    _deviceIdStatus.value = DeviceIdStatus.Valid(deviceId)
                    fetchLogs(deviceId)
                } else {
                    _deviceIdStatus.value = DeviceIdStatus.Invalid(deviceId)
                }
            }
        }
    }

    private fun fetchLogs(deviceId: String) {
        val logReference = database.getReference("deviceconfig").child(deviceId).child("LOG")
        logReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val logs = mutableListOf<LogItem>()
                for (dateSnapshot in dataSnapshot.children) {
                    val date = dateSnapshot.key
                    for (randomSnapshot in dateSnapshot.children) {
                        val randomKey = randomSnapshot.key
                        val text = randomSnapshot.getValue(String::class.java)
                        val logItem = LogItem(date.orEmpty(), randomKey.orEmpty(), text.orEmpty())
                        logs.add(logItem)
                    }
                }
                _logs.value = logs
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("fetchLog","error while fetching db, $databaseError")
            }
        })
    }

    fun setSensor(deviceId:String,isChecked:Int) {

            val sensorFB = FirebaseDatabase.getInstance().getReference("deviceconfig").child(deviceId)
            sensorFB.child("sensor").setValue(isChecked)
                .addOnSuccessListener {
                    Log.d("testing","berhasil")
                }
                .addOnFailureListener {
                    Log.d("testing","gagal : $it")
                }
        database.getReference("deviceconfig").child(deviceId).child("LOG")
            .child(SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                .format(Date()).toString())
            .child("fromAndroid")
            .setValue("Set Sensor Status to $isChecked from phone")
            .addOnSuccessListener {
                Log.d("testing","berhasil")
            }
            .addOnFailureListener {
                Log.d("testing","gagal : $it")
            }
    }

    fun setLampuStatus(deviceId: String,switchValue: Int) {
        val firebase = FirebaseDatabase.getInstance().getReference("deviceconfig").child(deviceId)
        firebase.child("lampu").setValue(switchValue)
            .addOnSuccessListener {
                Log.d("testing","berhasil")
            }
            .addOnFailureListener {
                Log.d("testing","gagal : $it")
            }
        database.getReference("deviceconfig").child(deviceId).child("LOG")
            .child(SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                .format(Date()).toString())
            .child("fromAndroid")
            .setValue("Set Lampu Status to $switchValue from phone")
            .addOnSuccessListener {
                Log.d("testing","berhasil")
            }
            .addOnFailureListener {
                Log.d("testing","gagal : $it")
            }
    }

    fun setTimerHidup(deviceId:String,hour: Int, minute: Int) {
        database.getReference("deviceconfig").child(deviceId).child("timerhidup").setValue("$hour:$minute")
            .addOnSuccessListener {
                Log.d("testing","berhasil")
            }
            .addOnFailureListener {
                Log.d("testing","gagal : $it")
            }
        database.getReference("deviceconfig").child(deviceId).child("LOG")
            .child(SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                .format(Date()).toString())
                .child("fromAndroid")
                .setValue("Set Timer hidup from phone to $hour:$minute")
            .addOnSuccessListener {
                Log.d("testing","berhasil")
            }
            .addOnFailureListener {
                Log.d("testing","gagal : $it")
            }

    }
    fun setTimerMati(deviceId:String,hour: Int, minute: Int) {
        database.getReference("deviceconfig").child(deviceId).child("timermati").setValue("$hour:$minute")
            .addOnSuccessListener {
                Log.d("testing","berhasil")
            }
            .addOnFailureListener {
                Log.d("testing","gagal : $it")
            }
        database.getReference("deviceconfig").child(deviceId).child("LOG")
            .child(SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                .format(Date()).toString())
            .child("fromAndroid")
            .setValue("Set Timer mati from phone to $hour:$minute")
            .addOnSuccessListener {
                Log.d("testing","berhasil")
            }
            .addOnFailureListener {
                Log.d("testing","gagal : $it")
            }

    }

    fun clearLogs() {
        _logs.value= emptyList()
    }

    sealed class DeviceIdStatus {
        data class Valid(val deviceId: String) : DeviceIdStatus()
        data class Invalid(val deviceId: String) : DeviceIdStatus()
    }
}
