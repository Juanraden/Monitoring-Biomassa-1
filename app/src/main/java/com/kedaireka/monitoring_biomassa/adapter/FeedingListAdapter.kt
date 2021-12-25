package com.kedaireka.monitoring_biomassa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.FeedingDomain
import com.kedaireka.monitoring_biomassa.databinding.ListFeedingBinding

class FeedingListAdapter(
    val clickListener: (obj: FeedingDomain) -> Unit,
    val longClickListener: (obj: FeedingDomain) -> Boolean
): ListAdapter<FeedingDomain, FeedingListAdapter.ViewHolder>(DiffCallBack) {
    object DiffCallBack: DiffUtil.ItemCallback<FeedingDomain>() {
        override fun areItemsTheSame(oldItem: FeedingDomain, newItem: FeedingDomain): Boolean {
            return oldItem.feeding_id == newItem.feeding_id
        }

        override fun areContentsTheSame(oldItem: FeedingDomain, newItem: FeedingDomain): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val withDataBinding: ListFeedingBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_feeding,
            parent,
            false
        )

        return ViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feeding = getItem(position)
        holder.bind(feeding)
    }

    inner class ViewHolder(private val binding: ListFeedingBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(feedingDomain: FeedingDomain){
            with(binding){
                tanggalFeedingTv.text = feedingDomain.tanggal_feeding.toString()

                feedingCard.setOnClickListener { clickListener(feedingDomain) }

                feedingCard.setOnLongClickListener { longClickListener(feedingDomain) }
            }

            binding.executePendingBindings()
        }

    }
}