package com.kedaireka.monitoring_biomassa.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.BiotaDomain
import com.kedaireka.monitoring_biomassa.databinding.ListHistoryBiotaBinding
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString

class BiotaHistoryListAdapter: ListAdapter<BiotaDomain, BiotaHistoryListAdapter.ViewHolder>(DiffCallBack) {
    object DiffCallBack: DiffUtil.ItemCallback<BiotaDomain>() {
        override fun areItemsTheSame(oldItem: BiotaDomain, newItem: BiotaDomain): Boolean {
            return oldItem.biota_id == newItem.biota_id
        }

        override fun areContentsTheSame(oldItem: BiotaDomain, newItem: BiotaDomain): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val withDataBinding: ListHistoryBiotaBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_history_biota,
            parent,
            false
        )
        return ViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val biota = getItem(position)
        holder.bind(biota)
    }

    inner class ViewHolder(private val binding: ListHistoryBiotaBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(biotaDomain: BiotaDomain){
            with(binding){
                jenisBiotaTv.text = biotaDomain.jenis_biota

                jumlahBiotaTv.text = biotaDomain.jumlah_bibit.toString()

                bobotBiotaTv.text = biotaDomain.bobot.toString()

                tanggalPanenTv.text = convertLongToDateString(biotaDomain.tanggal_panen, "EEEE dd-MMM-yyyy")

                tanggalTebarTv.text = convertLongToDateString(biotaDomain.tanggal_tebar, "EEEE dd-MMM-yyyy")
            }

            binding.executePendingBindings()
        }

    }
}