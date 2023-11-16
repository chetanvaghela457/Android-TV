package com.strimm.application.di

import com.strimm.application.HomeActivity
import com.strimm.application.SplashActivity
import com.strimm.application.ui.activity.LoginActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface ApplicationComponent {

    fun injectHome(mainActivity: HomeActivity)
    fun injectLogin(mainActivity: LoginActivity)
    fun injectSplash(mainActivity: SplashActivity)
}