package com.kedaireka.monitoring_biomassa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.FeedingDetailDomain
import com.kedaireka.monitoring_biomassa.data.domain.FeedingDomain
import com.kedaireka.monitoring_biomassa.database.relation.FeedingDetailAndPakan
import com.kedaireka.monitoring_biomassa.databinding.ListFeedingDetailBinding
import com.kedaireka.monitoring_biomassa.util.convertLongToDateString

class FeedingDetailListAdapter(
    val longClickListener: (obj: FeedingDetailDomain) -> Boolean
) : ListAdapter<FeedingDetailAndPakan, FeedingDetailListAdapter.ViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val withDataBinding: ListFeedingDetailBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_feeding_detail,
            parent,
            false
        )
        return ViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListFeedingDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(feedingDetailAndPakan: FeedingDetailAndPakan) {

            val feedingDetailDomain = FeedingDetailDomain(
                detail_id = feedingDetailAndPakan.feedingDetail.detail_id,
                feeding_id = feedingDetailAndPakan.feedingDetail.feeding_id,
                ukuran_tebar = feedingDetailAndPakan.feedingDetail.ukuran_tebar,
                pakan_id = feedingDetailAndPakan.feedingDetail.pakan_id,
                waktu_feeding = feedingDetailAndPakan.feedingDetail.waktu_feeding
            )
            val pakan = feedingDetailAndPakan.pakan

            with(binding) {
                jenisPakanTv.text = pakan.jenis_pakan

                ukuranTebarTv.text = feedingDetailDomain.ukuran_tebar.toString()

                jamPakanTv.text =
                    convertLongToDateString(feedingDetailDomain.waktu_feeding, "H:m")

                detailCard.setOnLongClickListener { longClickListener(feedingDetailDomain) }
            }
        }
    }

    object DiffCallBack : DiffUtil.ItemCallback<FeedingDetailAndPakan>() {
        override fun areItemsTheSame(
            oldItem: FeedingDetailAndPakan,
            newItem: FeedingDetailAndPakan
        ): Boolean {
            return oldItem.feedingDetail.detail_id == newItem.feedingDetail.detail_id
        }

        override fun areContentsTheSame(
            oldItem: FeedingDetailAndPakan,
            newItem: FeedingDetailAndPakan
        ): Boolean {
            return oldItem == newItem
        }
    }
}