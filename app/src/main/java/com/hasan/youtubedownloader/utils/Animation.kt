package com.hasan.youtubedownloader.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.graphics.Outline
import android.transition.Transition
import android.transition.TransitionValues
import android.util.Property
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView

class ChangeOutlineRadiusTransition(
    private val startRadius: Int,
    private val endRadius: Int
) : Transition() {

    private companion object {
        /**
         * Unique key for start and end values to be kept in [TransitionValues] [TransitionValues]
         */
        private const val RADIUS = "ChangeOutlineRadiusTransition:outlineRadius"

        /**
         * The properties from [TransitionValues]
         */
        private val PROPERTIES = arrayOf(RADIUS)

        /**
         * Animator property which will set a rounded outline through a [ViewOutlineProvider]
         */
        private object OutlineRadiusProperty : Property<View, Int>(Int::class.java, "outlineRadius") {
            var offset = 0

            override fun get(view: View) = 0

            override fun set(view: View, value: Int) {
                view.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setRoundRect(0, 0, view.width, view.height + offset, value.toFloat())
                    }
                }
            }
        }
    }

    override fun getTransitionProperties(): Array<String> {
        return PROPERTIES
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        if (view !is ImageView || view.getVisibility() != View.VISIBLE) {
            return
        }
        transitionValues.values[RADIUS] = startRadius
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        if (view !is ImageView || view.getVisibility() != View.VISIBLE) {
            return
        }
        transitionValues.values[RADIUS] = endRadius
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?, endValues: TransitionValues?): Animator {
        val view = endValues?.view
        view?.clipToOutline = true

        val startRadius = startValues?.values?.get(RADIUS) as? Int ?: 0
        val endRadius = endValues?.values?.get(RADIUS) as? Int ?: 0

        val property = OutlineRadiusProperty.apply { offset = if (startRadius > 0) startRadius else endRadius }
        return ObjectAnimator.ofInt(view, property, startRadius, endRadius)
    }
}