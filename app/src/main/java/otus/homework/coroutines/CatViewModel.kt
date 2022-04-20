package otus.homework.coroutines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import otus.homework.coroutines.remote.CatsService

class CatViewModel : ViewModel() {

    private lateinit var _catsService: CatsService
    private var _catsView: ICatsView? = null

    fun attachView(catsService: CatsService, catsView: ICatsView) {
        _catsService = catsService
        _catsView = catsView
    }

    fun detachView() {
        _catsView = null
    }

    fun getData(){
        viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            CrashMonitor.trackWarning()
        }) {
            val resDefFact = async { _catsService.getCatFact() }
            val resDefImage = async { _catsService.getCatImage("https://aws.random.cat/meow") }

            val responseFact = resDefFact.await()
            val responseImage = resDefImage.await()

            if (responseFact.isSuccessful) {
                _catsView?.populate(CatData(responseFact.body()!!, responseImage.body()!!))
            } else {
                CrashMonitor.trackWarning()
            }
        }
    }

    sealed class Result{
        class Success<T>: Result()
        class Error :Result()
    }
}