package com.example.travelplanner.DataClasses


data class EmailVerify(
    val email: String
)
//
//data class EmailVerifyResponse(
//    val `data`: Data1,
//    val message: String,
//    val success: Boolean
//)
//
//data class Data1(
//    val is_registered: Boolean,
//    val is_verified: Boolean
//)

data class Register(
    val confirm_password: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val password: String
)

data class SignUpResponse(
    val `data`: Data2,
    val message: String,
    val success: Boolean
)

data class Data2(
    val email: String,
    val is_registered: Boolean,
    val is_verified: Boolean
)

data class Registrationotp(
    val email: String,
    val otp: String,
    val verification_type: String
)

data class Registrationotpresponse(
    val `data`: Data3,
    val message: String,
    val success: Boolean
)

data class Data3(
    val access: String,
    val email: String,
    val is_registered: Boolean,
    val is_verified: Boolean,
    val refresh: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val data : Data4,
    val message: String,
    val success: Boolean
)

data class Data4(
    val access: String,
    val email: String,
    val is_registered: Boolean,
    val is_verified: Boolean,
    val refresh: String
)

data class ForgotPasswordEmailVerifyDataClass(
    val `data`: Data5,
    val message: String,
    val success: Boolean
)

data class Data5(
    val email: String
)

data class ForgotPasswordOtpVerify(
    val email: String,
    val otp: String,
    val verification_type: String
)

data class ForgotPasswordOtpResponse(
    val `data`: Data6,
    val message: String,
    val success: Boolean
)

data class Data6(
    val email: String,
    val reset_token: String
)

data class ResetPasswordRequest(
    val confirm_password: String,
    val email: String,
    val new_password: String,
    val reset_token: String
)

data class ResetPasswordResponse(
    val message: String,
    val success: Boolean
)

data class googleOauthResponse(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val scope: String,
    val token_type: String,
    val user: User
)

data class User(
    val email: String,
    val first_name: String,
    val last_name: String
)