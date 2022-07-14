package alex.pakshin.ru.netology.nerecipe.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY id")
    fun getAll(): LiveData<List<RecipeEntity>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId ")
    fun getRecipeWithSteps(recipeId: Long): RecipeWithSteps

    @Query("UPDATE recipes SET title = :title,author = :author, category = :category WHERE id = :id")
    fun updateContentById(id: Long, title: String, author: String, category: String)

    @Insert
    fun insertRecipe(post: RecipeEntity)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStep(recipeStep: RecipeStepEntity)

    @Query("UPDATE recipeSteps SET title = :title, text = :text, step=:step, image = :image WHERE id = :id and recipeId=:recipeId")
    fun updateStepById(recipeId: Long, id: Long, title: String, text: String, step: Int, image: String?)

    @Query(
        """
            UPDATE recipes SET
                favorite = CASE WHEN favorite THEN 0 ELSE 1 END
                WHERE id = :id
        """
    )
    fun favoritedById(id: Long)

    @Query("SELECT * FROM recipes WHERE title LIKE :searchQuery or author LIKE :searchQuery")
    fun search(searchQuery: String): LiveData<List<RecipeEntity>>

    @Query("DELETE FROM recipes WHERE id = :id")
    fun removeById(id: Long)

    @Query("DELETE FROM recipeSteps WHERE id = :id")
    fun removeStepById(id: Long)

}