package com.exaiio.secondproject.adapterrlv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exaiio.secondproject.databinding.ItemMemoRlvBinding
import com.exaiio.secondproject.room.entity.Memo

class MemoAdapter(
    private val onItemClick: (Memo) -> Unit = {},
    private val onItemLongClick: (Memo) -> Unit = {}
) : ListAdapter<Memo, MemoAdapter.MemoViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val binding = ItemMemoRlvBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MemoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MemoViewHolder(
        private val binding: ItemMemoRlvBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(memo: Memo) {
            binding.tvTitle.text = memo.title
            binding.tvContent.text = memo.content

            binding.root.setOnClickListener { onItemClick(memo) }
            binding.root.setOnLongClickListener {
                onItemLongClick(memo)
                true
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Memo>() {
            override fun areItemsTheSame(oldItem: Memo, newItem: Memo): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Memo, newItem: Memo): Boolean =
                oldItem == newItem   // data class 自动生成的 equals
        }
    }
}