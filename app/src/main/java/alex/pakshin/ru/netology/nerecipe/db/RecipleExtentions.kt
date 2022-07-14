package alex.pakshin.ru.netology.nerecipe.db

import alex.pakshin.ru.netology.nerecipe.dataModels.Recipe
import alex.pakshin.ru.netology.nerecipe.dataModels.RecipeStep

fun RecipeEntity.toRecipe() = Recipe(
    id = id,
    title = title,
    author = author,
    category = Recipe.Companion.RecipeCategory.values().find { it.categoryName == category }
        ?: throw RuntimeException("No such category"),
    favorite = favorite
)

fun Recipe.toEntity() = RecipeEntity(
    id = id,
    title = title,
    author = author,
    category = category.categoryName,
    favorite = favorite
)

fun RecipeStepEntity.toRecipeStep()=RecipeStep(
    id= id,
    recipeId= recipeId,
    step=step,
    title=title,
    text=text,
    image=image
)

fun RecipeStep.toEntity() = RecipeStepEntity(
    id= id,
    recipeId= recipeId,
    step=step,
    title=title?:"",
    text=text?:"",
    image = image
)