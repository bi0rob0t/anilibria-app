package ru.radiationx.anilibria.ui.activities.auth

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_container.*
import ru.radiationx.anilibria.App
import ru.radiationx.anilibria.R
import ru.radiationx.anilibria.Screens
import ru.radiationx.anilibria.ui.activities.main.MainActivity
import ru.radiationx.anilibria.ui.common.RouterProvider
import ru.radiationx.anilibria.ui.fragments.auth.AuthFragment
import ru.radiationx.anilibria.ui.fragments.auth.AuthSocialFragment
import ru.radiationx.anilibria.utils.DimensionHelper
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator


/**
 * Created by radiationx on 30.12.17.
 */
class AuthActivity : AppCompatActivity(), RouterProvider {

    override val router: Router = App.navigation.root.router
    private val navigationHolder = App.navigation.root.holder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        router.newRootScreen(Screens.AUTH)

        val dimensionHelper = DimensionHelper(view_for_measure, root_content, object : DimensionHelper.DimensionsListener {
            override fun onDimensionsChange(dimensions: DimensionHelper.Dimensions) {
                view_for_measure.post {
                    root_container.setPadding(root_container.paddingLeft,
                            root_container.paddingTop,
                            root_container.paddingRight,
                            dimensions.keyboardHeight)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        navigationHolder.setNavigator(navigatorNew)
    }

    override fun onPause() {
        super.onPause()
        navigationHolder.removeNavigator()
    }

    private val navigatorNew = object : SupportAppNavigator(this, R.id.root_container) {
        override fun createActivityIntent(screenKey: String?, data: Any?): Intent? {
            return when (screenKey) {
                Screens.MAIN -> Intent(this@AuthActivity, MainActivity::class.java)
                else -> null
            }
        }

        override fun createFragment(screenKey: String?, data: Any?): Fragment? {
            return when (screenKey) {
                Screens.AUTH -> AuthFragment()
                Screens.AUTH_SOCIAL -> AuthSocialFragment().apply {
                    arguments = Bundle().apply {
                        putString(AuthSocialFragment.ARG_SOCIAL_URL, data as String)
                    }
                }
                else -> null
            }
        }
    }
}
