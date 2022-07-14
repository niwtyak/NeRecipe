package alex.pakshin.ru.netology.nerecipe.ui.main

import alex.pakshin.ru.netology.nerecipe.R
import alex.pakshin.ru.netology.nerecipe.adapter.RecipeStepsEditAdapter
import alex.pakshin.ru.netology.nerecipe.dataModels.Recipe
import alex.pakshin.ru.netology.nerecipe.databinding.RecipeContentFragmentBinding
import alex.pakshin.ru.netology.nerecipe.utils.ItemTouchCallback
import alex.pakshin.ru.netology.nerecipe.viewModel.RecipeEditViewModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.launch
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import kotlin.properties.Delegates


class RecipeContentFragment : Fragment() {

    private val recipeViewModel: RecipeEditViewModel by viewModels()
    private val args by navArgs<RecipeContentFragmentArgs>()
    private val recipeId by lazy { args.recipeId }

    private val prefs by lazy {
        requireContext().getSharedPreferences(
            "recipeIds", Context.MODE_PRIVATE
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RecipeContentFragmentBinding.inflate(layoutInflater, container, false).also { binding ->

        val items = Recipe.Companion.RecipeCategory.values().map { it.localizedName }
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        binding.categoryItem.setAdapter(arrayAdapter)

        val recycleAdapter = RecipeStepsEditAdapter(recipeViewModel)
        binding.recycleRecipeEdit.adapter = recycleAdapter

        val itemTouchHelper = ItemTouchHelper(ItemTouchCallback(recipeViewModel))
        itemTouchHelper.attachToRecyclerView(binding.recycleRecipeEdit)

        recipeViewModel.steps.observe(viewLifecycleOwner) { steps ->
            recycleAdapter.submitList(steps)
        }

        var newRecipeId: Long by Delegates.observable(
            prefs.getLong(NEXT_ID_RECIPE_KEY, 1L)
        ) { _, _, newValue ->
            prefs.edit { putLong(NEXT_ID_RECIPE_KEY, newValue) }
        }

        recipeViewModel.data.observe(viewLifecycleOwner) {
            val recipe = recipeViewModel.getRecipe(recipeId)
            println("fragment")
            println(args.recipeId)
            println(recipe)

            if (recipe != null) {
                with(binding) {
                    recipeTitleEdit.setText(recipe.title)
                    recipeAuthorEdit.setText(recipe.author)
                    categoryItem.setText(recipe.category.localizedName, false)
                    recipeViewModel.getSteps(recipeId)
                }
            } else {
                binding.newStepButton.callOnClick()
            }
        }

        binding.newStepButton.setOnClickListener {
            recipeViewModel.onAddNewStepClicked(
                newStepId++,
                if (recipeId != NEW_RECIPE_ID) recipeId else newRecipeId,
                recycleAdapter.itemCount
            )
        }

        binding.saveButton.setOnClickListener {
            val category = Recipe.Companion.RecipeCategory.values()
                .find { it.localizedName == binding.categoryItem.text.toString() }
            if (
                recipeViewModel.onSaveButtonClicked(
                    binding.recipeTitleEdit.text.toString(),
                    binding.recipeAuthorEdit.text.toString(),
                    category
                )
            ) {
                if (recipeId == NEW_RECIPE_ID) newRecipeId++
                findNavController().popBackStack()
            }
        }

        val selectImageActivityLauncher = registerForActivityResult(
            ResultContract
        ) { uri ->
            uri ?: return@registerForActivityResult
            recipeViewModel.setImage(uri)
        }

        recipeViewModel.selectImageEvent.observe(viewLifecycleOwner) {
            binding.recycleRecipeEdit.clearFocus()
            selectImageActivityLauncher.launch()
        }


    }.root

    object ResultContract : ActivityResultContract<Unit, Uri?>() {
        override fun createIntent(context: Context, input: Unit) =
            Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )

        override fun parseResult(resultCode: Int, intent: Intent?) =
            if (resultCode == Activity.RESULT_OK) {
                intent?.data
            } else null

    }


    companion object {
        var newStepId: Long = 0
        const val NEXT_ID_RECIPE_KEY = "nextRecipeId"
        const val NEW_RECIPE_ID = 0L
    }
}