package com.andesheavytech.maquicontrol.platform.subscription.application.internal.outboundservices.acl;

import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities.Payment;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.PaymentProviderName;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Anti-corruption layer towards Mercado Pago Checkout Pro (sandbox mode).
 */
@Service
@Slf4j
public class MercadoPagoService {

    /**
     * Creates a payment preference and returns the simulated checkout redirect URL.
     */
    public String createPreference(Long subscriptionId, String planName, Money price) {
        log.info("Creating Mercado Pago preference for subscription {} ({}) for {}", subscriptionId, planName, price);
        return "https://sandbox.mercadopago.com/checkout/v1/redirect?pref_id=" + UUID.randomUUID();
    }

    /**
     * Verifies or confirms a payment transaction returned from the checkout.
     */
    public Payment processPayment(Long subscriptionId, String providerPaymentId, Money amount, PaymentStatus status) {
        String paymentId = (providerPaymentId == null || providerPaymentId.isBlank())
                ? "MP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
                : providerPaymentId.trim();

        PaymentStatus effectiveStatus = status != null ? status : PaymentStatus.APPROVED;
        log.info("Processing Mercado Pago payment {} for subscription {} with status {}", paymentId, subscriptionId, effectiveStatus);
        return Payment.of(subscriptionId, PaymentProviderName.MERCADO_PAGO, paymentId, amount, effectiveStatus);
    }
}
