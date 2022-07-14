package alex.pakshin.ru.netology.nerecipe.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

data class RecipeWithSteps(
    @Embedded val recipe: RecipeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val steps: List<RecipeStepEntity>
)