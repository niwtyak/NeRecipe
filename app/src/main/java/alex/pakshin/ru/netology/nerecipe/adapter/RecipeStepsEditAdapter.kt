package alex.pakshin.ru.netology.nerecipe.adapter

import alex.pakshin.ru.netology.nerecipe.R
import alex.pakshin.ru.netology.nerecipe.dataModels.RecipeStep
import alex.pakshin.ru.netology.nerecipe.databinding.RecipeEditStepViewBinding
import alex.pakshin.ru.netology.nerecipe.utils.App
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


class RecipeStepsEditAdapter(
    private val interactionListener: RecipeEditInteractionListener
) : ListAdapter<RecipeStep, RecipeStepsEditAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(
        private val binding: RecipeEditStepViewBinding,
        private val listener: RecipeEditInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imageSelectButton.setOnClickListener {
                listener.onImageSelectClicked(adapterPosition)
            }
        }

        fun bind(recipeStep: RecipeStep) {
            with(binding) {
                stepNumber.text =
                    App.getAppResources().getString(R.string.step_number, recipeStep.step + 1)

                stepTitleEdit.setText(recipeStep.title)

                binding.stepTitleEdit.addTextChangedListener {
                    recipeStep.title = it.toString()
                }
                stepTextEdit.setText(recipeStep.text)
                stepTextEdit.addTextChangedListener {
                    recipeStep.text = it.toString()
                }
                if (recipeStep.image != null) stepImage.setImageURI(Uri.parse(recipeStep.image))
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeStepsEditAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeEditStepViewBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: RecipeStepsEditAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object DiffCallback : DiffUtil.ItemCallback<RecipeStep>() {
        override fun areItemsTheSame(oldItem: RecipeStep, newItem: RecipeStep): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: RecipeStep, newItem: RecipeStep): Boolean =
            oldItem == newItem
    }


}