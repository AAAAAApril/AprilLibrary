package com.april.library.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.april.library.R

class NavHomeFragment : AbsNavFragment() {
    override fun onCreateViewFirst(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.nav_fragment_home, container, false)
}