package ru.radiationx.anilibria.screen.search.sort

import androidx.lifecycle.MutableLiveData
import ru.radiationx.anilibria.common.fragment.GuidedRouter
import ru.radiationx.anilibria.screen.LifecycleViewModel
import ru.radiationx.anilibria.screen.search.SearchController
import ru.radiationx.data.entity.domain.search.SearchForm
import toothpick.InjectConstructor

@InjectConstructor
class SearchSortViewModel(
    private val argExtra: SearchSortExtra,
    private val searchController: SearchController,
    private val guidedRouter: GuidedRouter
) : LifecycleViewModel() {

    private val titles = listOf(
        "По популярности",
        "По новизне"
    )

    val titlesData = MutableLiveData<List<String>>()
    val selectedIndex = MutableLiveData<Int>()

    init {
        titlesData.value = titles
        selectedIndex.value = when (argExtra.sort) {
            SearchForm.Sort.RATING -> 0
            SearchForm.Sort.DATE -> 1
        }
    }

    fun applySort(index: Int) {
        guidedRouter.close()
        val sort = when (index) {
            0 -> SearchForm.Sort.RATING
            1 -> SearchForm.Sort.DATE
            else -> null
        }
        sort?.also {
            searchController.sortEvent.emit(it)
        }
    }
}