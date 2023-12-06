package com.kavarera.iottugasakhir.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kavarera.iottugasakhir.LogItem
import com.kavarera.iottugasakhir.databinding.LayoutLogBinding

class LogAdapter(private var datetimes:List<LogItem>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LogViewHolder(LayoutLogBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return datetimes.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LogViewHolder).bind(datetimes[position].date,datetimes[position].text)
    }

    fun submitList(logs:List<LogItem>){
        datetimes = logs
        notifyDataSetChanged()
    }

    class LogViewHolder(private val binding: LayoutLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(datetime:String, message:String){
            binding.tvDatetime.text = datetime
            binding.tvLog.text=message
        }
    }
}