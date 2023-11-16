package com.strimm.application.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.res.Resources.getSystem
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

fun dpToPx(context: Context, dp: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (dp * scale).roundToInt()
}

fun Context.getApplicationVersion(): String {
    var version = ""
    try {
        val pInfo: PackageInfo =
            packageManager.getPackageInfo(packageName, 0)
        version = pInfo.versionName
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return version
}

fun Context.getApplicationVersionCode(): Long {
    var version = 1L
    try {
        val pInfo: PackageInfo =
            packageManager.getPackageInfo(packageName, 0)
        version = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pInfo.longVersionCode
        } else {
            pInfo.versionCode.toLong()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return version
}

fun Context.noNetworkConnection(): Boolean {
    val cm: ConnectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo: NetworkInfo? = cm.activeNetworkInfo
    return netInfo == null || !netInfo.isConnected
}

fun Context.showKeyboard() {
    val inputMethodManager: InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
}

fun Activity.getWindowHeight(): Int {
    // Calculate window height for fullscreen use
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}

fun Int.toPx(): Float = (this * getSystem().displayMetrics.density)


inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}

fun convertTimeFormat(time: String, fromFormat: String, toFormat: String): String {
    return if (time.isBlank()) "" else
        try {
            val sdf = SimpleDateFormat(fromFormat, Locale.getDefault())
            sdf.timeZone = TimeZone.getDefault()
            val dateObj = sdf.parse(time)
            println(dateObj)
            SimpleDateFormat(toFormat, Locale.getDefault()).format(
                dateObj ?: Date()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
}

//converts UTC time to local time with given format
fun convertTimeFormatFromUTC(time: String?, fromFormat: String, toFormat: String): String {
    return if (time.isNullOrBlank()) "" else
        try {
            val sdf = SimpleDateFormat(fromFormat, Locale.ENGLISH)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val dateObj = sdf.parse(time)
            SimpleDateFormat(toFormat, Locale.getDefault()).format(dateObj ?: Date())
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
}

// converts local time to UTC time with given format
fun convertTimeFormatToUTC(time: String, fromFormat: String, toFormat: String): String {
    return if (time.isBlank()) "" else
        try {
            val sdf = SimpleDateFormat(fromFormat, Locale.getDefault())
            val dateObj = sdf.parse(time)
            SimpleDateFormat(toFormat, Locale.ENGLISH).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())
                }
            }.format(dateObj ?: Date())
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
}

fun Calendar.getLastWeek(): Pair<String, String> {
    // Monday
    add(Calendar.DAY_OF_YEAR, -13)
    val mDateMonday = time

    // Sunday
    add(Calendar.DAY_OF_YEAR, 6)
    val mDateSunday = time

    // Date format
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val monday = sdf.format(mDateMonday)
    val sunday = sdf.format(mDateSunday)
    //return monday + " - " + sunday;
    return Pair(monday, sunday)
}

fun String.convertUTCTimeToMillis(): Long {
    if (isBlank()) return 0
    return try {
        val dateFormat = SimpleDateFormat(API_UTC_TIME_FORMAT, Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = dateFormat.parse(this) ?: Date()
        dateFormat.timeZone = TimeZone.getDefault()
        val millis = dateFormat.parse(dateFormat.format(date))?.time
        millis ?: 0
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

fun String.convertLocalTimeToMillis(format: String = "yyyy-MM-dd"): Long {
    if (isBlank()) return 0
    return try {
        val dateFormat = SimpleDateFormat(format, Locale.ENGLISH)
        val date = dateFormat.parse(this) ?: Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        val cal = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, cal.get(Calendar.MINUTE))
        calendar.timeInMillis
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

fun String.timeToMillis(time: String,format: String = "yyyy-MM-dd HH:mm:ss"): Long {
    if (isBlank()) return 0
    return try {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        val dateObj = sdf.parse(time)
        dateObj.time.milliseconds.inWholeMilliseconds
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

fun Calendar.getCurrentWeek(): Pair<String, String> {
    val date = Date()
    time = date
    // 1 = Sunday, 2 = Monday, etc.
    val dayOfWeek = this[Calendar.DAY_OF_WEEK]
    val mondayOffset: Int = if (dayOfWeek == 1) {
        -6
    } else 2 - dayOfWeek // need to minus back
    add(Calendar.DAY_OF_YEAR, mondayOffset)
    val mDateMonday = time

    // return 6 the next days of current day (object cal save current day)
    add(Calendar.DAY_OF_YEAR, 6)
    val mDateSunday = time

    //Get format date
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val monday = sdf.format(mDateMonday)
    val sunday = sdf.format(mDateSunday)
    //return monday + " - " + sunday;
    return Pair(monday, sunday)
}


fun Calendar.getLastMonth(): Pair<String, String> {
// add -1 month to current month
    add(Calendar.MONTH, -1)
// set DATE to 1, so first date of previous month
    this[Calendar.DATE] = 1
    val firstDateOfPreviousMonth = time
// set actual maximum date of previous month
    this[Calendar.DATE] = getActualMaximum(Calendar.DAY_OF_MONTH)
//read it
    val lastDateOfPreviousMonth = time
    return Pair(
        firstDateOfPreviousMonth.format("yyyy-MM-dd"),
        lastDateOfPreviousMonth.format("yyyy-MM-dd")
    )
}

fun getCurrentDayOfWeek(): String {
    val c = Calendar.getInstance()
    val dayOfWeek = c[Calendar.DAY_OF_WEEK]
    return if (Calendar.MONDAY == dayOfWeek) {
        "Mon"
    } else if (Calendar.TUESDAY == dayOfWeek) {
        "Tue"
    } else if (Calendar.WEDNESDAY == dayOfWeek) {
        "Wed"
    } else if (Calendar.THURSDAY == dayOfWeek) {
        "Thu"
    } else if (Calendar.FRIDAY == dayOfWeek) {
        "Fri"
    } else if (Calendar.SATURDAY == dayOfWeek) {
        "Sat"
    } else if (Calendar.SUNDAY == dayOfWeek) {
        "Sun"
    } else {
        ""
    }
}

fun Calendar.yesterday(): Date {
    add(Calendar.DATE, -1)
    return time
}

fun Date.format(pattern: String, locale: Locale = Locale.getDefault()): String {
    return SimpleDateFormat(pattern, locale).format(this)
}

fun Uri.getRealPathFromURI(
    context: Context
): String? {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = context.contentResolver.query(this, proj, null, null, null)
        cursor!!.moveToFirst()
        val columnIndex = cursor.getColumnIndex(proj.firstOrNull() ?: "")
        cursor.getString(columnIndex)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    } finally {
        cursor?.close()
    }
}


fun <T> T?.toJsonObject(): JSONObject {
    var jsonObj = JSONObject()
    try {

        val gson = Gson()
        val jsonStr = if (this == null) "null" else gson.toJson(this)
        jsonObj = JSONObject(jsonStr)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return jsonObj
}

fun <T : List<Any>> T?.toJsonArray(): JSONArray {
    var jsonObj = JSONArray()
    try {

        val gson = Gson()
        val jsonStr = if (this == null) "null" else gson.toJson(this)
        jsonObj = JSONArray(jsonStr)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return jsonObj
}