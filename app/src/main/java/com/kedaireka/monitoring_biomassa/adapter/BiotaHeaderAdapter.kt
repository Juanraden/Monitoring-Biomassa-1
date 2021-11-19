package com.kedaireka.monitoring_biomassa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.databinding.HeaderListBiotaBinding

class BiotaHeaderAdapter (val clickListener: () -> Unit): RecyclerView.Adapter<BiotaHeaderAdapter.HeaderViewHolder>() {

    inner class HeaderViewHolder(private val binding: HeaderListBiotaBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind() {
            binding.addBiotaBtn.setOnClickListener { clickListener() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val withDataBinding: HeaderListBiotaBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.header_list_biota,
            parent,
            false
        )
        return HeaderViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 1
    }
}