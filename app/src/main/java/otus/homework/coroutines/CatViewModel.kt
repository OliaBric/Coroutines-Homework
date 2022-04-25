package otus.homework.coroutines

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import otus.homework.coroutines.remote.CatsService

class CatViewModel : ViewModel() {

    private lateinit var _catsService: CatsService

    val currentName: MutableLiveData<Result> by lazy {
        MutableLiveData<Result>()
    }

    fun getData(catsService: CatsService) {
        _catsService = catsService
        viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            CrashMonitor.trackWarning()
        }) {
            val resDefFact = async { _catsService.getCatFact() }
            val resDefImage = async { _catsService.getCatImage("https://aws.random.cat/meow") }

            val responseFact = resDefFact.await()
            val responseImage = resDefImage.await()

            if (responseFact.isSuccessful) {
                currentName.value =
                    Result.Success(CatData(responseFact.body()!!, responseImage.body()!!))
            } else {
                currentName.value = Result.Error("Не удалось получить ответ от сервера")
                CrashMonitor.trackWarning()
            }
        }
    }

    sealed class Result {
        class Success(val catData: CatData) : Result()
        class Error(val message: String) : Result()
    }
}