package com.knowitall.customer.ui.activity.login

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.knowitall.customer.AppConstant
import com.knowitall.customer.ui.activity.main.MainActivity
import com.knowitall.customer.R
import com.knowitall.customer.base.BaseActivity
import com.knowitall.customer.common.SharedPrefsConstant
import com.knowitall.customer.data.model.UserDetailResponse
import com.knowitall.customer.data.model.UserLoginLog
import com.knowitall.customer.databinding.ActivityLoginBinding
import com.knowitall.customer.ui.activity.profile.ProfileActivity
import com.knowitall.customer.utils.get
import com.knowitall.customer.utils.put
import com.mukeshsolanki.OnOtpCompletionListener
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.util.concurrent.TimeUnit
import kotlin.collections.set


class LoginActivity : BaseActivity(), LoginPresenterCallback, OnOtpCompletionListener {

    private lateinit var binding: ActivityLoginBinding
    private val TAG: String? = LoginActivity::class.simpleName
    private val RC_SIGN_IN: Int = 111
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var auth: FirebaseAuth
    private lateinit var edtType: String
    private lateinit var value: String
    private lateinit var storedVerificationId: String
    private lateinit var presenter: LoginPresenter
    private lateinit var firestoreDB: FirebaseFirestore
    private lateinit var phoneNumberUtil: PhoneNumberUtil

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
            binding.otpView.setText(credential.smsCode.toString())
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                Log.e(TAG, "onVerificationFailed: invalid credentials", e)
            } else if (e is FirebaseTooManyRequestsException) {
                Log.e(TAG, "onVerificationFailed: SMS Quota expired", e)
            }
            hideProgress()
            Snackbar.make(binding.btnResendOTP, "Invalid OTP", Snackbar.LENGTH_SHORT).show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            hideProgress()
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")
            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token

            showPhoneNumberLayout(false)
            Snackbar.make(binding.btnResendOTP, "OTP Sent", Snackbar.LENGTH_SHORT).show()
            startCounter()
        }
    }

    private fun showPhoneNumberLayout(show: Boolean) {
        binding.phoneText.visibility = if (show) View.VISIBLE else View.GONE
        binding.mobileNoLayout.visibility = if (show) View.VISIBLE else View.GONE
        binding.signin.visibility = if (show) View.VISIBLE else View.GONE

        binding.btnVerify.visibility = if (!show) View.VISIBLE else View.GONE
        binding.btnResendOTP.visibility = if (!show) View.VISIBLE else View.GONE
        binding.frameLayout.visibility = if (!show) View.VISIBLE else View.GONE

        binding.loginText.text = if (show) getString(R.string.login_text) else getString(
            R.string.otp_text,
            AppConstant.COUNTRY_CODE + binding.phoneNo.text
        )

    }

    fun startCounter() {
        object : CountDownTimer(60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.txtTimer.text = "00:" + String.format("%02d", millisUntilFinished / 1000)
            }

            override fun onFinish() {
                binding.txtTimer.text = ""
            }
        }.start()
    }

    fun sendOTP(phoneNo: String) {
        value = phoneNo
        showProgress()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            value, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks
        ) // OnVerificationStateChangedCallbacks
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestoreDB = Firebase.firestore
        presenter = LoginPresenter(this, firestoreDB)

        phoneNumberUtil = PhoneNumberUtil.createInstance(this)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.btnVerify.isEnabled = false
        binding.btnVerify.setBackgroundColor(
            ContextCompat.getColor(
                this@LoginActivity,
                R.color.disabled
            )
        )

        auth = FirebaseAuth.getInstance()

        binding.otpView.setOtpCompletionListener(this)
        binding.otpView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count < 6) {
                    binding.btnVerify.isEnabled = false
                    binding.btnVerify.setBackgroundColor(
                        ContextCompat.getColor(
                            this@LoginActivity,
                            R.color.disabled
                        )
                    )
                }
            }
        })
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = task.result.user

                    task.result.user?.getIdToken(true)?.addOnSuccessListener { result ->
                        sharedPreferences.put(
                            SharedPrefsConstant.AUTH_TOKEN,
                            "Bearer ${result.token}"
                        )
                        Log.d(TAG, "GetTokenResult result = ${result.token}")

                        presenter.getUserDetail()
                    }
                    // Save user log for logging in
                    val userLoginLog =
                        UserLoginLog(user!!.uid, "android", System.currentTimeMillis())
