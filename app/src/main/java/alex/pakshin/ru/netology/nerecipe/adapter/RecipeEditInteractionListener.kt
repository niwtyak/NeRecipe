package alex.pakshin.ru.netology.nerecipe.adapter

import alex.pakshin.ru.netology.nerecipe.dataModels.RecipeStep

interface RecipeEditInteractionListener {
    fun onImageSelectClicked(position:Int)
    fun onSwipe(position: Int)
    fun onDrag(steps:List<RecipeStep>)
}