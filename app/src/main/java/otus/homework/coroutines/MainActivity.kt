package otus.homework.coroutines

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import otus.homework.coroutines.remote.DiContainer

class MainActivity : AppCompatActivity() {

    private val diContainer = DiContainer()

    private val viewModel: CatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = layoutInflater.inflate(R.layout.activity_main, null) as CatsView
        setContentView(view)

        val nameObserver = Observer<CatViewModel.Result> { data ->
            when (data) {
                is CatViewModel.Result.Success -> {
                    findViewById<TextView>(R.id.fact_textView).text = data.catData.fact.text
                    Picasso.get().load(data.catData.image.file)
                        .into(findViewById<ImageView>(R.id.image_imageView))
                }
                is CatViewModel.Result.Error -> {
                    Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }

        }

        viewModel.currentName.observe(this, nameObserver)

        findViewById<Button>(R.id.button).setOnClickListener {
            viewModel.getData(diContainer.service)
        }

    }

    override fun onResume() {
        viewModel.getData(diContainer.service)
        super.onResume()
    }
}