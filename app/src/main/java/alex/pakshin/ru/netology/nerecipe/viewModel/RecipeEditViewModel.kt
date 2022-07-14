package alex.pakshin.ru.netology.nerecipe.viewModel

import alex.pakshin.ru.netology.nerecipe.adapter.RecipeEditInteractionListener
import alex.pakshin.ru.netology.nerecipe.dataModels.Recipe
import alex.pakshin.ru.netology.nerecipe.dataModels.RecipeStep
import alex.pakshin.ru.netology.nerecipe.db.AppDb
import alex.pakshin.ru.netology.nerecipe.repository.RecipeRepository
import alex.pakshin.ru.netology.nerecipe.repository.RecipeRepositoryImpl
import alex.pakshin.ru.netology.nerecipe.ui.main.RecipeContentFragment
import alex.pakshin.ru.netology.nerecipe.utils.SingleLiveEvent
import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData


class RecipeEditViewModel(
    application: Application
) : AndroidViewModel(application), RecipeEditInteractionListener {

    private val repository: RecipeRepository = RecipeRepositoryImpl(
        recipeDao = AppDb.getInstance(context = application).recipeDao
    )


    val data by repository::data

    val steps = MutableLiveData<List<RecipeStep>>(null)

    val selectImageEvent = SingleLiveEvent<Int>()

    private var currentRecipe: Recipe? = null

    private var currentPosition: Int? = null


    override fun onImageSelectClicked(position: Int) {
        currentPosition = position
        selectImageEvent.call()
    }

    fun onSaveButtonClicked(
        title: String,
        author: String,
        category: Recipe.Companion.RecipeCategory?
    ): Boolean {
        if (title.isBlank() || author.isBlank()) {
            Toast.makeText(getApplication(), "Recipe has empty fields!", Toast.LENGTH_LONG).show()
            return false
        }

        if (category == null) {
            Toast.makeText(getApplication(), "Choose category", Toast.LENGTH_LONG).show()
            return false
        }

        if (!checkStepsFieldsNotNull()) {
            Toast.makeText(
                getApplication(),
                "Steps has empty fields!",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        val recipe = currentRecipe?.copy(
            title = title,
            author = author,
            category = category
        ) ?: Recipe(RecipeContentFragment.NEW_RECIPE_ID, title, author, category)
        println(recipe)
        repository.save(recipe)
        println(steps.value)
        repository
        steps.value!!.forEach {
           // it.id = 0
            repository.save(recipe.id, it)
        }

        currentRecipe = null
        steps.value = emptyList()
        currentPosition = null

        return true
    }

    fun onAddNewStepClicked(globalId: Long, recipeId: Long, position: Int) {

        val newStep = listOf(
            RecipeStep(
                globalId,
                recipeId,
                position,
                null,
                null
            )
        )

        steps.value = steps.value?.plus(newStep) ?: newStep
    }

    private fun checkStepsFieldsNotNull(): Boolean {
        var result = true
        if (steps.value.isNullOrEmpty()) result = false
        else {
            steps.value!!.forEach { if (it.title == null || it.text == null) result = false }
        }
        return result
    }

    override fun onSwipe(position: Int) {
        if (steps.value?.count()!! > 1) {
            if (currentRecipe != null)
                repository.deleteStep(steps.value?.find { it.step == position }?.id!!)

            steps.value = steps.value?.filter { it.step != position }
                ?.map { if (it.step > position) it.copy(step = it.step - 1) else it }
        } else
            Toast.makeText(getApplication(), "Can't delete last step!", Toast.LENGTH_LONG).show()

    }

    override fun onDrag(steps: List<RecipeStep>) {
        this.steps.value = steps
    }

    fun setImage(uri: Uri) {

        steps.value =
            steps.value?.map { if (it.step == currentPosition) it.copy(image = uri.toString()) else it }
    }

    fun getSteps(recipeId: Long) {
        steps.value = repository.getSteps(recipeId)
    }

    fun getRecipe(recipeId: Long): Recipe? {
        currentRecipe = data.value?.find { it.id == recipeId }
        println("viewmodel")
        println(recipeId)
        println(currentRecipe)
        return currentRecipe
    }
}