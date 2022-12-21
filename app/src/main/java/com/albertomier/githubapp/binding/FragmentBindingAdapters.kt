package com.albertomier.githubapp.binding

import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import javax.inject.Inject

class FragmentBindingAdapters @Inject constructor(val fragment: Fragment) {

    @BindingAdapter("imageUrl")
    fun bindImage(imageView: ImageView, url: String?) {
        Glide.with(fragment).load(url).into(imageView)
    }
}