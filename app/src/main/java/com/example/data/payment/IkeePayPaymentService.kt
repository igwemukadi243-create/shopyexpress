package com.example.data.payment

import com.example.data.model.PaymentMethod
import kotlinx.coroutines.delay
import java.util.UUID

/**
 * Service gérant l'intégration de la passerelle de paiement iKeePay pour la République Démocratique du Congo.
 * Permet l'encaissement unifié de Vodacom M-Pesa, Airtel Money, Orange Money et Cartes Bancaires.
 */
object IkeePayPaymentService {

    const val GATEWAY_NAME = "iKeePay RDC"
    const val GATEWAY_URL = "https://api.ikeepay.com/v1"
    const val MERCHANT_NAME = "ShopyExpress RDC"

    data class IkeePayTransaction(
        val reference: String,
        val amountUsd: Double,
        val amountCdf: Long,
        val paymentMethod: PaymentMethod,
        val customerPhone: String,
        val status: Status,
        val providerMessage: String
    ) {
        enum class Status {
            INITIALIZING,
            WAITING_FOR_USSD_PIN,
            SUCCESS,
            FAILED
        }
    }

    /**
     * Génère une référence unique de transaction selon la convention iKeePay
     */
    fun generateTransactionRef(): String {
        val randomDigits = (100000..999999).random()
        return "IKP-RDC-$randomDigits"
    }

    /**
     * Valide et formate un numéro de téléphone congolais (+243...)
     */
    fun formatDrcPhone(phone: String): String {
        val clean = phone.replace(" ", "").replace("-", "")
        return when {
            clean.startsWith("+243") -> clean
            clean.startsWith("243") -> "+$clean"
            clean.startsWith("0") -> "+243${clean.drop(1)}"
            else -> "+243$clean"
        }
    }

    /**
     * Détermine l'opérateur en fonction du préfixe congolais
     */
    fun detectOperator(phone: String): String {
        val clean = phone.replace(" ", "").replace("-", "").replace("+243", "")
        return when {
            clean.startsWith("81") || clean.startsWith("82") || clean.startsWith("83") -> "Vodacom M-Pesa"
            clean.startsWith("97") || clean.startsWith("98") || clean.startsWith("99") -> "Airtel Money"
            clean.startsWith("84") || clean.startsWith("85") || clean.startsWith("89") -> "Orange Money"
            clean.startsWith("90") -> "Africell / Afrimoney"
            else -> "Mobile Money RDC"
        }
    }

    /**
     * Simule l'appel API à l'infrastructure iKeePay pour déclencher le push USSD Mobile Money
     */
    suspend fun initiatePayment(
        amountUsd: Double,
        paymentMethod: PaymentMethod,
        customerPhone: String,
        onStatusUpdate: (IkeePayTransaction.Status, String) -> Unit
    ): IkeePayTransaction {
        val reference = generateTransactionRef()
        val amountCdf = (amountUsd * 2800).toLong()
        val formattedPhone = formatDrcPhone(customerPhone)
        val operator = if (paymentMethod == PaymentMethod.CARD) "Carte Bancaire 3D Secure" else detectOperator(customerPhone)

        // Étape 1 : Initialisation avec les serveurs iKeePay
        onStatusUpdate(
            IkeePayTransaction.Status.INITIALIZING,
            "Connexion sécurisée aux serveurs iKeePay RDC (Réf: $reference)..."
        )
        delay(700)

        // Étape 2 : Envoi du push USSD sur le téléphone du client
        if (paymentMethod != PaymentMethod.CARD) {
            onStatusUpdate(
                IkeePayTransaction.Status.WAITING_FOR_USSD_PIN,
                "Demande USSD envoyée vers $formattedPhone ($operator). Veuillez saisir votre code PIN secret..."
            )
        } else {
            onStatusUpdate(
                IkeePayTransaction.Status.WAITING_FOR_USSD_PIN,
                "Validation 3D Secure iKeePay en cours..."
            )
        }
        delay(1300)

        // Étape 3 : Succès & Confirmation par iKeePay
        onStatusUpdate(
            IkeePayTransaction.Status.SUCCESS,
            "Paiement de ${amountUsd.toInt()} $ (≈ $amountCdf FC) confirmé avec succès par iKeePay !"
        )

        return IkeePayTransaction(
            reference = reference,
            amountUsd = amountUsd,
            amountCdf = amountCdf,
            paymentMethod = paymentMethod,
            customerPhone = formattedPhone,
            status = IkeePayTransaction.Status.SUCCESS,
            providerMessage = "Transaction approuvée par $operator via iKeePay"
        )
    }
}
