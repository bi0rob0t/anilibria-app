package ru.radiationx.anilibria.screen.update

import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.leanback.app.ProgressBarManager
import androidx.transition.Fade
import androidx.transition.TransitionManager
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.radiationx.anilibria.R
import ru.radiationx.anilibria.common.GradientBackgroundManager
import ru.radiationx.anilibria.databinding.FragmentUpdateBinding
import ru.radiationx.anilibria.di.DownloadModule
import ru.radiationx.shared.ktx.android.subscribeTo
import ru.radiationx.shared_app.common.download.DownloadControllerImpl
import ru.radiationx.shared_app.di.viewModel
import ru.radiationx.shared_app.screen.ScopedFragment
import javax.inject.Inject

class UpdateFragment : ScopedFragment(R.layout.fragment_update) {

    private val binding by viewBinding<FragmentUpdateBinding>()

    private val progressBarManager by lazy { ProgressBarManager() }

    @Inject
    lateinit var backgroundManager: GradientBackgroundManager

    @Inject
    lateinit var downloadController: DownloadControllerImpl

    private val viewModel by viewModel<UpdateViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        dependencyInjector.installModules(DownloadModule())
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
        lifecycle.addObserver(downloadController)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backgroundManager.clearGradient()
        progressBarManager.setRootView(binding.updateRoot)

        subscribeTo(viewModel.updateData) {
            val string = StringBuilder().apply {
                appendParam("Версия", it.name.orEmpty())
                appendParam("Дата", it.date.orEmpty())
                appendln("<br>")
                appendSection("Важно", it.important)
                appendSection("Добавлено", it.added)
                appendSection("Исправлено", it.fixed)
                appendSection("Изменено", it.changed)
            }
            binding.updateDescription.text = Html.fromHtml(string.toString())
        }

        subscribeTo(viewModel.downloadActionTitle) {
            binding.updateButton.text = it
        }

        subscribeTo(viewModel.downloadProgressShowState) {
            TransitionManager.beginDelayedTransition(view as ViewGroup)
            binding.progressBar.isVisible = it
            binding.progressText.isVisible = it
        }

        subscribeTo(viewModel.downloadProgressData) {
            binding.progressBar.isIndeterminate = it == 0
            binding.progressBar.progress = it
            binding.progressText.text = "$it%"
        }

        subscribeTo(viewModel.progressState) {
            if (it) {
                progressBarManager.show()
            } else {
                progressBarManager.hide()
                binding.updateButton.requestFocus()
                TransitionManager.beginDelayedTransition(binding.updateRoot, Fade())
            }
            binding.updateContainer.isVisible = !it
        }

        binding.updateButton.setOnClickListener {
            viewModel.onActionClick()
        }
    }

    private fun StringBuilder.appendParam(title: String, value: String) {
        append("<b>$title:</b> $value<br>")
    }

    private fun StringBuilder.appendSection(title: String, changes: List<String>) {
        if (changes.isEmpty()) {
            return
        }
        append("<b>$title</b><br>")
        changes.forEachIndexed { index, s ->
            append("— ").append(s)
            if (index + 1 < changes.size) {
                append("<br>")
            }
        }
        append("<br>")
    }

}