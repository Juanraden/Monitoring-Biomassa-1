package com.kedaireka.monitoring_biomassa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.PanenDomain
import com.kedaireka.monitoring_biomassa.database.entity.Panen
import com.kedaireka.monitoring_biomassa.database.relation.PanenAndBiota
import com.kedaireka.monitoring_biomassa.databinding.ListPanenBinding
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString
import javax.inject.Inject


class PanenListAdapter: ListAdapter<PanenAndBiota, PanenListAdapter.ViewHolder>(DiffCallBack) {

    @Inject lateinit var panenMapper: EntityMapper<Panen, PanenDomain>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val withDataBinding: ListPanenBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_panen,
            parent,
            false
        )
        return ViewHolder(withDataBinding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListPanenBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(panen: PanenAndBiota) {
            val panenDomain: PanenDomain = panenMapper.mapFromEntity(panen.panen)

            with(binding) {
                jenisBiotaTv.text = panen.biota.jenis_biota

                ukuranBiotaTv.text = panenDomain.panjang.toString()

                bobotBiotaTv.text = panenDomain.bobot.toString()

                jumlahPanenBiotaTv.text = panenDomain.jumlah_hidup.toString()

                gagalPanenBiotaTv.text = panenDomain.jumlah_mati.toString()

                tanggalPanenBiotaTv.text =
                    convertLongToDateString(panenDomain.tanggal_panen, "EEEE dd-MMM-yyyy")
            }

            binding.executePendingBindings()
        }

    }

    object DiffCallBack : DiffUtil.ItemCallback<PanenAndBiota>() {
        override fun areItemsTheSame(oldItem: PanenAndBiota, newItem: PanenAndBiota): Boolean {
            return oldItem.panen.activity_id == newItem.panen.activity_id
        }

        override fun areContentsTheSame(oldItem: PanenAndBiota, newItem: PanenAndBiota): Boolean {
            return oldItem == newItem
        }
    }
}