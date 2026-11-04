package com.policyboss.policybosspro.utils

object Constant {
    const val TAG = "PolicyBossPro"
    const val token = "1234567890"
    const val base_url = "https://horizon.policyboss.com:5443/"

    // WorkManager Input / Output Keys
    const val KEY_fbaid = "fbaid"
    const val KEY_ssid = "ssid"
    const val KEY_parentid = "parentid"
    const val KEY_deviceid = "device_id"
    const val KEY_appversion = "appversion"
    const val KEY_result = "result"
    const val KEY_error_result = "error_result"

    // Progress Keys
    const val CONTACT_LOG_Progress = "CONTACT_LOGGProgress"
    const val CONTACT_LOG_MAXProgress = "CONTACT_LOGMAXProgress"
    const val CALL_LOG_Progress = "CALL_LOGProgress"
    const val CALL_LOG_MAXProgress = "CALL_LOGMAXProgress"

    // WorkManager Tags
    const val TAG_SAVING_CONTACT_LOG = "SAVING_CONTACT_LOG"
    const val TAG_SAVING_CALL_LOG = "SAVING_CALL_LOG"

    // Status Strings
    const val CONTACT_LOG_DataFetching = "Data is Fetching Please Wait..."
    const val CONTACT_LOG_DataSending = "Sending Data to Server..."

    // Notification Keys
    const val NOTIFICATION_EXTRA = "notification_extra"
    const val NOTIFICATION_PROGRESS = "notification_progress"
    const val NOTIFICATION_MAX = "notification_max"
    const val NOTIFICATION_MESSAGE = "notification_message"

    // PolicyBoss Web Links
    const val POLICYBOSS_HOME_URL = "https://www.policyboss.com/"
    const val POLICYBOSS_HOME_TITLE = "PolicyBoss"
    const val FAQ_URL = "https://www.policyboss.com/faq"
    const val FAQ_TITLE = "Frequently Asked Questions"
    const val TERMS_URL = "https://www.policyboss.com/terms-and-conditions"
    const val TERMS_TITLE = "Terms & Conditions"
}
