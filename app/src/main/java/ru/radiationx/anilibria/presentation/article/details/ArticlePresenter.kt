package ru.radiationx.anilibria.presentation.article.details

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.radiationx.anilibria.entity.app.article.ArticleItem
import ru.radiationx.anilibria.entity.app.vital.VitalItem
import ru.radiationx.anilibria.model.repository.ArticleRepository
import ru.radiationx.anilibria.model.repository.VitalRepository
import ru.radiationx.anilibria.utils.mvp.BasePresenter
import ru.terrakok.cicerone.Router

/**
 * Created by radiationx on 20.12.17.
 */
@InjectViewState
class ArticlePresenter(
        private val articleRepository: ArticleRepository,
        private val vitalRepository: VitalRepository,
        private val router: Router
) : BasePresenter<ArticleView>(router) {

    companion object {
        private const val START_PAGE = 1
    }

    private var currentPageComment = START_PAGE
    private var articleId = -1
    var code: String = ""

    fun setDataFromItem(item: ArticleItem) {
        item.run {
            viewState.preShow(imageUrl, title, userNick, commentsCount, viewsCount)
        }
        code = item.code
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.e("SUKA", "onFirstViewAttach " + this)
        loadArticle(code)
        loadVital()
    }

    private fun loadVital() {
        vitalRepository
                .observeByRule(VitalItem.Rule.ARTICLE_DETAIL)
                .subscribe {
                    router.showSystemMessage("Show vital in ART_DETAIL: ${it.size}")
                }
                .addToDisposable()
    }

    fun loadArticle(code: String) {
        Log.e("SUKA", "load article $code")
        viewState.setRefreshing(true)
        articleRepository.getArticle(code)
                .doAfterTerminate { viewState.setRefreshing(false) }
                .subscribe({ article ->
                    articleId = article.id
                    viewState.showArticle(article)
                    loadComments(currentPageComment)
                }) { throwable ->
                    throwable.printStackTrace()
                }
                .addToDisposable()
    }

    private fun loadComments(page: Int) {
        currentPageComment = page
        articleRepository
                .getComments(articleId, currentPageComment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
}
