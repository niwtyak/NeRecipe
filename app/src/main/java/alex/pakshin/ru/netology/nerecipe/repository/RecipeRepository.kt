package alex.pakshin.ru.netology.nerecipe.repository

import alex.pakshin.ru.netology.nerecipe.dataModels.Recipe
import alex.pakshin.ru.netology.nerecipe.dataModels.RecipeStep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Query

interface RecipeRepository {

    val data:LiveData<List<Recipe>>

    fun getSteps(recipeId:Long):List<RecipeStep>

    fun save(recipe: Recipe)

    fun save(recipeId:Long,recipeStep: RecipeStep)

    fun delete(recipeId: Long)

    fun favorite(recipeId: Long)

    fun deleteStep(stepId:Long)

    fun search(searchQuery:String):LiveData<List<Recipe>>

}