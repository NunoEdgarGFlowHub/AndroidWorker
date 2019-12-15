package com.mccorby.openmined.worker.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.mccorby.openmined.worker.services.DisconnectWorkManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.openmined.worker.domain.SyftCommand
import org.openmined.worker.domain.SyftOperand
import org.openmined.worker.domain.SyftRepository
import org.openmined.worker.domain.SyftResult
import org.openmined.worker.domain.usecase.ConnectUseCase
import org.openmined.worker.domain.usecase.ObserveMessagesUseCase

class MainViewModel(
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val connectUseCase: ConnectUseCase,
    private val syftRepository: SyftRepository,
    private val workManager: WorkManager
) : ViewModel() {

    val syftMessageState = MutableLiveData<SyftResult>()
    val syftTensorState = MutableLiveData<SyftOperand.SyftTensor>()
    val viewState = MutableLiveData<String>()

    private val compositeDisposable = CompositeDisposable()

    fun initiateCommunication() {
        val connectDisposable = connectUseCase.execute()
            .doOnError {
                viewState.postValue("Error connecting to socket.\n" +
                        "Did you set websocket_url in local_connection.properties file?\n" +
                        "Check also that the websocket is up and running")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        compositeDisposable.add(connectDisposable)

        // TODO Ideally we should start listening to message when connectUseCase completes
        startListeningToMessages()
    }

    private fun startListeningToMessages() {
        val messageDisposable = observeMessagesUseCase()
            .map { processNewMessage(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        val statusDisposable = syftRepository.onStatusChange()
            .map { viewState.postValue(it) }
            .doOnError {
                viewState.postValue("Error connecting to socket.\n" +
                        "Did you set websocket_url in local_connection.properties file?\n" +
                        "Check also that the websocket is up and running")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        compositeDisposable.addAll(messageDisposable, statusDisposable)
    }

    private fun processNewMessage(syftResult: SyftResult) {
        Log.d("MainActivity", "Received new SyftMessage at $syftResult")
        when (syftResult) {
            is SyftResult.ObjectAdded -> {
                syftTensorState.postValue(syftResult.syftObject as SyftOperand.SyftTensor)
            }
            is SyftResult.CommandResult -> {
                processCommand(syftResult)
            }
            is SyftResult.ObjectRetrieved -> {
                viewState.postValue("Server requested tensor with id ${syftResult.syftObject.id}")
            }
            is SyftResult.ObjectRemoved -> {
                viewState.postValue("Tensor with id ${syftResult.pointer} deleted")
            }
            else -> {
                syftMessageState.postValue(syftResult)
            }
        }
    }

    private fun processCommand(commandResult: SyftResult.CommandResult) {
        when (commandResult.command) {
            is SyftCommand.Add -> {
                viewState.postValue("Result of Add:")
                syftTensorState.postValue(commandResult.commandResult)
            }
        }
    }

    public override fun onCleared() {
        compositeDisposable.clear()
        workManager.enqueue(OneTimeWorkRequest.from(DisconnectWorkManager::class.java))
        super.onCleared()
    }
}
