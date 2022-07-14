package alex.pakshin.ru.netology.nerecipe.adapter

import alex.pakshin.ru.netology.nerecipe.R
import alex.pakshin.ru.netology.nerecipe.dataModels.RecipeStep
import alex.pakshin.ru.netology.nerecipe.databinding.RecipeStepViewBinding
import alex.pakshin.ru.netology.nerecipe.utils.App
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


class RecipeStepsAdapter : ListAdapter<RecipeStep, RecipeStepsAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(
        private val binding: RecipeStepViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipeStep: RecipeStep) {
            with(binding) {
                stepNumber.text =
                    App.getAppResources().getString(R.string.step_number, recipeStep.step + 1)
                stepTitle.text = recipeStep.title
                stepText.text = recipeStep.text
                if (recipeStep.image != null) stepImage.setImageURI(Uri.parse(recipeStep.image))
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeStepsAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeStepViewBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeStepsAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object DiffCallback : DiffUtil.ItemCallback<RecipeStep>() {
        override fun areItemsTheSame(oldItem: RecipeStep, newItem: RecipeStep): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: RecipeStep, newItem: RecipeStep): Boolean =
            oldItem == newItem
    }
}