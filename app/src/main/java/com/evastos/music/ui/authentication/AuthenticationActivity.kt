package com.evastos.music.ui.authentication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import com.evastos.music.R
import com.evastos.music.ui.base.BaseActivity
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.activity_authentication.activity_main
import kotlinx.android.synthetic.main.activity_authentication.code_text_view
import kotlinx.android.synthetic.main.activity_authentication.response_text_view
import kotlinx.android.synthetic.main.activity_authentication.token_text_view
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class AuthenticationActivity : BaseActivity() {

    val CLIENT_ID = "cbec5663d6be4e91b13f0de53eaa084c"
    val AUTH_TOKEN_REQUEST_CODE = 0x10
    val AUTH_CODE_REQUEST_CODE = 0x11

    private val mOkHttpClient = OkHttpClient()
    private var mAccessToken: String? = null
    private var mAccessCode: String? = null
    private var mCall: Call? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
    }

    override fun onDestroy() {
        cancelCall()
        super.onDestroy()
    }

    fun onGetUserProfileClicked(view: View) {
        if (mAccessToken == null) {
            val snackbar = Snackbar.make(activity_main, R.string.warning_need_token, Snackbar.LENGTH_SHORT)
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.accent))
            snackbar.show()
            return
        }

        val request = Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken!!)
                .build()

        cancelCall()
        mCall = mOkHttpClient.newCall(request)

        mCall!!.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                setResponse("Failed to fetch data: $e")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    val jsonObject = JSONObject(response.body()!!.string())
                    setResponse(jsonObject.toString(3))
                } catch (e: JSONException) {
                    setResponse("Failed to parse data: $e")
                }

            }
        })
    }

    fun onRequestCodeClicked(view: View) {
        val request = getAuthenticationRequest(AuthenticationResponse.Type.CODE)
        AuthenticationClient.openLoginActivity(this, AUTH_CODE_REQUEST_CODE, request)
    }

    fun onRequestTokenClicked(view: View) {
        val request = getAuthenticationRequest(AuthenticationResponse.Type.TOKEN)
        AuthenticationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request)
    }

    private fun getAuthenticationRequest(type: AuthenticationResponse.Type): AuthenticationRequest {
        return AuthenticationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(arrayOf("user-read-email"))
                .setCampaign("your-campaign-token")
                .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val response = AuthenticationClient.getResponse(resultCode, data)

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken()
            updateTokenView()
        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode()
            updateCodeView()
        }
    }

    private fun setResponse(text: String) {
        runOnUiThread {
            response_text_view.text = text
        }
    }

    private fun updateTokenView() {
        token_text_view.text = getString(R.string.token, mAccessToken)
    }

    private fun updateCodeView() {
        code_text_view.text = getString(R.string.code, mAccessCode)
    }

    private fun cancelCall() {
        if (mCall != null) {
            mCall!!.cancel()
        }
    }

    private fun getRedirectUri(): Uri {
        return Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build()
    }
}
