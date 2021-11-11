package com.kedaireka.monitoring_biomassa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.databinding.ListKerambaBinding

class KerambaListAdapter(
    val onClick: OnClickListener
): ListAdapter<KerambaDomain, KerambaListAdapter.ViewHolder>(DiffCallBack), Filterable {

    private var list = mutableListOf<KerambaDomain>()

    fun setData(list: List<KerambaDomain>?){
        this.list.clear()
        this.list.addAll(list!!)
        submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val withDataBinding: ListKerambaBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_keramba,
            parent,
            false
        )
        return ViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val keramba = getItem(position)
        holder.bind(keramba)
    }

    inner class ViewHolder(private val listKerambaBinding: ListKerambaBinding):
    RecyclerView.ViewHolder(listKerambaBinding.root){

        fun bind(kerambaDomain: KerambaDomain){
            with(listKerambaBinding){
                keramba = kerambaDomain

                namaKerambaTv.text = kerambaDomain.nama_keramba

                kerambaCard.setOnClickListener {
                    onClick.onClick(kerambaDomain)
                }
            }
            listKerambaBinding.executePendingBindings()
        }
    }

    object DiffCallBack: DiffUtil.ItemCallback<KerambaDomain>() {
        override fun areItemsTheSame(oldItem: KerambaDomain, newItem: KerambaDomain): Boolean {
            return oldItem.kerambaid == newItem.kerambaid
        }

        override fun areContentsTheSame(oldItem: KerambaDomain, newItem: KerambaDomain): Boolean {
            return oldItem == newItem
        }
    }

    override fun getFilter(): Filter {
        return customFilter
    }

    private val customFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            val filteredList = mutableListOf<KerambaDomain>()

            if (constraint == null || constraint.isEmpty()){
                filteredList.clear()
                filteredList.addAll(list)
            } else {
                filteredList.clear()
                for (keramba in list){
                    if (keramba.nama_keramba.lowercase().contains(constraint.toString().lowercase())){
                        filteredList.add(keramba)
                    }
                }
            }
            results.values = filteredList

            return results
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            submitList(p1?.values as MutableList<KerambaDomain>)
        }
    }
}

class OnClickListener(val clickListener: (kerambaDomain: KerambaDomain) -> Unit){
    fun onClick(kerambaDomain: KerambaDomain) = clickListener(kerambaDomain)
}


