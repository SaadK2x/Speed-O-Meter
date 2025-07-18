package gps.navigation.speedmeter.utils

import android.app.Activity
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.utils.Constants.APP_UPDATE_REQUEST_CODE

private fun getUpdateType(): Int {
    return AppUpdateType.FLEXIBLE
}

fun Activity.checkForAppUpdates(appUpdateManager: AppUpdateManager) {
    val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo

    appUpdateInfoTask.addOnSuccessListener { info ->
        val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
        val isUpdateAllowed = when (getUpdateType()) {
            AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
            AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
            else -> false
        }

        if (isUpdateAvailable && isUpdateAllowed) {
            requestAppUpdate(appUpdateManager, info, getUpdateType())
        }
    }
}

private fun Activity.requestAppUpdate(
    appUpdateManager: AppUpdateManager,
    appUpdateInfo: AppUpdateInfo,
    updateType: Int
) {
    try {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            this,
            AppUpdateOptions.newBuilder(updateType).build(),
            APP_UPDATE_REQUEST_CODE
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Activity.setupAppUpdateListeners(
    appUpdateManager: AppUpdateManager,
) {
    val updateType = getUpdateType()

    when (updateType) {
        AppUpdateType.FLEXIBLE -> setupFlexibleUpdateSuccessListener(appUpdateManager)
        AppUpdateType.IMMEDIATE -> setupImmediateUpdateSuccessListener(appUpdateManager)
    }

    if (updateType == AppUpdateType.FLEXIBLE) {
        appUpdateManager.registerListener(getInstallStateUpdateListener(appUpdateManager))
    }
}

private fun Activity.setupImmediateUpdateSuccessListener(appUpdateManager: AppUpdateManager) {
    appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
            requestAppUpdate(appUpdateManager, appUpdateInfo, AppUpdateType.IMMEDIATE)
        }
    }
}

private fun Activity.setupFlexibleUpdateSuccessListener(appUpdateManager: AppUpdateManager) {
    appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
        if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
            showInstallSnackBar(appUpdateManager)
        }
    }
}

fun Activity.unregisterAppUpdateListeners(appUpdateManager: AppUpdateManager) {
    if (getUpdateType() == AppUpdateType.FLEXIBLE) {
        appUpdateManager.unregisterListener(getInstallStateUpdateListener(appUpdateManager))
    }
}

private fun Activity.getInstallStateUpdateListener(appUpdateManager: AppUpdateManager) =
    InstallStateUpdatedListener {
        if (it.installStatus() == InstallStatus.DOWNLOADED) {
            this.showInstallSnackBar(appUpdateManager)
        }
    }

fun Activity.showInstallSnackBar(appUpdateManager: AppUpdateManager) {
    Snackbar.make(
        findViewById(android.R.id.content),
        getString(R.string.str_download_complete),
        Snackbar.LENGTH_INDEFINITE
    ).apply {
        setAction(getString(R.string.str_restart)) {
            appUpdateManager.completeUpdate()
        }
        show()
    }
}