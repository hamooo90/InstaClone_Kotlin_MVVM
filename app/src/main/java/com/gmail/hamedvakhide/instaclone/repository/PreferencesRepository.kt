package com.gmail.hamedvakhide.instaclone.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext context: Context
){

    private val pref =
        context.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
    private val prefEdit = pref.edit()

    fun saveProfileId(uid: String) {
        prefEdit.putString(PROFILE_ID, uid)
        prefEdit.apply()
    }
    fun saveBackUserId(uid: String) {
        prefEdit.putString(BACK_USER_ID, uid)
        prefEdit.apply()
    }
    fun savePosition(pos: Int) {
        prefEdit.putInt(POSITION, pos)
        prefEdit.apply()
    }

    fun readProfileId(): String{
        var id = ""
        if (pref != null) {
            id = pref.getString(PROFILE_ID, "").toString()
        }
        return id
    }
    fun readBackUserId(): String{
        var id = ""
        if (pref != null) {
            id = pref.getString(BACK_USER_ID, "").toString()
        }
        return id
    }
    fun readPosition(): Int{
        var pos = 0
        if (pref != null) {
            pos = pref.getInt(POSITION,0)
        }
        return pos
    }


    companion object {
        private const val PROFILE_ID = "profileId"
        private const val BACK_USER_ID = "userId"
        private const val POSITION = "position"
    }

}