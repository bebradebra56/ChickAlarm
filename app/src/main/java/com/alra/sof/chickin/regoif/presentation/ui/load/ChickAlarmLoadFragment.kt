package com.alra.sof.chickin.regoif.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.alra.sof.chickin.MainActivity
import com.alra.sof.chickin.R
import com.alra.sof.chickin.databinding.FragmentLoadChickAlarmBinding
import com.alra.sof.chickin.regoif.data.shar.ChickAlarmSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChickAlarmLoadFragment : Fragment(R.layout.fragment_load_chick_alarm) {
    private lateinit var chickAlarmLoadBinding: FragmentLoadChickAlarmBinding

    private val chickAlarmLoadViewModel by viewModel<ChickAlarmLoadViewModel>()

    private val chickAlarmSharedPreference by inject<ChickAlarmSharedPreference>()

    private var chickAlarmUrl = ""

    private val chickAlarmRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            chickAlarmNavigateToSuccess(chickAlarmUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                chickAlarmSharedPreference.chickAlarmNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                chickAlarmNavigateToSuccess(chickAlarmUrl)
            } else {
                chickAlarmNavigateToSuccess(chickAlarmUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chickAlarmLoadBinding = FragmentLoadChickAlarmBinding.bind(view)

        chickAlarmLoadBinding.chickAlarmGrandButton.setOnClickListener {
            val chickAlarmPermission = Manifest.permission.POST_NOTIFICATIONS
            chickAlarmRequestNotificationPermission.launch(chickAlarmPermission)
            chickAlarmSharedPreference.chickAlarmNotificationRequestedBefore = true
        }

        chickAlarmLoadBinding.chickAlarmSkipButton.setOnClickListener {
            chickAlarmSharedPreference.chickAlarmNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            chickAlarmNavigateToSuccess(chickAlarmUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chickAlarmLoadViewModel.chickAlarmHomeScreenState.collect {
                    when (it) {
                        is ChickAlarmLoadViewModel.ChickAlarmHomeScreenState.ChickAlarmLoading -> {

                        }

                        is ChickAlarmLoadViewModel.ChickAlarmHomeScreenState.ChickAlarmError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is ChickAlarmLoadViewModel.ChickAlarmHomeScreenState.ChickAlarmSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val chickAlarmPermission = Manifest.permission.POST_NOTIFICATIONS
                                val chickAlarmPermissionRequestedBefore = chickAlarmSharedPreference.chickAlarmNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), chickAlarmPermission) == PackageManager.PERMISSION_GRANTED) {
                                    chickAlarmNavigateToSuccess(it.data)
                                } else if (!chickAlarmPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > chickAlarmSharedPreference.chickAlarmNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    chickAlarmLoadBinding.chickAlarmNotiGroup.visibility = View.VISIBLE
                                    chickAlarmLoadBinding.chickAlarmLoadingGroup.visibility = View.GONE
                                    chickAlarmUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(chickAlarmPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > chickAlarmSharedPreference.chickAlarmNotificationRequest) {
                                        chickAlarmLoadBinding.chickAlarmNotiGroup.visibility = View.VISIBLE
                                        chickAlarmLoadBinding.chickAlarmLoadingGroup.visibility = View.GONE
                                        chickAlarmUrl = it.data
                                    } else {
                                        chickAlarmNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    chickAlarmNavigateToSuccess(it.data)
                                }
                            } else {
                                chickAlarmNavigateToSuccess(it.data)
                            }
                        }

                        ChickAlarmLoadViewModel.ChickAlarmHomeScreenState.ChickAlarmNotInternet -> {
                            chickAlarmLoadBinding.chickAlarmStateGroup.visibility = View.VISIBLE
                            chickAlarmLoadBinding.chickAlarmLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun chickAlarmNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_chickAlarmLoadFragment_to_chickAlarmV,
            bundleOf(CHICK_ALARM_D to data)
        )
    }

    companion object {
        const val CHICK_ALARM_D = "chickAlarmData"
    }
}