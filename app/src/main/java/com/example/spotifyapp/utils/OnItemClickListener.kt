package com.example.spotifyapp.utils

interface OnItemClickListener<T> {
    fun onItemClick(pos: Int, item: T)
}