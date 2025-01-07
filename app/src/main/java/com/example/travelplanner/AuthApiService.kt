package com.example.travelplanner

import com.example.travelplanner.DataClasses.EmailVerify
import com.example.travelplanner.DataClasses.ForgotPasswordEmailVerifyDataClass
import com.example.travelplanner.DataClasses.ForgotPasswordOtpResponse
import com.example.travelplanner.DataClasses.ForgotPasswordOtpVerify
import com.example.travelplanner.DataClasses.LoginRequest
import com.example.travelplanner.DataClasses.LoginResponse
import com.example.travelplanner.DataClasses.Register
import com.example.travelplanner.DataClasses.Registrationotp
import com.example.travelplanner.DataClasses.Registrationotpresponse
import com.example.travelplanner.DataClasses.ResetPasswordRequest
import com.example.travelplanner.DataClasses.ResetPasswordResponse
import com.example.travelplanner.DataClasses.SignUpResponse
import com.example.travelplanner.DataClasses.googleOauthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApiService {

//    @POST("check-email/")
//    suspend fun verifyEmail(@Body emailVerify: EmailVerify): EmailVerifyResponse

    @POST("register/")
    suspend fun register(@Body register: Register): SignUpResponse

    @POST("verify-otp/")
    suspend fun restrationOtp(@Body registerotp: Registrationotp): Registrationotpresponse

    @POST("login/")
    suspend fun login(@Body login: LoginRequest): LoginResponse

    @POST("forgot-password/")
    suspend fun verifyEmailForgot(@Body emailVerifyForgot: EmailVerify): ForgotPasswordEmailVerifyDataClass

    @POST("verify-otp/")
    suspend fun resetpasswordOtp(@Body resetpasswordotp: ForgotPasswordOtpVerify): ForgotPasswordOtpResponse

    @POST("reset-password/")
    suspend fun resetpassword(@Body resetpassword: ResetPasswordRequest): ResetPasswordResponse

    @POST("convert-token/")
    @FormUrlEncoded
    suspend fun convertToken(
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: String,
        @Field("backend") backend: String,
        @Field("token") token: String
    ): googleOauthResponse
}