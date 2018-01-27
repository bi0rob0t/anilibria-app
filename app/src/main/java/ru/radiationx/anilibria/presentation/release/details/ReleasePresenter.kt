package ru.radiationx.anilibria.presentation.release.details;

import android.os.Bundle
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import ru.radiationx.anilibria.Screens
import ru.radiationx.anilibria.entity.app.release.ReleaseFull
import ru.radiationx.anilibria.entity.app.release.ReleaseItem
import ru.radiationx.anilibria.entity.app.vital.VitalItem
import ru.radiationx.anilibria.model.data.remote.api.PageApi
import ru.radiationx.anilibria.model.repository.ReleaseRepository
import ru.radiationx.anilibria.model.repository.VitalRepository
import ru.radiationx.anilibria.ui.fragments.release.details.ReleaseFragment
import ru.radiationx.anilibria.ui.fragments.search.SearchFragment
import ru.radiationx.anilibria.utils.mvp.BasePresenter
import ru.terrakok.cicerone.Router
import java.util.regex.Pattern

/* Created by radiationx on 18.11.17. */
@InjectViewState
class ReleasePresenter(
        private val releaseRepository: ReleaseRepository,
        private val router: Router,
        private val vitalRepository: VitalRepository
) : BasePresenter<ReleaseView>(router) {

    companion object {
        private const val START_PAGE = 1
    }

    private var currentPageComment = START_PAGE

    private var currentData: ReleaseFull? = null
    private var releaseId = -1
    private var releaseIdName: String? = null

    fun setCurrentData(item: ReleaseItem) {
        viewState.showRelease(ReleaseFull(item))
    }

    fun setReleaseId(releaseId: Int) {
        this.releaseId = releaseId
    }

    fun setReleaseIdName(releaseIdName: String) {
        this.releaseIdName = releaseIdName
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.e("SUKA", "onFirstViewAttach " + this)
        loadRelease()
        loadVital()
    }

    private fun loadVital() {
        vitalRepository
                .observeByRule(VitalItem.Rule.RELEASE_DETAIL)
                .subscribe {
                    it.filter { it.type == VitalItem.VitalType.CONTENT_ITEM }.let {
                        Log.e("SUKA", "VITAL SET LIST ITEMS ${it.size}")
                        if (it.isNotEmpty()) {
                            viewState.showVitalItems(it)
                        }
                    }
                    router.showSystemMessage("Show vital in REL_LIST: ${it.size}")
                }
                .addToDisposable()
    }

    private fun loadRelease() {
        Log.e("SUKA", "load release $releaseId : $releaseIdName : $currentData")
        val source = when {
            releaseId != -1 -> releaseRepository.getRelease(releaseId)
            releaseIdName != null -> releaseRepository.getRelease(releaseIdName!!)
            else -> return
        }
        source
                .subscribe({ release ->
                    releaseId = release.id
                    releaseIdName = release.idName
                    Log.d("SUKA", "subscribe call show")
                    viewState.setRefreshing(false)
                    viewState.showRelease(release)
                    loadComments(currentPageComment)
                    currentData = release
                }) { throwable ->
                    viewState.setRefreshing(false)
                    Log.d("SUKA", "SAS")
                    throwable.printStackTrace()
                }
                .addToDisposable()
    }

    private fun loadComments(page: Int) {
        currentPageComment = page
        releaseRepository
                .getComments(releaseId, currentPageComment)
                .subscribe({ comments ->
                    viewState.setEndlessComments(!comments.isEnd())
                    Log.e("SUKA", "Comments loaded: " + comments.data.size)
                    comments.data.forEach {
                        Log.e("SUKA", "Comment: ${it.id}, ${it.authorNick}")
                    }
                    if (isFirstPage()) {
                        viewState.showComments(comments.data)
                    } else {
                        viewState.insertMoreComments(comments.data)
                    }
                }) { throwable ->
                    throwable.printStackTrace()
                }
                .addToDisposable()
    }

    private fun isFirstPage(): Boolean {
        return currentPageComment == START_PAGE
    }

    fun loadMoreComments() {
        loadComments(currentPageComment + 1)
    }

    fun onTorrentClick() {
        currentData?.torrentLink?.let {
            viewState.loadTorrent(it)
        }
    }

    fun onShareClick() {
        currentData?.link?.let {
            viewState.loadTorrent(it)
        }
    }

    fun onCopyLinkClick() {
        currentData?.link?.let {
            viewState.loadTorrent(it)
        }
    }

    fun onPlayAllClick() {
        currentData?.let {
            if (it.episodes.isEmpty()) {
                it.moonwalkLink?.let { viewState.playMoonwalk(it) }
            } else {
                viewState.playEpisodes(it)
            }
        }
    }

    fun onPlayEpisodeClick(episode: ReleaseFull.Episode, quality: Int) {
        currentData?.let {
            viewState.playEpisode(it, it.episodes.indexOf(episode), quality)
        }
    }

    fun onClickLink(url: String): Boolean {
        val matcher = Pattern.compile("\\/release\\/([\\s\\S]*?)\\.html").matcher(url)
        if (matcher.find()) {
            val args: Bundle = Bundle().apply {
                putString(ReleaseFragment.ARG_ID_NAME, matcher.group(1))
            }
            router.navigateTo(Screens.RELEASE_DETAILS, args)
            return true
        }
        return false
    }

    fun onClickDonate() {
        router.navigateTo(Screens.STATIC_PAGE, PageApi.PAGE_ID_DONATE)
    }

    fun onClickFav() {
        currentData?.favoriteCount?.let { fav ->
            if (fav.isGuest) {
                router.showSystemMessage("Для выполнения действия необходимо авторизоваться")
                return
            }
            releaseRepository
                    .sendFav(fav.id, !fav.isFaved, fav.sessId, fav.skey)
                    .doOnSubscribe {
                        fav.inProgress = true
                        viewState.updateFavCounter()
                    }
                    .doAfterTerminate {
                        fav.inProgress = false
                        viewState.updateFavCounter()
                    }
                    .subscribe({ newCount ->
                        fav.count = newCount
                        fav.isFaved = !fav.isFaved
                        viewState.updateFavCounter()
                    }) { throwable ->
                        throwable.printStackTrace()
                    }
                    .addToDisposable()
        }

    }

    fun openSearch(genre: String) {
        val args: Bundle = Bundle().apply {
            putString(SearchFragment.ARG_GENRE, genre)
        }
        router.navigateTo(Screens.RELEASES_SEARCH, args)
    }
}
