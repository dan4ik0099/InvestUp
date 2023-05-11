package com.example.investup.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.R
import com.example.investup.databinding.TagItemBinding
import com.example.investup.retrofit.dataClass.Tag

class TagInPostAdapter() : RecyclerView.Adapter<TagInPostAdapter.TagInPostHolder>() {
    private var tagList = ArrayList<Tag>()

    class TagInPostHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = TagItemBinding.bind(item)
        fun bind(tag: Tag) = with(binding) {
            tagName.text = tag.value
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagInPostHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tag_item, parent, false)
        return TagInPostHolder(view)
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    override fun onBindViewHolder(holder: TagInPostHolder, position: Int) {
        holder.bind(tagList[position])
    }
    fun addTags(tags: ArrayList<Tag>){
        tagList.clear()
        tagList.addAll(tags)
        notifyDataSetChanged()
    }
    interface Listener{
        fun onClickTag(tag: Tag)
    }



}