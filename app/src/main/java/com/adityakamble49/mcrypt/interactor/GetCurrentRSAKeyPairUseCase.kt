package com.adityakamble49.mcrypt.interactor

import com.adityakamble49.mcrypt.cache.PreferenceHelper
import com.adityakamble49.mcrypt.cache.db.RSAKeyPairRepo
import com.adityakamble49.mcrypt.cache.exception.RSAKeyPairNotFoundException
import com.adityakamble49.mcrypt.model.RSAKeyPair
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Build RSAKeyPair UseCase
 *
 * @author Aditya Kamble
 * @since 18/12/2017
 */
class GetCurrentRSAKeyPairUseCase @Inject constructor(
        private val rsaKeyPairRepo: RSAKeyPairRepo,
        private val preferenceHelper: PreferenceHelper) {

    private fun buildUseCaseObservable(): Observable<RSAKeyPair> {
        return Observable.create(object : ObservableOnSubscribe<RSAKeyPair> {
            override fun subscribe(e: ObservableEmitter<RSAKeyPair>) {
                val currentRSAKeyPairId = preferenceHelper.currentRSAKeyId
                val rsaKeyPair = rsaKeyPairRepo.getRSAKeyPairById(currentRSAKeyPairId)
                if (rsaKeyPair != null) {
                    e.onNext(rsaKeyPair)
                    e.onComplete()
                } else {
                    e.onError(RSAKeyPairNotFoundException(
                            "RSA Key not found with id $currentRSAKeyPairId"))
                }
            }
        })
    }

    fun execute(): Observable<RSAKeyPair> {
        return buildUseCaseObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}