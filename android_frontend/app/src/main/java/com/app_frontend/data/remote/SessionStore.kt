package com.app_frontend.data.remote

import android.content.Context
import android.content.SharedPreferences

/**
 * Almacenamiento local de la sesión (token JWT + usuario).
 *
 * Se usa SharedPreferences para no añadir dependencias; cuando el backend Java
 * esté listo, el token vendrá de `POST /auth/login` y se guardará aquí para
 * adjuntarlo en cada petición (ver `ApiConfig.okHttpClient`).
 */
object SessionStore {

    private const val PREFS_NAME = "papel_tinta_session"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_LOGGED_IN = "logged_in"

    private var prefs: SharedPreferences? = null

    /** Debe llamarse una vez desde la Application/Activity antes de usar la sesión. */
    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    var token: String?
        get() = prefs?.getString(KEY_TOKEN, null)
        set(value) {
            prefs?.edit()?.putString(KEY_TOKEN, value)?.apply()
        }

    var isLoggedIn: Boolean
        get() = prefs?.getBoolean(KEY_LOGGED_IN, false) ?: false
        set(value) {
            prefs?.edit()?.putBoolean(KEY_LOGGED_IN, value)?.apply()
        }

    var userName: String
        get() = prefs?.getString(KEY_USER_NAME, null) ?: ""
        set(value) {
            prefs?.edit()?.putString(KEY_USER_NAME, value)?.apply()
        }

    var userEmail: String
        get() = prefs?.getString(KEY_USER_EMAIL, null) ?: ""
        set(value) {
            prefs?.edit()?.putString(KEY_USER_EMAIL, value)?.apply()
        }

    fun saveSession(token: String?, userId: String?, name: String, email: String) {
        prefs?.edit()
            ?.putString(KEY_TOKEN, token)
            ?.putString(KEY_USER_ID, userId)
            ?.putString(KEY_USER_NAME, name)
            ?.putString(KEY_USER_EMAIL, email)
            ?.putBoolean(KEY_LOGGED_IN, true)
            ?.apply()
    }

    fun clear() {
        prefs?.edit()?.clear()?.apply()
    }
}
