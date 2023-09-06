package eu.on.screen.model

data class ListItemModel(
    val imageResId: Int,
    val title: String,
    val description: String,
    var isEnabled: Boolean
)
