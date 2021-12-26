package com.kedaireka.monitoring_biomassa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.databinding.HeaderCardBinding
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString

class HeaderCardAdapter(
    val tanggal: Long,
    val clickListener: () -> Unit
) : RecyclerView.Adapter<HeaderCardAdapter.HeaderViewHolder>() {

    inner class HeaderViewHolder(private val binding: HeaderCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            with(binding) {
                tanggalPemberianPakanTv.text = convertLongToDateString(tanggal, "EEEE dd-MMM-yyyy")

                editBtn.setOnClickListener { clickListener() }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val withDataBinding: HeaderCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.header_card,
            parent,
            false
        )
        return HeaderViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: HeaderCardAdapter.HeaderViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 1
    }
}