//                    presenter.createUserLog(userLoginLog)
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Snackbar.make(binding.otpView, "Invalid OTP.", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            }
    }

    private fun getAuthToken(task: Task<AuthResult>) {
        var fbUser = auth.currentUser
        auth.currentUser?.getIdToken(true)?.addOnSuccessListener { result ->
            sharedPreferences.put(SharedPrefsConstant.AUTH_TOKEN, "Bearer ${result.token}")
            Log.d(TAG, "GetTokenResult result = ${result.token}")
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if (sharedPreferences.getString(SharedPrefsConstant.USER_ID, "")!!.length > 2) {
            val currentUser = auth.currentUser
            gotoMainActivity(currentUser)
        }
    }

    private fun gotoMainActivity(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            sharedPreferences.put(SharedPrefsConstant.IS_FIRST_TIME, false)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            ActivityCompat.startActivity(this, intent, null)
            finish()
        }
    }

    fun signIn(view: View) {
        hideSoftKeyboard()
        if (validatePhoneOrEmail()) {
            val params = hashMapOf(
                "type" to edtType,
                "value" to binding.phoneNo.text.toString()
            )
            if (edtType == "phone") {
                params["value"] = AppConstant.COUNTRY_CODE + params["value"]
                sendOTP(AppConstant.COUNTRY_CODE + binding.phoneNo.text.toString())
            }
        }
    }

    fun resesndOTP(view: View) {
        showProgress()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            value,        // Phone number to verify
            60,                 // Timeout duration
            TimeUnit.SECONDS,   // Unit of timeout
            this,               // Activity (for callback binding)
            callbacks,         // OnVerificationStateChangedCallbacks
            resendToken
        )
    }

    fun verifyOTP(view: View) {
        hideSoftKeyboard()
        showProgress()
        signInWithPhoneAuthCredential(
            PhoneAuthProvider.getCredential(
                storedVerificationId,
                binding.otpView.text.toString()
            )
        )
    }

    private fun validatePhoneOrEmail(): Boolean {
        binding.txtError.text = ""
        if (TextUtils.isEmpty(binding.phoneNo.text)) {
            binding.phoneNo.error = getString(R.string.invalid_email_phone)
            binding.txtError.text = getString(R.string.invalid_email_phone)
            return false
        }

        if (Patterns.EMAIL_ADDRESS.matcher(binding.phoneNo.text).matches()) {
            edtType = "email"
            return true
        }

        if (binding.phoneNo.text.length == 10 && Patterns.PHONE.matcher(binding.phoneNo.text)
                .matches()
        ) {
            try {
                val phoneNumber = phoneNumberUtil.parse(
                    AppConstant.COUNTRY_CODE + binding.phoneNo.text.toString(),
                    "IN"
                )
                if (phoneNumberUtil.isValidNumber(phoneNumber)) {
                    edtType = "phone"
                    return true
                }
            } catch (error: NumberParseException) {
                binding.phoneNo.error = getString(R.string.invalid_phone)
                binding.txtError.text = getString(R.string.invalid_phone)
                return false
            }
        }

        binding.phoneNo.error = getString(R.string.invalid_email_phone)
        binding.txtError.text = getString(R.string.invalid_email_phone)
        return false
    }

    override fun onPause() {
        super.onPause()
        presenter.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dispose()
    }

    override fun showProgress() {
        binding.progressBar.progressBarWrapper.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        binding.progressBar.progressBarWrapper.visibility = View.GONE
    }

    override fun onOtpCompleted(otp: String?) {
        binding.btnVerify.isEnabled = true
        binding.btnVerify.setBackgroundColor(
            ContextCompat.getColor(
                this@LoginActivity,
                R.color.black
            )
        )
    }

    override fun onUserDetailError(error: Throwable) {
        hideProgress()
        Snackbar.make(
            binding.loginImg,
            getString(R.string.something_went_wrong),
            Snackbar.LENGTH_SHORT
        ).show()
        Log.d(TAG, "onUserDetailError: " + error.message)
    }

    override fun onUserDetailsFatched(userDetailResponse: UserDetailResponse) {
        sharedPreferences.put(SharedPrefsConstant.USER_PHONE, binding.phoneNo.text.toString())
        sharedPreferences.put(SharedPrefsConstant.USER_COUNTRY_CODE, AppConstant.COUNTRY_CODE)
        hideProgress()
        if (userDetailResponse.status) {
            sharedPreferences.put(
                SharedPrefsConstant.USER_ID,
                userDetailResponse.payload.customerId
            )
            sharedPreferences.put(SharedPrefsConstant.USER_NAME, userDetailResponse.payload.name)
            sharedPreferences.put(SharedPrefsConstant.USER_EMAIL, userDetailResponse.payload.email)
            sharedPreferences.put(
                SharedPrefsConstant.USER_PHONE,
                userDetailResponse.payload.phoneNumber
            )
            sharedPreferences.put(
                SharedPrefsConstant.USER_FULL_PHONE,
                userDetailResponse.payload.fullPhoneNumber
            )
            sharedPreferences.put(
                SharedPrefsConstant.USER_COUNTRY_CODE,
                userDetailResponse.payload.countryCode
            )
            openActivity()
        } else {
            if (userDetailResponse.payload.code == 101) {
                openActivity()
            }
            Snackbar.make(
                binding.loginImg,
                userDetailResponse.payload.message ?: "Unauthorised access",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun openActivity() {
        if (sharedPreferences.get(SharedPrefsConstant.AUTH_TOKEN, "")
                .isNotEmpty() && sharedPreferences.get(SharedPrefsConstant.USER_NAME, "")
                .isNotEmpty()
        ) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            ActivityCompat.startActivity(this, intent, null)
            finish()
        } else {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            ActivityCompat.startActivity(this, intent, null)
            finish()
        }
    }
}