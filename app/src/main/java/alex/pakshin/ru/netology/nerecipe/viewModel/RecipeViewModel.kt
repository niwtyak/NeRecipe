package alex.pakshin.ru.netology.nerecipe.viewModel

import alex.pakshin.ru.netology.nerecipe.adapter.RecipeInteractionListener
import alex.pakshin.ru.netology.nerecipe.dataModels.Recipe
import alex.pakshin.ru.netology.nerecipe.db.AppDb
import alex.pakshin.ru.netology.nerecipe.repository.RecipeRepository
import alex.pakshin.ru.netology.nerecipe.repository.RecipeRepositoryImpl
import alex.pakshin.ru.netology.nerecipe.ui.main.RecipeContentFragment
import alex.pakshin.ru.netology.nerecipe.utils.SingleLiveEvent
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

class RecipeViewModel(
    application: Application
) : AndroidViewModel(application), RecipeInteractionListener {

    private val repository: RecipeRepository = RecipeRepositoryImpl(
        recipeDao = AppDb.getInstance(context = application).recipeDao
    )

    val data by repository::data

    val searchQuery = MutableLiveData("")

    val categoryList = MutableLiveData(Recipe.Companion.RecipeCategory.values().map { it.categoryName })

    val navigateRecipeContentScreenEvent = SingleLiveEvent<Long>()

    val navigateRecipeDetailsScreenEvent = SingleLiveEvent<Long>()

    val favoriteTab=MutableLiveData(false)

    var filteredData = Transformations.distinctUntilChanged(data) as MutableLiveData<List<Recipe>>

    fun setDefaultData() {
        filteredData.value = data.value
    }

    fun setQuery(query: String) {
        searchQuery.value = query
    }

    fun setCategoryList(categories:List<String>){
        categoryList.value=categories
    }

    fun filterFavorites(index: Int) {
       favoriteTab.value = index == 1
    }

    override fun onRecipeClicked(recipe: Recipe) {
        navigateRecipeDetailsScreenEvent.value = recipe.id
    }

    override fun onRemoveClicked(recipe: Recipe) = repository.delete(recipe.id)

    override fun onEditClicked(recipe: Recipe) {
        navigateRecipeContentScreenEvent.value = recipe.id
    }

    override fun onFavoriteClicked(recipe: Recipe) = repository.favorite(recipe.id)

    fun onAddButtonClicked() {
        navigateRecipeContentScreenEvent.value = RecipeContentFragment.NEW_RECIPE_ID
    }

    fun getSteps(recipeId: Long) = repository.getSteps(recipeId)

    fun filterList(
        query: String? = searchQuery.value,
        categoriesList: List<String>? = categoryList.value,
        favorite:Boolean = favoriteTab.value == true) {
        filteredData.value = repository.search(if (query.isNullOrBlank()) "%%" else query,categoriesList,favorite)

        println("query $query favorite $favorite")
        println(categoriesList)
        println(filteredData.value)
    }


    override fun onDrag(firstRecipe: Recipe, secondRecipe: Recipe) = repository.swapPositions(firstRecipe,secondRecipe)

}