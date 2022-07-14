package alex.pakshin.ru.netology.nerecipe.adapter

import alex.pakshin.ru.netology.nerecipe.R
import alex.pakshin.ru.netology.nerecipe.dataModels.Recipe
import alex.pakshin.ru.netology.nerecipe.databinding.RecipeCardBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class RecipeFeedAdapter(
    private val interactionListener: RecipeInteractionListener
) : ListAdapter<Recipe, RecipeFeedAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(
        private val binding: RecipeCardBinding,
        listener: RecipeInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var recipe: Recipe

        private val popupMenu by lazy {
            PopupMenu(itemView.context, binding.options).apply {

                inflate(R.menu.options)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.remove -> {
                            listener.onRemoveClicked(recipe)
                            true
                        }
                        R.id.edit -> {
                            listener.onEditClicked(recipe)
                            true
                        }
                        else -> false
                    }
                }
                setOnDismissListener { binding.options.isChecked = false }
            }
        }

        init {
            with(binding) {
                options.setOnClickListener {
                    popupMenu.show()
                }
                favorite.setOnClickListener {
                    listener.onFavoriteClicked(recipe)
                }
                recipeCard.setOnClickListener {
                    listener.onRecipeClicked(recipe)
                }
            }
        }

        fun bind(recipe: Recipe) {
            this.recipe = recipe
            with(binding) {
                recipeTitle.text = recipe.title
                recipeAuthor.text = recipe.author
                favorite.isChecked = recipe.favorite
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeCardBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(getItem(position))
    }

    private object DiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
            oldItem == newItem
    }
}