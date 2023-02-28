package com.hasan.youtubedownloader.ui.screens

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.databinding.FragmentPlayBinding

class PlayFragment : Fragment() {

    private var _binding : FragmentPlayBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val heroImageView = binding.ivVideo
        ViewCompat.setTransitionName(heroImageView,"hero_image")

        val image = arguments?.getInt("image")

        val animation = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
//        sharedElementReturnTransition = TransitionSet().apply {
//            addTransition(ChangeImageTransform())
//            addTransition(ChangeBounds())
//            addTransition(ChangeTransform())
//            addTransition(ChangeOutlineRadiusTransition(animationRadiusData.startRadius,
//                animationRadiusData.endRadius)) // add this Transition
//        }

        //startPostponedEnterTransition()

        Glide.with(this)
            .load(image)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

            })
            .into(binding.ivVideo)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}