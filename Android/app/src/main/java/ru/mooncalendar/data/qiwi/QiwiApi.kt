package ru.mooncalendar.data.qiwi

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.mooncalendar.data.qiwi.model.InvoicingBody
import ru.mooncalendar.data.qiwi.model.InvoicingResponse

interface QiwiApi {

    @PUT("/partner/bill/v1/bills/{billId}")
    suspend fun invoicing(
        @Path("billId") billId: String,
        @Body body: InvoicingBody
    ): Response<InvoicingResponse>

    @GET("/partner/bill/v1/bills/{billId}")
    suspend fun getStatus(@Path("billId") billId: String): InvoicingResponse
}

val retrofit = Retrofit.Builder()
    .baseUrl("https://api.qiwi.com")
    .addConverterFactory(GsonConverterFactory.create())
    .client(OkHttpClient.Builder()
        .addInterceptor {
            val request = it.request().newBuilder()
                .addHeader("Authorization","Bearer $KEY")
                .build()

            it.proceed(request)
        }.build()
    ).build().create<QiwiApi>()

private const val KEY = "eyJ2ZXJzaW9uIjoiUDJQIiwiZGF0YSI6eyJwYXlpbl9tZXJjaGFudF9zaXRlX3VpZCI6Im5kc3JoNS0wMCIsInVzZXJfaWQiOiI3NzA3MzAwOTI5MyIsInNlY3JldCI6ImJjZjQ3MjgxNTkxZTNjOTcxNGQyNTU2NmIwZDM4NGNiYTgzOWRjNWFhMDRmZjY1MmVmMjQ5YzZkNjAxMzk3YmMifX0="