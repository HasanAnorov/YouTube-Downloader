package com.hasan.youtubedownloader.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.databinding.ItemDownloadBinding
import com.hasan.youtubedownloader.ui.models.ItemDownload

class HomeAdapter(var applicationList:ArrayList<ItemDownload>, val itemClick :(video:ItemDownload) -> Unit): RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    inner class HomeViewHolder( var binding: ItemDownloadBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(download:ItemDownload){
            Glide
                .with(binding.root.context)
                .load(download.image)
                .centerCrop()
                .placeholder(R.drawable.video)
                .into(binding.ivVideo)
            //binding.ivApplication.setImageResource(application.image)
            binding.root.setOnClickListener {
                itemClick(download)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = ItemDownloadBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        //view.root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int = applicationList.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.onBind(applicationList[position])
    }

}