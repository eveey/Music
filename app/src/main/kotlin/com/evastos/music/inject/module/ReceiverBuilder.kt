package com.evastos.music.inject.module

import com.evastos.music.data.network.connectivity.NetworkConnectivityReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ReceiverBuilder {

    @ContributesAndroidInjector
    internal abstract fun bindNetworkConnectivityReceiver(): NetworkConnectivityReceiver
}
