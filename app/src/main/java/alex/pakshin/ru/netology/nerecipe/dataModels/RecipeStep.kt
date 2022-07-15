package alex.pakshin.ru.netology.nerecipe.dataModels

data class RecipeStep(
    var id: Long,
    val recipeId: Long,
    var step: Long=1,
    var title: String?,
    var text: String?,
    var image: String? = null
)