package alex.pakshin.ru.netology.nerecipe.dataModels

import alex.pakshin.ru.netology.nerecipe.utils.App
import alex.pakshin.ru.netology.nerecipe.R


data class Recipe (
val id:Long,
val title:String,
val author: String,
val category:RecipeCategory,
val favorite:Boolean=false
){
    companion object{
        enum class RecipeCategory(val categoryName:String,val localizedName:String){
            European("European", App.getAppResources().getString(R.string.category_european)),
            Asian("Asian",
                App.getAppResources().getString(R.string.category_asian)),
            PanAsian("PanAsian",
                App.getAppResources().getString(R.string.category_pan_asian)),
            Eastern("Eastern",
                App.getAppResources().getString(R.string.category_eastern)),
            American("American",
                App.getAppResources().getString(R.string.category_american)),
            Russian("Russian",
                App.getAppResources().getString(R.string.category_russian)),
            Mediterranean("Mediterranean",
                App.getAppResources().getString(R.string.category_mediterranean))
        }
    }
}