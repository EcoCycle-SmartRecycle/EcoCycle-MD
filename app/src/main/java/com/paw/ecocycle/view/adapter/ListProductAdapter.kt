package com.paw.ecocycle.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.paw.ecocycle.R
import com.paw.ecocycle.databinding.ItemProductBinding
import com.paw.ecocycle.model.remote.response.DataItem

class ListProductAdapter : ListAdapter<DataItem, ListProductAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dataItem: DataItem) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(dataItem.path)
                    .centerCrop()
                    .into(ivItemPhoto)
                tvItemRust.text =
                    itemView.context.getString(
                        R.string.tv_rust_result,
                        dataItem.rusty.toString()
                    )
                tvItemNoRust.text =
                    itemView.context.getString(
                        R.string.tv_norust_result,
                        dataItem.noRust.toString()
                    )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}