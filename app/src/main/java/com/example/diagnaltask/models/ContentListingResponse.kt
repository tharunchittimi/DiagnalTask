package com.example.diagnaltask.models


import com.google.gson.annotations.SerializedName

data class ContentListingResponse(
    @SerializedName("page")
    val page: Page?
) {

    data class Page(
        @SerializedName("content-items")
        val contentItems: ContentItems?,
        @SerializedName("page-num")
        val pageNum: String?,
        @SerializedName("page-size")
        val pageSize: String?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("total-content-items")
        val totalContentItems: Int?
    ) {
        data class ContentItems(
            @SerializedName("content")
            val content: ArrayList<Content?>?
        ) {
            data class Content(
                @SerializedName("name")
                val name: String?,
                @SerializedName("poster-image")
                val posterImage: String?
            )
        }
    }
}