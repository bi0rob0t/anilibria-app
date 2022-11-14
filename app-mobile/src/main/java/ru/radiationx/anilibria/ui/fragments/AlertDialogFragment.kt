package ru.radiationx.anilibria.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog

open class AlertDialogFragment(@LayoutRes layoutRes: Int) :
    CustomMvpAppCompatDialogFragment(layoutRes) {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext()).show()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}