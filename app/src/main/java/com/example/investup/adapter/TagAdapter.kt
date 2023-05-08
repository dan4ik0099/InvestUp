package com.example.investup.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.R
import com.example.investup.databinding.TagItemBinding
import com.example.investup.retrofit.dataClass.Tag

class TagAdapter(val listener: Listener) : RecyclerView.Adapter<TagAdapter.TagHolder>() {
    private var tagList = ArrayList<Tag>()


    class TagHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = TagItemBinding.bind(item)
        fun bind(tag: Tag, listener: Listener) = with(binding) {
            if (tag.isActive) isOnImage.visibility = View.VISIBLE
            else isOnImage.visibility = View.GONE
            itemView.setOnClickListener {
                if (tag.isActive) {
                    isOnImage.visibility = View.GONE
                    tag.isActive = false
                } else {
                    isOnImage.visibility = View.VISIBLE
                    tag.isActive = true
                }
                listener.onClickTag(tag)

            }
            tagName.text = tag.value

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tag_item, parent, false)
        return TagHolder(view)
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    override fun onBindViewHolder(holder: TagHolder, position: Int) {
        holder.bind(tagList[position], listener)
    }

    fun addTags(tags: ArrayList<Tag>) {
        tagList.addAll(tags)
        notifyDataSetChanged()
    }

    open fun addActiveTags(tags: ArrayList<Tag>) {
        tagList.addAll(tags)
        notifyDataSetChanged()
    }

    interface Listener {
        fun onClickTag(tag: Tag)
    }


}