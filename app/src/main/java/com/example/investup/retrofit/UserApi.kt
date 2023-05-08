package com.example.investup.retrofit

import com.example.investup.retrofit.dataClass.*
import com.example.investup.retrofit.dataClass.Tag
import com.example.investup.retrofit.requestModel.UserAuthRequest
import com.example.investup.retrofit.requestModel.UserChangeNameRequest
import com.example.investup.retrofit.requestModel.UserChangePasswordRequest
import com.example.investup.retrofit.requestModel.UserRegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    @GET("/posts")
    suspend fun requestAllPosts(@Header("Authorization") accessToken: String): Response<ArrayList<Post>>

    @Headers("Content-Type: application/json")
    @GET("/posts/favorites")
    suspend fun requestFavoritePosts(@Header("Authorization") accessToken: String): Response<ArrayList<Post>>

    @Headers("Content-Type: application/json")
    @GET("/posts/me")
    suspend fun requestMyPosts(@Header("Authorization") accessToken: String): Response<ArrayList<Post>>
    @Headers("Content-Type: application/json")
    @GET("/posts/{id}")
    suspend fun requestPostById(@Path("id") id :String,  @Header("Authorization") accessToken: String): Response<Post>

    @Headers("Content-Type: application/json")
    @DELETE("/posts/{id}")
    suspend fun deletePostById(@Path("id") id :String,  @Header("Authorization") accessToken: String): Response<ResponseBody>


    @Headers("Content-Type: application/json")
    @POST("/posts/favorite/{id}")
    suspend fun addToFavoritePostById(@Path("id") id :String,  @Header("Authorization") accessToken: String): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("tags")
    suspend fun requestTags(@Header("Authorization") accessToken: String): Response<ArrayList<Tag>>

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
    ) : Response<RequestBody>

    @Multipart
    @POST("/users/avatar")
    suspend fun uploadFile(@Header("Authorization") accessToken: String,
        @Part photo: MultipartBody.Part
    ): Response<ResponseBody>

    @Multipart
    @POST("/posts/create")
    suspend fun uploadPost(@Header("Authorization") accessToken: String,
                           @Part("title") title: RequestBody,
                           @Part("description") description: RequestBody,
                           @Part("shortDescription") shortDescription: RequestBody,
                           @Part("tags") tags: RequestBody,
                           @Part video: MultipartBody.Part?): Response<ResponseBody>


}

