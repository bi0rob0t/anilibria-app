package ru.radiationx.anilibria.screen.search.year

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.radiationx.anilibria.common.fragment.GuidedRouter
import ru.radiationx.anilibria.screen.search.BaseSearchValuesViewModel
import ru.radiationx.anilibria.screen.search.SearchController
import ru.radiationx.data.entity.app.release.YearItem
import ru.radiationx.data.repository.SearchRepository
import timber.log.Timber
import toothpick.InjectConstructor

@InjectConstructor
class SearchYearViewModel(
    private val searchRepository: SearchRepository,
    private val searchController: SearchController,
    private val guidedRouter: GuidedRouter
) : BaseSearchValuesViewModel() {

    private val currentYears = mutableListOf<YearItem>()

    override fun onColdCreate() {
        super.onColdCreate()
        searchRepository
            .observeYears()
            .onEach {
                currentYears.clear()
                currentYears.addAll(it)
                currentValues.clear()
                currentValues.addAll(it.map { it.value })
                valuesData.value = it.map { it.title }
                progressState.value = false
                updateChecked()
                updateSelected()
            }
            .launchIn(viewModelScope)
    }

    override fun onCreate() {
        super.onCreate()
        viewModelScope.launch {
            progressState.value = true
            runCatching {
                searchRepository.getYears()
            }.onFailure {
                Timber.e(it)
            }
            progressState.value = false
        }

    }

    override fun applyValues() {
        viewModelScope.launch {
            val newYears = currentYears.filterIndexed { index, item ->
                checkedValues.contains(item.value)
            }
            searchController.yearsEvent.emit(newYears)
            guidedRouter.close()
        }
    }
}