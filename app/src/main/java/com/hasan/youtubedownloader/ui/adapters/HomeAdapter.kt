package com.hasan.youtubedownloader.ui.adapters

import android.graphics.drawable.Drawable
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.databinding.ItemDownloadBinding
import com.hasan.youtubedownloader.ui.models.ItemDownload
import com.hasan.youtubedownloader.ui.screens.PlayFragment

class HomeAdapter(var applicationList:ArrayList<ItemDownload>, val itemClick :(video:ItemDownload,image:View) -> Unit): RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    inner class HomeViewHolder( var binding: ItemDownloadBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(download:ItemDownload){

            Glide
                .with(binding.root.context)
                .load(download.image)
                .centerCrop()
                .placeholder(R.drawable.video)
                .into(binding.ivVideo)

            //binding.ivApplication.setImageResource(application.image)

            ViewCompat.setTransitionName(binding.ivVideo,"item_image")

            binding.video.setOnClickListener {
                itemClick.invoke(download,binding.ivVideo)
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