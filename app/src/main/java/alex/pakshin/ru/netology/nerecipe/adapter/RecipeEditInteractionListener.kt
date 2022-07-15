package alex.pakshin.ru.netology.nerecipe.adapter

import alex.pakshin.ru.netology.nerecipe.dataModels.RecipeStep

interface RecipeEditInteractionListener {
    fun onImageSelectClicked(position:Long)
    fun onSwipe(position: Long)
    fun onDrag(steps:List<RecipeStep>)
}