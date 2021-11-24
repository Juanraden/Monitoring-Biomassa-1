package com.kedaireka.monitoring_biomassa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.PakanDomain
import com.kedaireka.monitoring_biomassa.databinding.ListPakanBinding

class PakanListAdapter : ListAdapter<PakanDomain, PakanListAdapter.ViewHolder>(DiffCallBack){

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val withDataBinding: ListPakanBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_pakan,
            parent,
            false
        )
        return ViewHolder(withDataBinding)
    }

    class ViewHolder(val binding: ListPakanBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(pakan: PakanDomain){
            with(binding){
                jenisPakanTv.text = pakan.jenis_pakan
            }
        }

    }

    object DiffCallBack: DiffUtil.ItemCallback<PakanDomain>() {
        override fun areItemsTheSame(oldItem: PakanDomain, newItem: PakanDomain): Boolean {
            return oldItem.pakan_id == newItem.pakan_id
        }

        override fun areContentsTheSame(oldItem: PakanDomain, newItem: PakanDomain): Boolean {
            return oldItem == newItem
        }
    }

}