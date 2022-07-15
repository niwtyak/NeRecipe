package alex.pakshin.ru.netology.nerecipe.adapter

import alex.pakshin.ru.netology.nerecipe.dataModels.Recipe

interface RecipeInteractionListener{
    fun onRecipeClicked(recipe: Recipe)
    fun onRemoveClicked(recipe: Recipe)
    fun onEditClicked(recipe: Recipe)
    fun onFavoriteClicked(recipe: Recipe)
    fun onDrag(firstRecipe:Recipe,secondRecipe:Recipe)
}