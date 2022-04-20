package otus.homework.coroutines

import kotlinx.coroutines.*
import java.net.SocketTimeoutException
import kotlin.coroutines.CoroutineContext

class CatsPresenter(
    private val catsService: CatsService
) {

    private var _catsView: ICatsView? = null
    private val scope = PresenterScope()
    private lateinit var job: Job

    fun onInitComplete() {
        job = scope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
            CrashMonitor.trackWarning()
            _catsView?.showToast(throwable.message ?: "Exception")
        }) {
            try {
                val resDefFact = async { catsService.getCatFact() }
                val resDefImage = async { catsService.getCatImage("https://aws.random.cat/meow") }

                val responseFact = resDefFact.await()
                val responseImage = resDefImage.await()

                if (responseFact.isSuccessful) {
                    _catsView?.populate(CatData(responseFact.body()!!, responseImage.body()!!))
                } else {
                    CrashMonitor.trackWarning()
                }

            } catch (e: Exception) {
                when (e) {
                    is SocketTimeoutException -> _catsView?.showToast("Не удалось получить ответ от сервера")
                    else -> _catsView?.showToast(e.message ?: "Exception")
                }

            }

        }
    }

    fun attachView(catsView: ICatsView) {
        _catsView = catsView
    }

    fun detachView() {
        _catsView = null
        job.cancel()
    }

    class PresenterScope : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = SupervisorJob() + Dispatchers.Main + CoroutineName("CatsCoroutine")
    }
}