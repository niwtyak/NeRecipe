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

    val searchQuery = MutableLiveData<String>()

    val navigateRecipeContentScreenEvent = SingleLiveEvent<Long>()

    val navigateRecipeDetailsScreenEvent = SingleLiveEvent<Long>()

    // val selectItemEvent = SingleLiveEvent<Int>()

    // var currentPosition = MutableLiveData<Int?>(null)

    var filteredData = Transformations.distinctUntilChanged(data) as MutableLiveData<List<Recipe>>

    private fun setDefaultData() {
        filteredData.value = data.value
    }

    fun setQuery(query: String) {
        this.searchQuery.value = query
    }

    fun filterFavorites(index: Int) {
        if (index == 1) {
            filteredData.value = data.value?.filter { it.favorite }
        } else
            setDefaultData()

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

    fun filterList(query: String) {
        filteredData.value = repository.search(query).value
    }
}