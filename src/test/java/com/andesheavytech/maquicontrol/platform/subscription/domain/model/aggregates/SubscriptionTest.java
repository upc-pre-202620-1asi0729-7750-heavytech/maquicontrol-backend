package com.andesheavytech.maquicontrol.platform.subscription.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities.Payment;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.PaymentProviderName;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.PaymentStatus;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.SubscriptionStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubscriptionTest {

    @Test
    void subscribeCreatesSubscriptionPendingPayment() {
        var sub = Subscription.subscribe(10L, 1L);
        assertEquals(10L, sub.getOwnerProfileId());
        assertEquals(1L, sub.getPlanId());
        assertEquals(SubscriptionStatus.PENDING_PAYMENT, sub.getStatus());
        assertTrue(sub.isPendingPayment());
        assertFalse(sub.isActive());
    }

    @Test
    void activateTransitionsFromPendingToActive() {
        var sub = Subscription.subscribe(10L, 1L);
        sub.activate();

        assertEquals(SubscriptionStatus.ACTIVE, sub.getStatus());
        assertTrue(sub.isActive());
        assertNotNull(sub.getStartedAt());
        assertNotNull(sub.getCurrentPeriodEnd());
    }

    @Test
    void activateFailsIfNotPendingPayment() {
        var sub = Subscription.subscribe(10L, 1L);
        sub.activate();
        assertThrows(IllegalStateException.class, sub::activate);
    }

    @Test
    void cancelTransitionsFromActiveToCancelled() {
        var sub = Subscription.subscribe(10L, 1L);
        sub.activate();
        sub.cancel("Switching to another provider");

        assertEquals(SubscriptionStatus.CANCELLED, sub.getStatus());
        assertEquals("Switching to another provider", sub.getCancellationReason());
        assertFalse(sub.isActive());
    }

    @Test
    void cancelFailsIfNotActive() {
        var sub = Subscription.subscribe(10L, 1L);
        assertThrows(IllegalStateException.class, () -> sub.cancel("Reason"));
    }

    @Test
    void confirmPaymentAppendsPaymentAndAllowsActivation() {
        var sub = Subscription.subscribe(10L, 1L);
        var payment = Payment.of(1L, PaymentProviderName.MERCADO_PAGO, "MP-12345",
                new Money(new BigDecimal("149.00"), "PEN"), PaymentStatus.APPROVED);

        sub.confirmPayment(payment);
        assertEquals(1, sub.getPayments().size());
        assertTrue(sub.getPayments().get(0).isApproved());

        sub.activate();
        assertTrue(sub.isActive());
    }
}
