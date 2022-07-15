package alex.pakshin.ru.netology.nerecipe.repository

import alex.pakshin.ru.netology.nerecipe.dataModels.Recipe
import alex.pakshin.ru.netology.nerecipe.dataModels.RecipeStep
import alex.pakshin.ru.netology.nerecipe.db.RecipeDao
import alex.pakshin.ru.netology.nerecipe.db.toEntity
import alex.pakshin.ru.netology.nerecipe.db.toRecipe
import alex.pakshin.ru.netology.nerecipe.db.toRecipeStep
import alex.pakshin.ru.netology.nerecipe.ui.main.RecipeContentFragment
import androidx.lifecycle.map

class RecipeRepositoryImpl(
    private val recipeDao: RecipeDao
) : RecipeRepository {

    override val data = recipeDao.getAll().map { entities ->
        entities.map { it.toRecipe() }
    }


    override fun save(recipe: Recipe) {
        if (recipe.id == RecipeContentFragment.NEW_RECIPE_ID) recipeDao.insertRecipe(recipe.toEntity())
        else recipeDao.updateContentById(
            recipe.id,
            recipe.title,
            recipe.author,
            recipe.category.categoryName,
            recipe.position
        )
    }

    override fun save(recipeId: Long, recipeStep: RecipeStep) {
        recipeDao.insertStep(recipeStep.toEntity())
        /*if (recipeId == RecipeContentFragment.NEW_RECIPE_ID) recipeDao.insertStep(recipeStep.toEntity())
        else {
            recipeDao.updateStepById(recipeId,recipeStep.id, recipeStep.title!!,recipeStep.text!!,recipeStep.step,recipeStep.image)
            println()
        }*/
    }

    override fun getSteps(recipeId: Long) =
        recipeDao.getRecipeWithSteps(recipeId).steps.map { it.toRecipeStep() }.sortedBy { it.step }

    override fun delete(recipeId: Long) = recipeDao.removeById(recipeId)

    override fun deleteStep(stepId: Long) = recipeDao.removeStepById(stepId)

    override fun search(searchQuery: String?,categoriesList:List<String>?, favorite: Boolean) =
        (if (favorite) recipeDao.searchFavorites(searchQuery,categoriesList,favorite)
        else recipeDao.search(searchQuery,categoriesList)).map { entities ->
            entities.toRecipe()
        }

    override fun swapPositions(first: Recipe, second: Recipe) {
        recipeDao.updateContentById(first.id,first.title,first.author,first.category.categoryName,second.position)
        recipeDao.updateContentById(second.id,second.title,second.author,second.category.categoryName,first.position)
    }

    override fun favorite(recipeId: Long) = recipeDao.favoritedById(recipeId)
}