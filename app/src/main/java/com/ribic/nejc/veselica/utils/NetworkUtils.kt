package com.ribic.nejc.veselica.utils


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.util.Log

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.Scanner

object NetworkUtils {
    private val API_KEY = "appid=b587472c-5fe5-4a16-83ae-626aa4aad33e"
    private val TAG = NetworkUtils::class.java.simpleName
    private val COUNTRY_PARTIES_MAIN = "http://api.nejcribic.com/veselica/myApi.php?"
    private val COUNTRY_PARTIES_URL = COUNTRY_PARTIES_MAIN + "getAll/"


    private val format = "json"

    val urlAll: String
        get() = COUNTRY_PARTIES_URL + "&" + API_KEY

    fun getUrlPlace(place: String): String {
        return String.format("%s/place/%s&%s", COUNTRY_PARTIES_URL, place, API_KEY)
    }

    fun getUrlDate(date: String): String {
        return String.format("%s/date/%s&%s", COUNTRY_PARTIES_URL, date, API_KEY)
    }

    fun getUrlDay(day: String): String {
        return String.format("%s/day/%s&%s", COUNTRY_PARTIES_URL, day, API_KEY)
    }

    fun getUrlMoreInfo(href: String): String {
        return String.format("%smoreInfo%s&%s", COUNTRY_PARTIES_MAIN, href, API_KEY)
    }


    fun buildUrl(): URL {
        val builtUri = Uri.parse(COUNTRY_PARTIES_URL)

        var url: URL? = null
        try {
            url = URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        Log.v(TAG, "Built URI " + url!!)
        return url
    }

    @Throws(IOException::class)
    fun getResponseFromHttpUrl(url: URL): String? {
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            val inputStream = urlConnection.inputStream
            val scanner = Scanner(inputStream)
            scanner.useDelimiter("\\A")
            if (scanner.hasNext()) {
                return scanner.next()
            } else
                return null
        } finally {
            urlConnection.disconnect()
        }
    }

    fun networkUp(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }


}
