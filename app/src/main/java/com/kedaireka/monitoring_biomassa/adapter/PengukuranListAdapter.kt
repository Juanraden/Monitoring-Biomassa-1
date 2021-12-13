package com.kedaireka.monitoring_biomassa.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.PengukuranDomain
import com.kedaireka.monitoring_biomassa.databinding.ListPengukuranBinding
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString

class PengukuranListAdapter(
    val longClickListener: (obj: PengukuranDomain) -> Boolean
) : ListAdapter<PengukuranDomain, PengukuranListAdapter.ViewHolder>(DiffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val withDataBinding: ListPengukuranBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_pengukuran,
            parent,
            false
        )

        return ViewHolder(withDataBinding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ListPengukuranBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pengukuran: PengukuranDomain) {
            with(binding) {
                bobotBiotaTv.text = pengukuran.bobot.toString()

                ukuranBiotaTv.text = pengukuran.panjang.toString()

                tanggalUkurTv.text = context.getString(
                    R.string.tanggal_pengukuran,
                    convertLongToDateString(pengukuran.tanggal_ukur, "EEEE dd-MMM-yyyy")
                )

                pengukuranCard.setOnLongClickListener { longClickListener(pengukuran) }
            }

            binding.executePendingBindings()
        }
    }

    object DiffCallBack : DiffUtil.ItemCallback<PengukuranDomain>() {
        override fun areItemsTheSame(
            oldItem: PengukuranDomain,
            newItem: PengukuranDomain
        ): Boolean {
            return oldItem.pengukuran_id == oldItem.pengukuran_id
        }

        override fun areContentsTheSame(
            oldItem: PengukuranDomain,
            newItem: PengukuranDomain
        ): Boolean {
            return oldItem == newItem
        }
    }
}