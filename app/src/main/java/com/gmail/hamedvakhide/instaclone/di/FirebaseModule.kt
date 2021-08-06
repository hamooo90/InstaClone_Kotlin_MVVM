package com.gmail.hamedvakhide.instaclone.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseDB() = FirebaseDatabase.getInstance("https://instaclone-365b0-default-rtdb.europe-west1.firebasedatabase.app")

    @Provides
    @Singleton
    fun provideFirebaseStorage() = FirebaseStorage.getInstance()
}