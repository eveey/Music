package com.evastos.music.data.network.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NetworkConnectivityProviderTest {

    private val context = mock<Context>()
    private val connectivityManager = mock<ConnectivityManager>()
    private val networkInfo = mock<NetworkInfo>()

    private lateinit var networkConnectivityProvider: NetworkConnectivityProvider

    @Before
    fun setUp() {
        networkConnectivityProvider = NetworkConnectivityProvider(context)
        whenever(context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(connectivityManager)
        whenever(connectivityManager.activeNetworkInfo).thenReturn(networkInfo)
    }

    @Test
    fun isConnected_withConnectivityAction_whenConnected_returnsTrue() {
        whenever(networkInfo.isConnected).thenReturn(true)

        val isConnected = networkConnectivityProvider.isConnected()

        assertTrue(isConnected)
    }

    @Test
    fun isConnected_withConnectivityAction_whenNotConnected_returnsFalse() {
        whenever(networkInfo.isConnected).thenReturn(false)

        val isConnected = networkConnectivityProvider.isConnected()

        assertFalse(isConnected)
    }

    @Test
    fun isConnected_withConnectivityAction_withoutActiveNetworkInfo_returnsFalse() {
        whenever(connectivityManager.activeNetworkInfo).thenReturn(null)

        val isConnected = networkConnectivityProvider.isConnected()

        assertFalse(isConnected)
    }
}
