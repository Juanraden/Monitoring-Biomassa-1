package com.kedaireka.monitoring_biomassa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.BiotaDomain
import com.kedaireka.monitoring_biomassa.databinding.ListBiotaBinding

class BiotaListAdapter(
    val clickListener: (obj: BiotaDomain) -> Unit
): ListAdapter<BiotaDomain, BiotaListAdapter.ViewHolder>(DiffCallBack) {
    object DiffCallBack: DiffUtil.ItemCallback<BiotaDomain>() {
        override fun areItemsTheSame(oldItem: BiotaDomain, newItem: BiotaDomain): Boolean {
            return oldItem.biota_id == newItem.biota_id
        }

        override fun areContentsTheSame(oldItem: BiotaDomain, newItem: BiotaDomain): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val withDataBinding: ListBiotaBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_biota,
            parent,
            false
        )

        return ViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val biota = getItem(position)
        holder.bind(biota)
    }

    inner class ViewHolder(private val binding: ListBiotaBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(biotaDomain: BiotaDomain){
            with(binding){
                jenisBiotaTv.text = biotaDomain.jenis_biota

                jumlahBibitTv.text = biotaDomain.jumlah_bibit.toString()

                biotaCard.setOnClickListener { clickListener(biotaDomain) }
            }

            binding.executePendingBindings()
        }

    }
}