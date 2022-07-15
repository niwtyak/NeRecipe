package alex.pakshin.ru.netology.nerecipe.ui.main

import alex.pakshin.ru.netology.nerecipe.R
import alex.pakshin.ru.netology.nerecipe.adapter.RecipeFeedAdapter
import alex.pakshin.ru.netology.nerecipe.dataModels.Recipe
import alex.pakshin.ru.netology.nerecipe.databinding.FeedFragmentBinding
import alex.pakshin.ru.netology.nerecipe.utils.RecipeTouchCallback
import alex.pakshin.ru.netology.nerecipe.viewModel.RecipeViewModel
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout

class FeedFragment : Fragment(), SearchView.OnQueryTextListener {

    private val recipeViewModel: RecipeViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private lateinit var binding :FeedFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeViewModel.navigateRecipeContentScreenEvent.observe(this) { recipeId ->
            val direction = FeedFragmentDirections.toRecipeContentFragment(recipeId)
            findNavController().navigate(direction)
        }

        recipeViewModel.navigateRecipeDetailsScreenEvent.observe(this) { recipeId ->
            val direction = FeedFragmentDirections.toRecipeDetailsFragment(recipeId)
            findNavController().navigate(direction)
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)
                val search = menu.findItem(R.id.searchItem)
                val searchView = search.actionView as SearchView
                searchView.isSubmitButtonEnabled = true
                searchView.setOnQueryTextListener(this@FeedFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.filterButton) {

                    if (menuItem.isChecked) binding.filterGroup.visibility = ViewGroup.VISIBLE
                    else binding.filterGroup.visibility = ViewGroup.GONE

                    menuItem.isChecked = ! menuItem.isChecked
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FeedFragmentBinding.inflate(inflater, container, false).also { binding ->
            val tabs: TabLayout = binding.tabs
            tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab != null) {
                        recipeViewModel.filterFavorites(tab.position)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })

            binding.fab.setOnClickListener {
                recipeViewModel.onAddButtonClicked()
            }

           binding.filterGroup.forEach { child ->
                (child as? Chip)?.setOnCheckedChangeListener { _, _ ->
                    val ids = binding.filterGroup.checkedChipIds

                    val categories = mutableListOf<String>()

                    ids.forEach { id ->
                        categories.add(binding.filterGroup.findViewById<Chip>(id).text as String)
                    }

                    val categoryNames=categories.map {name->
                        Recipe.Companion.RecipeCategory.values().find { it.localizedName == name }?.categoryName ?: throw throw RuntimeException("No such category")
                    }

                    recipeViewModel.setCategoryList(categoryNames)

                }
            }

            val adapter = RecipeFeedAdapter(recipeViewModel)
            binding.recipeRecycleView.adapter = adapter

            val itemTouchHelper = ItemTouchHelper(RecipeTouchCallback(recipeViewModel))
            itemTouchHelper.attachToRecyclerView(binding.recipeRecycleView)

            recipeViewModel.filteredData.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                println(it)
            }

            recipeViewModel.favoriteTab.observe(viewLifecycleOwner) {
                recipeViewModel.filterList(favorite = it)
            }

            recipeViewModel.searchQuery.observe(viewLifecycleOwner) {
                recipeViewModel.filterList(query = it)
            }

            recipeViewModel.categoryList.observe(viewLifecycleOwner){
                recipeViewModel.filterList(categoriesList = it)
            }

        }
        return binding.root
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }


    override fun onQueryTextChange(query: String?): Boolean {
        if (query.isNullOrBlank()) recipeViewModel.setDefaultData()
        else {
            recipeViewModel.setQuery("%$query%")
        }
        return true
    }
}