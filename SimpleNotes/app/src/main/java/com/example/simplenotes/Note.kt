package com.example.simplenotes

data class Note(
    var Title: String? =null,
    var Content: String?= null,
    var Category:String?=null,
    var LastModified: String?=null
)