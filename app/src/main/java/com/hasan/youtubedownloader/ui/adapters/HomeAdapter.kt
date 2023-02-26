package com.hasan.youtubedownloader.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.layout.Layout
import androidx.recyclerview.widget.RecyclerView
import com.hasan.youtubedownloader.databinding.ItemApplicationBinding
import com.hasan.youtubedownloader.ui.models.ItemApplication

class HomeAdapter(var applicationList:ArrayList<ItemApplication>): RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(private var binding: ItemApplicationBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(application:ItemApplication){
            binding.ivApplication.setImageResource(application.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = ItemApplicationBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        //view.root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int = applicationList.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.onBind(applicationList[position])
    }

}