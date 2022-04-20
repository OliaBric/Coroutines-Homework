package otus.homework.coroutines

import otus.homework.coroutines.model.CatImage
import otus.homework.coroutines.model.Fact
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface CatsService {

    @GET("random?animal_type=cat")
    suspend fun getCatFact(): Response<Fact>

    @GET
    suspend fun getCatImage(@Url path:String):Response<CatImage>
}