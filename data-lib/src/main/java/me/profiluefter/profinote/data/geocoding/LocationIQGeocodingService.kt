package me.profiluefter.profinote.data.geocoding

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

class LocationIQGeocodingService @Inject constructor(
    private val api: LocationIQAPI,
    @Named("locationIQ")
    private val key: String
) : GeocodingService {
    override suspend fun reverse(latitude: Double, longitude: Double): String =
        api.reverse(latitude, longitude, key).displayName
}

interface LocationIQAPI {
    @GET("v1/reverse.php")
    suspend fun reverse(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("key") key: String,
        @Query("format") format: String = "json"
    ): ReverseResponse
}

data class ReverseResponse(
    @SerializedName("display_name")
    val displayName: String
)

@Module
@InstallIn(SingletonComponent::class)
object LocationIQModule {
    @Provides
    @Singleton
    fun locationIQAPIClient(gson: Gson, client: OkHttpClient): LocationIQAPI = Retrofit.Builder()
        .baseUrl("https://eu1.locationiq.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create()
}