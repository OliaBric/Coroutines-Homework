package otus.homework.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import otus.homework.coroutines.remote.DiContainer

class MainActivity : AppCompatActivity() {

//    lateinit var catsPresenter: CatsPresenter

    private val diContainer = DiContainer()

    val viewModel: CatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = layoutInflater.inflate(R.layout.activity_main, null) as CatsView
        setContentView(view)

//        catsPresenter = CatsPresenter(diContainer.service)
//        view.presenter = catsPresenter
//        catsPresenter.attachView(view)

        viewModel.attachView(diContainer.service, view)

    }

    override fun onResume() {
//        catsPresenter.onInitComplete()
        viewModel.getData()
        super.onResume()
    }

    override fun onStop() {
        if (isFinishing) {
//            catsPresenter.detachView()
            viewModel.detachView()
        }
        super.onStop()
    }
}