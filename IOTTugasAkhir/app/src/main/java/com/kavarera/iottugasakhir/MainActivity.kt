package com.kavarera.iottugasakhir

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kavarera.iottugasakhir.adapters.LogAdapter
import com.kavarera.iottugasakhir.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding :ActivityMainBinding
    private val viewModel by viewModels<MainActivityViewModel>()
    private lateinit var deviceId :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        viewModel.deviceIdStatus.observe(this,{
            when(it){
                is MainActivityViewModel.DeviceIdStatus.Valid->{
                    binding.tvDeviceId.text="Device Id : ${it.deviceId}"
                    deviceId = it.deviceId
                    binding.swSensor.isEnabled = true
                    binding.swStatusLampu.isEnabled = true
                    binding.btnSetTimerHidup.visibility = View.VISIBLE
                    binding.btnSetTimerMati.visibility = View.VISIBLE
                }
                is MainActivityViewModel.DeviceIdStatus.Invalid->{
                    binding.tvDeviceId.setText(R.string.device_id)
                    binding.swSensor.isEnabled = false
                    binding.swStatusLampu.isEnabled = false
                    binding.btnSetTimerHidup.visibility = View.INVISIBLE
                    binding.btnSetTimerMati.visibility = View.INVISIBLE
                    viewModel.clearLogs()
                }
            }
        })

        binding.btnCheckId.setOnClickListener {
            if (binding.etDeviceId.text.isNullOrEmpty().not()) {
                viewModel.checkDeviceId(binding.etDeviceId.text.toString())
            }
        }

        binding.swStatusLampu.setOnCheckedChangeListener { _, isChecked ->
            val switchValue: Int = if (isChecked) 1 else 0
            viewModel.setLampuStatus(deviceId,switchValue)
        }

        binding.swSensor.setOnCheckedChangeListener { _, isChecked ->
            val switchValue: Int = if (isChecked) 1 else 0
            viewModel.setSensor(deviceId, switchValue)
        }

        val rvAdapter = LogAdapter(viewModel.logs.value?: emptyList())
        binding.rvLog.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = rvAdapter
        }
        viewModel.logs.observe(this,{
            rvAdapter.submitList(it)
        })

        binding.btnSetTimerHidup.setOnClickListener {
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(0)
                .setMinute(0)
                .setTitleText("Set Time For Lamp to be Active")
                .build()
                .apply {
                    addOnPositiveButtonClickListener{picker->
                        val hour = getHour()
                        val minute = getMinute()
                        viewModel.setTimerHidup(deviceId,hour,minute)
                    }
                }
                .show(supportFragmentManager,"TimerForOn")
        }
        binding.btnSetTimerMati.setOnClickListener {
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(0)
                .setMinute(0)
                .setTitleText("Set Time For Lamp to be InActive")
                .build()
                .apply {
                    addOnPositiveButtonClickListener{picker->
                        val hour = getHour()
                        val minute = getMinute()
                        viewModel.setTimerMati(deviceId,hour,minute)
                    }
                }
                .show(supportFragmentManager,"TimerForOff")
        }
        setContentView(binding.root)


    }
}

data class LogItem(val date: String, val randomKey: String, val text: String)