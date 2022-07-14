package alex.pakshin.ru.netology.nerecipe.ui.main

import alex.pakshin.ru.netology.nerecipe.R
import alex.pakshin.ru.netology.nerecipe.adapter.RecipeStepsAdapter
import alex.pakshin.ru.netology.nerecipe.databinding.RecipeDetailsFragmentBinding
import alex.pakshin.ru.netology.nerecipe.viewModel.RecipeViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class RecipeDetailsFragment : Fragment() {
    private val recipeViewModel: RecipeViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val args by navArgs<RecipeDetailsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeViewModel.navigateRecipeContentScreenEvent.observe(this){ recipeId->
            val direction = RecipeDetailsFragmentDirections.recipeDetailsFragmentToRecipeContentFragment(recipeId)
            findNavController().navigate(direction)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RecipeDetailsFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        recipeViewModel.data.observe(viewLifecycleOwner) { recipes ->
            println(args.recipeId)
            println(recipes)
            val recipe = recipes.find { it.id == args.recipeId } ?: return@observe

            val popupMenu by lazy {
                PopupMenu(requireContext(), binding.recipeDetails.options).apply {

                    inflate(R.menu.options)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.remove -> {
                                recipeViewModel.onRemoveClicked(recipe)
                                findNavController().popBackStack()
                                true
                            }
                            R.id.edit -> {
                                recipeViewModel.onEditClicked(recipe)
                                true
                            }
                            else -> false
                        }
                    }
                    setOnDismissListener { binding.recipeDetails.options.isChecked = false }
                }
            }

            with(binding.recipeDetails) {
                recipeTitle.text = recipe.title
                recipeAuthor.text = recipe.author
                favorite.isChecked = recipe.favorite

                options.setOnClickListener {
                    popupMenu.show()
                }
                favorite.setOnClickListener {
                    recipeViewModel.onFavoriteClicked(recipe)
                }
                recipeCard.setOnClickListener {
                    recipeViewModel.onRecipeClicked(recipe)
                }
            }

            val stepsAdapter = RecipeStepsAdapter()
            binding.stepsRecycleView.adapter =stepsAdapter
            stepsAdapter.submitList(recipeViewModel.getSteps(args.recipeId))
        }
    }.root
}