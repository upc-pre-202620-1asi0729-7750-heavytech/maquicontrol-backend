package com.andesheavytech.maquicontrol.platform.rental.domain;

/** Lifecycle of a reservation. Rejected is kept distinct from cancelled for auditability. */
public enum RentalStatus { REQUESTED, CONFIRMED, REJECTED, IN_PROGRESS, COMPLETED, CANCELLED }
