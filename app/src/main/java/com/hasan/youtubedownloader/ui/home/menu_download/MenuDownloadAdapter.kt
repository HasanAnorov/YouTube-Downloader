package com.hasan.youtubedownloader.ui.home.menu_download

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hasan.youtubedownloader.databinding.DropdownItemBinding
import com.yausername.youtubedl_android.mapper.VideoFormat

class MenuDownLoadAdapter(
    private val videoFormats: List<String>,
    private val itemClick: (videoFormat: String) -> Unit,
) : RecyclerView.Adapter<MenuDownLoadAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(private val binding: DropdownItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(videoFormat : String) {

            binding.tvVideoFormat.text = videoFormat
            binding.cardDownload.setOnClickListener {
                itemClick.invoke(videoFormat)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = DropdownItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int = videoFormats.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.onBind(videoFormats[position])
    }

}