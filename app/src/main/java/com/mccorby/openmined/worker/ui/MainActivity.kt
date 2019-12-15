package com.mccorby.openmined.worker.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.work.WorkManager
import com.mccorby.openmined.worker.BuildConfig
import com.mccorby.openmined.worker.OpenMinedApplication
import com.mccorby.openmined.worker.R
import org.openmined.worker.datasource.SyftWebSocketDataSource
import kotlinx.android.synthetic.main.activity_main.*
import org.openmined.worker.domain.MLFramework
import org.openmined.worker.domain.SyftRepository
import org.openmined.worker.domain.SyftResult
import org.openmined.worker.domain.usecase.ConnectUseCase
import org.openmined.worker.domain.usecase.DeleteObjectUseCase
import org.openmined.worker.domain.usecase.ExecuteCommandUseCase
import org.openmined.worker.domain.usecase.GetObjectUseCase
import org.openmined.worker.domain.usecase.ObserveMessagesUseCase
import org.openmined.worker.domain.usecase.SetObjectUseCase
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mlFramework: MLFramework

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        // Make Dagger instantiate @Inject fields in LoginActivity
        (applicationContext as OpenMinedApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_initiate.setOnClickListener { viewModel.initiateCommunication() }

        injectDependencies()
    }

    // TODO Inject using Kodein or another DI framework
    private fun injectDependencies() {
        val clientId = "Android-${System.currentTimeMillis()}"
        val webSocketUrl = BuildConfig.websocketUrl
        val syftDataSource =
            org.openmined.worker.datasource.SyftWebSocketDataSource(webSocketUrl, clientId)
        val syftRepository = SyftRepository(syftDataSource)
        val setObjectUseCase = SetObjectUseCase(syftRepository)
        val executeCommandUseCase = ExecuteCommandUseCase(syftRepository, mlFramework)
        val getObjectUseCase = GetObjectUseCase(syftRepository)
        val deleteObjectUseCase = DeleteObjectUseCase(syftRepository)
        val observeMessagesUseCase = ObserveMessagesUseCase(
            syftRepository,
            setObjectUseCase,
            executeCommandUseCase,
            getObjectUseCase,
            deleteObjectUseCase
        )
        val connectUseCase = ConnectUseCase(syftRepository)

        viewModel = ViewModelProviders.of(
            this,
            MainViewModelFactory(
                observeMessagesUseCase,
                connectUseCase,
                syftRepository,
                WorkManager.getInstance()
            )
        ).get(MainViewModel::class.java)

        viewModel.syftMessageState.observe(this, Observer<SyftResult> {
            log_area.append(it.toString() + "\n")
        })
        // TODO This should map to a presentation object instead of a concrete type of a framework
//        viewModel.syftTensorState.observe(this, Observer<SyftOperand.SyftTensor> {
//            log_area.append(it!!.toINDArray().toString() + "\n")
//        })
        viewModel.viewState.observe(this, Observer {
            log_area.append(it + "\n")
        })
    }
}
