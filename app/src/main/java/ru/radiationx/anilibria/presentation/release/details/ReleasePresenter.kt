package ru.radiationx.anilibria.presentation.release.details

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import ru.radiationx.anilibria.entity.app.release.ReleaseFull
import ru.radiationx.anilibria.entity.app.release.ReleaseItem
import ru.radiationx.anilibria.model.interactors.ReleaseInteractor
import ru.radiationx.anilibria.model.repository.*
import ru.radiationx.anilibria.presentation.IErrorHandler
import ru.radiationx.anilibria.utils.mvp.BasePresenter
import ru.terrakok.cicerone.Router

/* Created by radiationx on 18.11.17. */
@InjectViewState
class ReleasePresenter(
        private val releaseInteractor: ReleaseInteractor,
        private val historyRepository: HistoryRepository,
        private val router: Router,
        private val errorHandler: IErrorHandler
) : BasePresenter<ReleaseView>(router) {

    private var currentData: ReleaseFull? = null
    var releaseId = -1
    var releaseIdCode: String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        releaseInteractor.getItem(releaseId, releaseIdCode)?.also {
            updateLocalRelease(ReleaseFull(it))
        }
        observeRelease()
        loadRelease()
    }

    private fun loadRelease() {
        releaseInteractor
                .loadRelease(releaseId, releaseIdCode)
                .doOnSubscribe { viewState.setRefreshing(true) }
                .subscribe({
                    viewState.setRefreshing(false)
                    historyRepository.putRelease(it as ReleaseItem)
                }, {
                    viewState.setRefreshing(false)
                    errorHandler.handle(it)
                })
                .addToDisposable()
    }

    private fun observeRelease() {
        Log.e("S_DEF_LOG", "load release $releaseId : $releaseIdCode : $currentData")
        releaseInteractor
                .observeFull(releaseId, releaseIdCode)
                .subscribe({ release ->
                    updateLocalRelease(release)
                    historyRepository.putRelease(release as ReleaseItem)
                }) {
                    errorHandler.handle(it)
                }
                .addToDisposable()
    }

    private fun updateLocalRelease(release: ReleaseFull) {
        currentData = release
        releaseId = release.id
        releaseIdCode = release.code
        viewState.showRelease(release)
    }

    fun onShareClick() {
        Log.e("S_DEF_LOG", "onShareClick $currentData, ${currentData?.link}")
        currentData?.link?.let {
            Log.e("S_DEF_LOG", "onShareClick $it")
            viewState.shareRelease(it)
        }
    }

    fun onCopyLinkClick() {
        Log.e("S_DEF_LOG", "onShareClick $currentData, ${currentData?.link}")
        currentData?.link?.let {
            Log.e("S_DEF_LOG", "onShareClick $it")
            viewState.copyLink(it)
        }
    }

    fun onShortcutAddClick() {
        currentData?.let {
            viewState.addShortCut(it)
        }
    }

}
