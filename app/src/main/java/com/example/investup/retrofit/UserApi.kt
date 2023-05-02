package com.example.investup.retrofit

import com.example.investup.retrofit.dataClass.*
import com.example.investup.retrofit.dataClass.Tag
import com.example.investup.retrofit.requestModel.UserAuthRequest
import com.example.investup.retrofit.requestModel.UserChangeNameRequest
import com.example.investup.retrofit.requestModel.UserChangePasswordRequest
import com.example.investup.retrofit.requestModel.UserRegisterRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    @POST("auth/login")
    suspend fun requestAuth(@Body userAuthRequest: UserAuthRequest): Response<UserToken>

    @POST("auth/registration")
    suspend fun requestRegister(@Body userRegisterRequest: UserRegisterRequest): Response<UserToken>

    @Headers("Content-Type: application/json")
    @GET("users/me")
    suspend fun requestInfoMe(@Header("Authorization") accessToken: String): Response<User>

    @Headers("Content-Type: application/json")
    @GET("tags")
    suspend fun requestTags(@Header("Authorization") accessToken: String): Response<List<Tag>>

    @Headers("Content-Type: application/json")
    @POST("/users/change/info")
    suspend fun requestChangeNameAndLastName(
        @Header("Authorization") accessToken: String,
        @Body userChangeNameRequest: UserChangeNameRequest
    ) : Response<Void>

    @POST("/users/change/password")
    suspend fun requestChangePassword(
        @Header("Authorization") accessToken: String,
        @Body userChangeNameRequest: UserChangePasswordRequest
    ) : Response<Void>

    @Multipart
    @POST("/users/avatar")
    suspend fun uploadFile(@Header("Authorization") accessToken: String,
        @Part photo: MultipartBody.Part
    ): Response<ResponseBody>
}

