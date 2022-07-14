package alex.pakshin.ru.netology.nerecipe.ui.main

import alex.pakshin.ru.netology.nerecipe.R
import alex.pakshin.ru.netology.nerecipe.adapter.RecipeFeedAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import alex.pakshin.ru.netology.nerecipe.databinding.FeedFragmentBinding
import alex.pakshin.ru.netology.nerecipe.viewModel.RecipeViewModel
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout

class FeedFragment : Fragment(), MenuProvider,SearchView.OnQueryTextListener {

    private val recipeViewModel:RecipeViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeViewModel.navigateRecipeContentScreenEvent.observe(this){ recipeId->
            val direction = FeedFragmentDirections.toRecipeContentFragment(recipeId)
            findNavController().navigate(direction)
        }

        recipeViewModel.navigateRecipeDetailsScreenEvent.observe(this){ recipeId->
            val direction = FeedFragmentDirections.toRecipeDetailsFragment(recipeId)
            findNavController().navigate(direction)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FeedFragmentBinding.inflate(inflater, container, false).also {binding->

        val tabs: TabLayout = binding.tabs
        tabs.addOnTabSelectedListener( object :TabLayout.OnTabSelectedListener{
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

        val adapter = RecipeFeedAdapter(recipeViewModel)
        binding.recipeRecycleView.adapter = adapter


        recipeViewModel.filteredData.observe(viewLifecycleOwner){
            adapter.submitList(it)
            println(it)
        }

        recipeViewModel.searchQuery.observe(viewLifecycleOwner){
            recipeViewModel.filterList(it)
        }


    }.root

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu,menu)

        val search = menu.findItem(R.id.searchItem)
        val searchView = search.actionView as SearchView
        searchView.isSubmitButtonEnabled =true
        searchView.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
      return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query!=null){
            recipeViewModel.setQuery("%$query%")
        }
        return true
    }

}