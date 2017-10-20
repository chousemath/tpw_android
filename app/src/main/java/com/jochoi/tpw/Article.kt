package com.jochoi.tpw

import org.json.JSONArray

/**
 * Created by jo on 10/20/17.
 */
data class Article(val title: String, val description: String, val titleKorean: String, val descriptionKorean: String, val downloadLink: String, val cardTags: JSONArray)